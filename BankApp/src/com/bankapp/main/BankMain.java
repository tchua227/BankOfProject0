package com.bankapp.main;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.bankapp.bo.UserBO;
import com.bankapp.bo.impl.UserBOImpl;
import com.bankapp.exception.BankException;
import com.bankapp.to.Account;
import com.bankapp.to.Transaction;
import com.bankapp.to.Transfer;
import com.bankapp.to.User;

public class BankMain {
	private static Logger log = Logger.getLogger(BankMain.class);
	
	private static void printEmployeeOptions() {
		log.info("Employee Options:");
		log.info("\t1. Account approval and rejection");
		log.info("\t2. View a customer's bank accounts");
		log.info("\t3. Log of transactions");
		log.info("\t4. Back to user options");
	}
	
	private static void printCustomerOptions() {
		log.info("Customer options:");
		log.info("\t1. Apply for a new account");
		log.info("\t2. View an account's balance");
		log.info("\t3. Withdraw from an account");
		log.info("\t4. Deposit to an account");
		log.info("\t5. Transfer center");
		log.info("\t6. Back to user options");
	}
	
	private static int printOptions(User user) {
		log.info("User Options: ");
		int count = 1;
		if (user.getIsCustomer() == 0) {
			log.info("\t" + count + ". Register for a customer account");
			++count;
		}
		if (user.getIsCustomer()==1) {
			log.info("\t" + count+". Customer options");
			++count;
		}
		if (user.getIsEmployee() == 1) {
			log.info("\t" + count+". Employee options");
			++count;
		}
		log.info("\t" + count+". Quit");
		return count;
	}
	
	static User newUser(Scanner scanner) {
		User user = null;
		UserBO bo = new UserBOImpl();
		log.info("Create a username: ");
		String username = scanner.nextLine();
		log.info("Create a password: ");
		String password = scanner.nextLine();
		try {
			log.info("Are you a customer? Enter 1 for yes or 0 for no.");
			int iscustomer = Integer.parseInt(scanner.nextLine());
			log.info("Are you an employee? Enter 1 for yes or 0 for no.");
			int isemployee = Integer.parseInt(scanner.nextLine());
			if (!(iscustomer == 1) && !(iscustomer == 0))
				throw new BankException("Please enter either 1 or 0.");
			if (!(isemployee == 1) && !(isemployee == 0))
				throw new BankException("Please enter either 1 or 0.");
			if ((iscustomer == 0) && (isemployee == 0))
				throw new BankException("Cannot make a user that is neither employee or customer.");
			user = new User();
			user.setIsCustomer(iscustomer);
			user.setIsEmployee(isemployee);
			user.setPassword(password);
			user.setUsername(username);
			user.setUser_id(bo.newUser(username, password, iscustomer, isemployee));
			if (user.getUser_id() == null)
				throw new BankException("Error creating account");
		}
		catch (NumberFormatException e) {
			log.error("Please enter either 1 or 0.");
		}
		catch (BankException e) {
			log.error(e.getMessage());
			return null;
		}
		
		return user;
	}
	
	static User login(Scanner scanner) {
		User user = null;
		log.info("Please enter your username, 'new' to create a new user account, 'quit' to exit the application: ");
		String username = scanner.nextLine();
		UserBO bo = new UserBOImpl();
		if (username.equals("quit"))
			return null;
		else if (username.equals("new")) {
			user = newUser(scanner);
			return user;
		}
		user = bo.getUser(username);
		if (user != null) {
			log.info("Please enter your password or 'quit' to exit the application: ");
			String password = scanner.nextLine();
			if (password.equals(user.getPassword())) {
				log.info("Welcome, " + username);
			}
			else if (password.equals("quit")) {
				log.info("Exiting application");
				return null;
			}
			else {
				log.info("Incorrect password. Session terminated.");
				return null;
			}
		}
		return user;
	}
	
	private static Account getAccount(Map<String, Account> am, String str, Scanner scanner) {
		if (am == null || am.isEmpty())
			return null;
		Account account = null;
		log.info(str);
		for (String a : am.keySet()) {
			log.info("\t"+a+": "+am.get(a).getName());
		}
		String accID = scanner.nextLine();
		while(!accID.equals("quit")) {

			if (!am.containsKey(accID)) {
				log.error("Account ID not found. Please enter the name of an account listed.\n");
			}
			else {
				return am.get(accID);
			}
			log.info(str);
			for (String a : am.keySet()) {
				log.info("\t"+a+": "+am.get(a).getName());
			}
			accID = scanner.nextLine();
		}
		return account;
	}
	
	private static boolean transferCode(User user, Scanner scanner) {
		log.info("Transfer options: ");
		log.info("\t1. Post a transfer to another account");
		log.info("\t2. View and approve incoming transfers");
		log.info("\t3. Return to customer options");
		try {
			int choice = Integer.parseInt(scanner.nextLine());
			UserBO bo = new UserBOImpl();
			switch(choice) {
				case 1:
					try {
						Map<String, Account> accountMap = bo.getAllCustomerAccounts(user.getUser_id());
						Account a1 = getAccount(accountMap, "Please enter the ID of the account you wish to transfer from or type 'quit' to return to transfer options: ", scanner);
						if (a1 == null)
							break;
						Map<String, Account> allAccounts = bo.getAllAccounts();
						if (allAccounts == null)
							break;
						log.info("\nPlease enter the ID of the account you wish to transfer to or 'quit' to return to transfer options: ");
						log.info("\tAccount ID\tAccount Name\tUser ID");
						for (String a : allAccounts.keySet()) {
							//log.info("\t"+a+": "+allAccounts.get(a).getName() + "\tUser ID: " + allAccounts.get(a).getUser_id());
							String strformat = "\t%10s\t%10s\t%10s";
							log.info(String.format(strformat, allAccounts.get(a).getAid(), allAccounts.get(a).getName(), allAccounts.get(a).getUser_id()));
						}
						String acc2ID = scanner.nextLine();
						while (!acc2ID.equals("quit")) {
							if (!allAccounts.containsKey(acc2ID)) {
								log.error("Account ID not found. Please enter the ID of an account listed.\n");
							}
							else {
								Account a2 = allAccounts.get(acc2ID);
								log.info("Please enter the amount you wish to transfer from Account "+a1.getAid() +" to Account "+acc2ID+": ");
								log.info("Account "+a1.getAid() +" balance: " +a1.getBalance());
								int transferAmount = Integer.parseInt(scanner.nextLine());
								if (bo.transfer(transferAmount, a1, a2)) {
									log.info("Transfer posted and sent to customer " + a2.getUser_id() +" for approval.");
									break;
								}
								else {
									log.error("Internal error occurred. Please contact support.\n");
								}
							}
							log.info("\nPlease enter the ID of the account you wish to transfer to or 'quit' to return to account 1 selection: ");
							for (String a : allAccounts.keySet()) {
								log.info("\t"+a+": "+allAccounts.get(a).getName() + "\tUser ID: " + allAccounts.get(a).getUser_id() + "\t\tBalance: "+allAccounts.get(a).getBalance());
							}
							acc2ID = scanner.nextLine();
						}
					}
					catch(BankException e) {
						log.error(e.getMessage());
					}
					break;
				case 2:
						Map<String, Transfer> transferMap = bo.getAllTransfers(user.getUser_id());
						if (transferMap == null || transferMap.isEmpty())
							break;
						log.info("Displaying all pending transfers to your accounts: ");
						log.info("\tTransfer ID\tTransfer Amount\t\tTransferring User");
						for (String t : transferMap.keySet()) {
							//log.info("\t"+transferMap.get(t).getTid()+"\t"+transferMap.get(t).getAmount()+"\t"+transferMap.get(t).getUsername());
							String strformat = "\t%10s\t%10d\t%10s";
							log.info(String.format(strformat, transferMap.get(t).getTid(), transferMap.get(t).getAmount(), transferMap.get(t).getUsername()));
						}
						log.info("\nPlease enter the ID of the transfer you wish to accept or reject, or type 'quit' to return to transfer options: ");
						String tid = scanner.nextLine();
						while (!tid.equals("quit")) {
							if (transferMap.containsKey(tid)) {
								Transfer t = transferMap.get(tid);
								log.info("Would you like to accept or reject Transfer "+t.getTid()+"?");
								log.info("\tID: "+tid);
								log.info("\tAmount: "+t.getAmount());
								log.info("\tTransferring User: "+t.getUsername());
								log.info("Please type 'accept', 'reject' or 'back' to return to the transfer list.");
								String acceptance = scanner.nextLine();
								if (acceptance.equals("back")) {
									break;
								}
								else if (acceptance.equals("accept")){
									bo.acceptTransfer(t);
									transferMap.remove(t.getTid());
									log.info("Transfer accepted!");
								}
								else if (acceptance.equals("reject")) {
									bo.rejectTransfer(t);
									transferMap.remove(t.getTid());
									log.info("Transfer rejected!");
								}
								else {
									log.info("Please enter 'accept', 'reject' or 'back'.");
								}
							}else {
								log.error("Transfer ID not found. Please enter the ID of a transfer listed.\n");
							}
							break;
						}

					break;
				case 3:
					log.info("Returning to customer options.\n");
					return false;
			}
		}
		catch (NumberFormatException e) {
			log.error("Please enter a number associated with an option.\n");
		}
		log.info("Returning to transfer options.\n");
		return true;
	}
	
	private static boolean customerCode(User user, Scanner scanner) {
		printCustomerOptions();
		try {
			int choice = Integer.parseInt(scanner.nextLine());
			UserBO bo = new UserBOImpl();
			switch(choice) {
				case 1:
					log.info("Please enter the name of your new account or 'quit' to return to customer options: ");
					String name = scanner.nextLine();
					if (name.equals("quit"))
						break;
					log.info("Please enter the starting balance of the account or 'quit' to return to customer options: ");
					bo.newAccount(user.getUser_id(), name, scanner);
					break;
				case 2:
					Map<String, Account> customerAccountMap = bo.getAllCustomerAccounts(user.getUser_id());
					Account customerAccount = getAccount(customerAccountMap, "Please enter the ID of the account you wish to view or type 'quit' to return to customer options: ", scanner);
					if (customerAccount==null){
						break;
					}
					log.info("Account " + customerAccount.getAid() +" with name "+customerAccount.getName() + " has a balance of "+customerAccount.getBalance()+"\n");
					break;
				case 3:
					try {
						Map<String, Account> accountMap = bo.getAllCustomerAccounts(user.getUser_id());
						Account account = getAccount(accountMap, "Please enter the ID of the account you wish to withdraw from or type 'quit' to return to customer options: ", scanner);
						if (account == null)
							break;
						log.info("Please enter the amount you wish to withdraw from this account or 'quit' to return to customer options: " );
						log.info("Current balance: " + account.getBalance());
						String stramount = scanner.nextLine();
						if (stramount.equals("quit"))
							break;
						else {
							try {
								int amount = Integer.parseInt(stramount);
								if (bo.withdraw(amount, account)) {
									int newBalance = account.getBalance()-amount;
									account.setBalance(newBalance);
									log.info("Withdrawal complete! Account "+ account.getAid() +" now has a balance of "+account.getBalance()+"\n");
								}
								else {
									log.error("Internal error occurred. Please contact support.\n");
								}
							}
							catch (NumberFormatException e) {
								log.error("Please enter a number greater than 0.\n");
							}
						}
					} catch (BankException e) {
						log.error(e.getMessage());
					}
					break;
				case 4:
					try {
						Map<String, Account> accountMap = bo.getAllCustomerAccounts(user.getUser_id());
						Account account = getAccount(accountMap, "Please enter the ID of the account you wish to deposit to or type 'quit' to return to customer options: ", scanner);
						if (account == null)
							break;
						log.info("Please enter the amount you wish to deposit to this account or 'quit' to return to customer options: " );
						log.info("Current balance: " + account.getBalance());
						String stramount = scanner.nextLine();
						if (stramount.equals("quit"))
							break;
						else {
							try {
								int amount = Integer.parseInt(stramount);
								if (bo.deposit(amount, account)) {
									int newBalance = account.getBalance()+amount;
									account.setBalance(newBalance);
									log.info("Deposit complete! Account "+ account.getAid() +" now has a balance of "+account.getBalance()+"\n");
								}
								else {
									log.error("Internal error occurred. Please contact support.\n");
								}
							}
							catch (NumberFormatException e) {
								log.error("Please enter a number greater than 0.\n");
							}
						}
					} catch (BankException e) {
						log.error(e.getMessage());
					}
					break;
				case 5:
					while(transferCode(user, scanner)) {}
					break;
				case 6:
					log.info("Returning to user options\n");
					return false;
				default:
					log.error("Please enter a number associated with an option.");
			}
		}
		catch (NumberFormatException e) {
			log.error("Please enter a number associated with an option.\n");
		}
		log.info("Returning to customer options\n");
		return true;
	}

	private static boolean employeeCode(Scanner scanner) {
		printEmployeeOptions();
		try {
			int choice = Integer.parseInt(scanner.nextLine());
			UserBO bo = new UserBOImpl();
			switch(choice) {
				case 1:
					try {
						Map<String, Account> accountMap = bo.getAllPendingAccounts();
						Account account = getAccount(accountMap, "Please select the account you wish to approve or reject, or type 'quit' to return to employee options: ", scanner);
						if (account == null)
							break;
						log.info("Would you like to approve or reject Account "+account.getName()+"?");
						log.info("\tID: "+account.getAid());
						log.info("\tStarting Balance: "+account.getBalance());
						log.info("Please type 'approve', 'reject', or 'back' to return to employee options.");
						String approval = scanner.nextLine();
						if (approval.toLowerCase().equals("approve")){
							bo.approveAccount(account.getAid());
							accountMap.remove(account.getName());
							log.info("Account "+account.getAid()+" approved.\n");
							break;
						}
						else if (approval.toLowerCase().equals("reject")) {
							bo.rejectAccount(account.getAid());
							accountMap.remove(account.getName());
							log.info("Account "+account.getAid()+" rejected.\n");
							break;
						} 
						else if (approval.toLowerCase().equals("back"))
							break;
						else {
							log.info("Please type 'approve', 'reject' or 'back'.");
						}
					} 
					catch (BankException e) {
						log.error(e.getMessage());
					}
					break;
				case 2:
					Map<String, User> customerMap = bo.getAllCustomers();
					log.info("Please select the customer whose accounts you wish to view or 'quit': ");
					for (String c : customerMap.keySet()) {
						log.info("\t"+c+": "+customerMap.get(c).getUsername());
					}
					String cid = scanner.nextLine();
					while (!cid.equals("quit")) {
						if (customerMap.containsKey(cid)) {
							Map<String, Account> accountMap = bo.getAllCustomerAccounts(cid);
							log.info("\nDisplaying bank accounts for customer "+cid);
							log.info("\tAccount ID\tAccount Name\t\tAccount Balance");
							for (Entry<String, Account> a : accountMap.entrySet()) {
								String strformat = "\t%10s\t%10s\t\t%10d";
								//log.info("\t"+a.getKey()+": "+a.getValue().getName() + "\t\tBalance: "+a.getValue().getBalance());
								log.info(String.format(strformat, a.getKey(), a.getValue().getName(), a.getValue().getBalance()));
							}
						}
						else {
							log.info("\nCustomer ID not found. Please enter the ID of a customer listed.\n");
						}
						log.info("Please select the customer whose accounts you wish to view or 'quit': ");
						for (String c : customerMap.keySet()) {
							log.info("\t"+c+": "+customerMap.get(c).getUsername());
						}
						cid = scanner.nextLine();
					}
					break;
				case 3:
					try {
						List<Transaction> transactionList = bo.getAllTransactions();
						log.info("Transaction log: ");
						log.info("\tTransaction ID\tTransaction Date\tTransaction Type\tAmount\tAccount 1 ID\t Account 2 ID");

						for (Transaction t : transactionList) {
							String strformat = "\t %10s \t %tF \t %20s \t %10d \t %10s \t %10s";
							String aid2 = t.getAid2() == null ? "null" : t.getAid2();
							log.info(String.format(strformat, t.getTrans_id(), t.getDate(), t.getType(), t.getAmount(), t.getAid1(), aid2));
						}
						log.info("\n");
					}
					catch (BankException e) {
						log.error(e.getMessage());
					}
					break;
				case 4:
					log.info("Returning to user options.\n");
					return false;
				default:
					throw new NumberFormatException();
			}
		} 
		catch (NumberFormatException e) {
			log.error("Please enter a number associated with an option.\n");
		}
		log.info("Returning to employee options.\n");
		return true;
	}
	
	private static boolean runApplication(User user, Scanner scanner) {
		int optionCount = printOptions(user);
		try {
			int choice = Integer.parseInt(scanner.nextLine());
			UserBO bo = new UserBOImpl();
			switch(choice) {
			case 1:
				if (user.getIsCustomer() == 0) {
					user.setIsCustomer(1);
					bo.registerCustomer(user.getUser_id());
				} else {
					while (customerCode(user, scanner));
				}
				break;
			case 2:
				if (user.getIsEmployee() == 1) {
					while(employeeCode(scanner));
				}
				else {
					return false;
				}
				break;
			case 3:
				if (choice == optionCount) {
					return false;
				} else {
					log.error("Invalid input.\n");
					break;
				}
			default:
				log.error("Invalid input.\n");
				break;
			}
		} 
		catch (NumberFormatException e) {
			log.error("Please enter a number associated with an option.\n");
		}
		return true;
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		log.info("Welcome to the Bank of Project 0 Application");
		log.info("---------------------------------");
		User user = login(scanner);
		try {
			if (user == null) {
				log.info("Error logging in. Please try again.");
				return;
			}
			while(runApplication(user, scanner)) {
				
			}
		} finally {
			log.info("Thank you for using Bank of Project 0 App. Please come again soon.");
			scanner.close();
		}
	}
}

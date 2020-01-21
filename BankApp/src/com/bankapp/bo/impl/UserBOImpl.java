package com.bankapp.bo.impl;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import java.sql.SQLException;

import com.bankapp.bo.UserBO;
import com.bankapp.dao.UserDAO;
import com.bankapp.dao.impl.UserDAOImpl;
import com.bankapp.exception.BankException;
import com.bankapp.main.BankMain;
import com.bankapp.to.Account;
import com.bankapp.to.Transaction;
import com.bankapp.to.Transfer;
import com.bankapp.to.User;

public class UserBOImpl implements UserBO {
	private UserDAO dao;
	private static Logger log = Logger.getLogger(UserBOImpl.class);


	@Override
	public User getUser(String username) {
		User user = null; 
		try {
			user = getDao().getUser(username);
		}
		catch (BankException e) {
			log.error(e.getMessage());
		}
		catch (ClassNotFoundException | SQLException e) {
			log.error("Internal error occurred while fetching user information.");
		}
		return user;
	}
	
	public void newAccount(String user_id, String name, Scanner scanner){
		try {
			String strbalance = scanner.nextLine();
			if (strbalance.equals("quit"))
				return;
			int balance = Integer.parseInt(strbalance);
			if (balance < 0)
				throw new BankException();
			getDao().newAccount(user_id, name, balance);
			log.info("Account has been sent for approval by an employee.\n");
		}
		catch (NumberFormatException | BankException e){
			log.error("Please enter a balance of 0 or greater. Returning to customer options.\n");
		}
		catch (ClassNotFoundException | SQLException e) {
			log.error("Internal error occurred while creating account.");
		}
	}

	public UserDAO getDao() {
		if (dao == null) {
			dao = new UserDAOImpl();
		}
		return dao;
	}

	@Override
	public Map<String, Account> getAllAccounts() {
		// only return approved accounts
		Map<String, Account> accountMap = null;
		try {
			accountMap = getDao().getAllAccounts();
		}
		catch (BankException e) {
			log.error(e.getMessage());
		}
		return accountMap;
	}

	@Override
	public boolean withdraw(int amount, Account account) throws BankException {
		if (amount < 0)
			throw new BankException("Please enter an amount greater than 0.\n");
		else if ((account.getBalance() - amount ) < 0) {
			throw new BankException("Please enter an amount less than or equal to the balance on this account.\n");
		}
		else {
			return getDao().withdraw(amount, account);
		}
	}
	
	@Override
	public boolean deposit(int amount, Account account) throws BankException {
		if (amount < 0)
			throw new BankException("Please enter an amount greater than 0.\n");
		else {
			return getDao().deposit(amount, account);
		}
	}

	@Override
	public Map<String, Account> getAllPendingAccounts() {
		Map<String, Account> accountMap = null;
		try {
			accountMap = getDao().getAllPendingAccounts();
		}
		catch (BankException e) {
			log.error(e.getMessage());
		}
		return accountMap;
	}

	@Override
	public void approveAccount(String aid) throws BankException {
		getDao().approveAccount(aid);
	}

	@Override
	public void rejectAccount(String aid) throws BankException {
		getDao().rejectAccount(aid);
	}

	@Override
	public Map<String, User> getAllCustomers() {
		Map<String, User> customerMap = null;
		try {
			customerMap = getDao().getAllCustomers();
		}
		catch (BankException e) {
			log.error(e.getMessage());
		}
		return customerMap;
	}

	@Override
	public Map<String, Account> getAllCustomerAccounts(String user_id) {
		Map<String, Account> accountMap = null;
		try {
			accountMap = getDao().getAllCustomerAccounts(user_id);
		}
		catch (BankException e) {
			log.error(e.getMessage());
		}
		return accountMap;
	}

	@Override
	public List<Transaction> getAllTransactions(){
		List<Transaction> transactionList = null;
		try {
			transactionList = getDao().getAllTransactions();
		}
		catch (BankException e) {
			log.error(e.getMessage());
		}
		return transactionList;
	}

	@Override
	public boolean transfer(int transferAmount, Account a1, Account a2) throws BankException {
		if (transferAmount > a1.getBalance()) {
			throw new BankException("Please enter an amount less than or equal to account "+a1.getAid()+ "'s balance.");
		}
		else
			return getDao().transfer(transferAmount, a1, a2);
	}

	
	@Override
	public Map<String, Transfer> getAllTransfers(String user_id) {
		Map<String, Transfer> transferMap = null;
		try {
		transferMap = getDao().getAllTransfers(user_id);
		}
		catch (BankException e) {
			log.error(e.getMessage());
		}
		return transferMap;
	}

	@Override
	public void acceptTransfer(Transfer t) {
		// TODO Auto-generated method stub
		try {
			getDao().acceptTransfer(t);
		} catch (BankException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	@Override
	public void rejectTransfer(Transfer t) {
		// TODO Auto-generated method stub
		try {
			getDao().rejectTransfer(t);
		} catch (BankException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}

	@Override
	public String newUser(String username, String password, int iscustomer, int isemployee) {
		try {
			 return getDao().newUser(username, password, iscustomer, isemployee);
		}
		catch (BankException e){
			log.error(e.getMessage());
		}
		return null;
		
	}

	@Override
	public void registerCustomer(String user_id) {
		// TODO Auto-generated method stub
		try {
		getDao().registerCustomer(user_id);
		}
		catch (BankException e) {
			log.error(e.getMessage());
		}
		
	}
}

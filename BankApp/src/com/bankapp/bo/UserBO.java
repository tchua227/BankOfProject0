package com.bankapp.bo;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.bankapp.exception.BankException;
import com.bankapp.to.Account;
import com.bankapp.to.Transaction;
import com.bankapp.to.Transfer;
import com.bankapp.to.User;

public interface UserBO {
	public User getUser(String username);
	public Map<String, Account> getAllAccounts();
	public void newAccount(String user_id, String name, Scanner scanner);
	public boolean withdraw(int amount, Account account) throws BankException;
	public boolean deposit(int amount, Account account) throws BankException;
	public Map<String, Account> getAllPendingAccounts();
	public void approveAccount(String aid) throws BankException;
	public void rejectAccount(String aid) throws BankException;
	public Map<String, User> getAllCustomers();
	public Map<String, Account> getAllCustomerAccounts(String cid);
	public List<Transaction> getAllTransactions() throws BankException;
	public boolean transfer(int transferAmount, Account a1, Account a2) throws BankException;
	public Map<String, Transfer> getAllTransfers(String user_id);
	public void acceptTransfer(Transfer t);
	public void rejectTransfer(Transfer t);
	public String newUser(String username, String password, int iscustomer, int isemployee);
	public void registerCustomer(String user_id);
}

package com.bankapp.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.bankapp.exception.BankException;
import com.bankapp.to.Account;
import com.bankapp.to.Transaction;
import com.bankapp.to.Transfer;
import com.bankapp.to.User;

public interface UserDAO {
	public User getUser(String username) throws BankException, ClassNotFoundException, SQLException;
	public Map<String, Account> getAllAccounts() throws BankException;
	public void newAccount(String user_id, String name, int balance) throws BankException, ClassNotFoundException, SQLException;
	public boolean withdraw(int amount, Account account) throws BankException;
	public boolean deposit(int amount, Account account) throws BankException;
	public Map<String, Account> getAllPendingAccounts() throws BankException;
	public void approveAccount(String aid) throws BankException;
	public void rejectAccount(String aid) throws BankException;
	public Map<String, User> getAllCustomers() throws BankException;
	public Map<String, Account> getAllCustomerAccounts(String cid) throws BankException;
	public List<Transaction> getAllTransactions() throws BankException;
	public boolean transfer(int transferAmount, Account a1, Account a2) throws BankException;
	public Map<String, Transfer> getAllTransfers(String user_id) throws BankException;
	public boolean acceptTransfer(Transfer t) throws BankException;
	public boolean rejectTransfer(Transfer t) throws BankException;
	public String newUser(String username, String password, int iscustomer, int isemployee) throws BankException;
	public void registerCustomer(String user_id) throws BankException;
}

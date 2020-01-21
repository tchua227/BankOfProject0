package com.bankapp.dao.impl;

import com.bankapp.dao.UserDAO;
import com.bankapp.exception.BankException;
import com.bankapp.to.Account;
import com.bankapp.to.Transaction;
import com.bankapp.to.Transfer;
import com.bankapp.to.User;
import com.dbutil.OracleConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAOImpl implements UserDAO {

	@Override
	public User getUser(String username) throws BankException, ClassNotFoundException, SQLException{
		User user = null;
		Connection connection=OracleConnection.getConnection();
		String sql = "select u.user_id,u.password,u.iscustomer,u.isemployee from users u where u.username=?";
		PreparedStatement preparedStatement=connection.prepareStatement(sql);
		preparedStatement.setString(1, username);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			user = new User();
			user.setIsCustomer(resultSet.getInt("iscustomer"));
			user.setPassword(resultSet.getString("password"));
			user.setUser_id(resultSet.getString("user_id"));
			user.setIsEmployee(resultSet.getInt("isemployee"));
			user.setUsername(username);
		} else {
			throw new BankException("Username " +username+" not found");
		}
		return user;
	}

	@Override
	public void newAccount(String user_id, String name, int balance) throws BankException, ClassNotFoundException, SQLException {
		Connection connection = OracleConnection.getConnection();
		String sql = "{call registeraccount(?, ?, ?, ?)}";
		CallableStatement callableStatement = connection.prepareCall(sql);
		callableStatement.setString(1, "id");
		callableStatement.setString(2, user_id);
		callableStatement.setString(3, name);
		callableStatement.setInt(4, balance);
		callableStatement.executeUpdate();
	}

	@Override
	public Map<String, Account> getAllAccounts() throws BankException {
		Map<String, Account> accountMap = new HashMap<String, Account>();
		try(Connection connection = OracleConnection.getConnection()){
			String sql = "select a.aid,a.name,a.balance,a.user_id from accounts a where a.approved=1";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Account account = new Account();
				account.setAid(resultSet.getString("aid"));
				account.setBalance(resultSet.getInt("balance"));
				account.setName(resultSet.getString("name"));
				account.setUser_id(resultSet.getString("user_id"));
				accountMap.put(account.getAid(), account);
			}
			if(accountMap.size() == 0) {
				throw new BankException("You have no open accounts yet.");
			}
		} 
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
		return accountMap;
	}

	@Override
	public boolean withdraw(int amount, Account account) throws BankException {
		try(Connection connection = OracleConnection.getConnection()){
			String sql = "update accounts a set a.balance=? where a.aid=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, account.getBalance() - amount);
			preparedStatement.setString(2,account.getAid());
			preparedStatement.executeUpdate();
			String registerTransaction = "{call registertransaction(?, 'withdrawal', ?, ?,null,?)}";
			CallableStatement callableStatement = connection.prepareCall(registerTransaction);
			callableStatement.setString(1, "id");
			callableStatement.setInt(2, amount);
			callableStatement.setString(3,account.getAid());
			callableStatement.setDate(4, new java.sql.Date(new Date().getTime()));
			callableStatement.execute();
			return true;
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
		
	}
	
	@Override
	public boolean deposit(int amount, Account account) throws BankException {
		try(Connection connection = OracleConnection.getConnection()){
			String sql = "update accounts a set a.balance=? where a.aid=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, account.getBalance() + amount);
			preparedStatement.setString(2,account.getAid());
			preparedStatement.executeUpdate();
			String registerTransaction = "{call registertransaction(?, 'deposit', ?, ?,null,?)}";
			CallableStatement callableStatement = connection.prepareCall(registerTransaction);
			callableStatement.setString(1, "id");
			callableStatement.setInt(2, amount);
			callableStatement.setString(3,account.getAid());
			callableStatement.setDate(4, new java.sql.Date(new Date().getTime()));
			callableStatement.execute();
			return true;
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
		
	}
	
	@Override
	public boolean transfer(int transferAmount, Account a1, Account a2) throws BankException {
		try(Connection connection = OracleConnection.getConnection()){
			String registerTransfer = "{call registertransfer(?, ?, ?, ?)}";
			CallableStatement callableStatement = connection.prepareCall(registerTransfer);
			callableStatement.setString(1, "id");
			callableStatement.setString(2, a1.getAid());
			callableStatement.setString(3,a2.getAid());
			callableStatement.setInt(4, transferAmount);
			callableStatement.execute();
			return true;
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
		
	}

	@Override
	public Map<String, Transfer> getAllTransfers(String user_id) throws BankException {
		Map<String, Transfer> transferMap = new HashMap<String, Transfer>();
		List<String> aidList = new ArrayList<String>();
		try(Connection connection = OracleConnection.getConnection()){
			String sql = "select a.aid from accounts a where a.user_id=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, user_id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				aidList.add(resultSet.getString("aid"));
			}
			for (String aid : aidList) {
				String sql1 = "select t.tid, t.aid1,t.amount,t.username from transfers t where t.aid2=? and t.isaccepted=0";
				PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
				preparedStatement1.setString(1, aid);
				ResultSet resultSet1 = preparedStatement1.executeQuery();
				//resultset1 is empty
				while(resultSet1.next()) {
					Transfer t = new Transfer();
					t.setAid1(resultSet1.getString("aid1"));
					t.setAid2(aid);
					t.setAmount(resultSet1.getInt("amount"));
					t.setTid(resultSet1.getString("tid"));
					t.setUsername(resultSet1.getString("username"));
					transferMap.put(t.getTid(), t);
				}
			}
			if (transferMap.isEmpty())
				throw new BankException("There are no pending offers for you.");
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
		return transferMap;
	}

	@Override
	public boolean acceptTransfer(Transfer t) throws BankException {
		try (Connection connection = OracleConnection.getConnection()){
			String sql = "{call accepttransfer(?)}"; //set aid1 to a.balance - t.amount
			CallableStatement callableStatement = connection.prepareCall(sql);
			callableStatement.setString(1, t.getTid());
			callableStatement.execute();
			return true;
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
	}

	@Override
	public boolean rejectTransfer(Transfer t) throws BankException {
		try (Connection connection = OracleConnection.getConnection()){
			String sql = "update transfers t set t.isaccepted=-1 where t.tid=?"; //set aid1 to a.balance - t.amount
			CallableStatement callableStatement = connection.prepareCall(sql);
			callableStatement.setString(1, t.getTid());
			callableStatement.execute();
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			throw new BankException("Internal error occurred.");
		} 
	}

	
	@Override
	public Map<String, Account> getAllPendingAccounts() throws BankException {
		Map<String, Account> accountMap = new HashMap<String, Account>();
		try(Connection connection = OracleConnection.getConnection()){
			String sql = "select a.aid,a.user_id,a.name,a.balance from accounts a where a.approved=0";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Account account = new Account();
				account.setAid(resultSet.getString("aid"));
				account.setBalance(resultSet.getInt("balance"));
				account.setName(resultSet.getString("name"));
				account.setUser_id(resultSet.getString("user_id"));
				accountMap.put(account.getAid(), account);
			}
			if(accountMap.size() == 0) {
				throw new BankException("There are no currently pending accounts.");
			}
		} 
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
		return accountMap;
	}

	@Override
	public void approveAccount(String aid) throws BankException {
		try (Connection connection = OracleConnection.getConnection()){
			String sql = "update accounts a set a.approved=1 where a.aid=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1,aid);
			preparedStatement.executeUpdate();
		}
		catch(ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
	}

	@Override
	public void rejectAccount(String aid) throws BankException {
		try (Connection connection = OracleConnection.getConnection()){
			String sql = "delete from accounts a where a.aid=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1,aid);
			preparedStatement.executeUpdate();
		}
		catch(ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
	}

	@Override
	public Map<String, User> getAllCustomers() throws BankException {
		Map<String, User> customerMap = new HashMap<String, User>();
		try(Connection connection = OracleConnection.getConnection()){
			String sql = "select u.user_id,u.username from users u where u.iscustomer=1";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				User u = new User();
				u.setUser_id(resultSet.getString("user_id"));
				u.setUsername(resultSet.getString("username"));
				customerMap.put(u.getUser_id(), u);
			}
			if(customerMap.size() == 0) {
				throw new BankException("There are no customers.");
			}
		} 
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
		return customerMap;
	}

	@Override
	public Map<String, Account> getAllCustomerAccounts(String user_id) throws BankException {
		Map<String, Account> accountMap = new HashMap<String, Account>();
		try(Connection connection = OracleConnection.getConnection()){
			String sql = "select a.aid,a.name,a.balance from accounts a"
					+ " where a.approved=1 and a.user_id=?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, user_id);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Account account = new Account();
				account.setAid(resultSet.getString("aid"));
				account.setBalance(resultSet.getInt("balance"));
				account.setName(resultSet.getString("name"));
				account.setUser_id(user_id);
				accountMap.put(account.getAid(), account);
			}
			if(accountMap.size() == 0) {
				throw new BankException("You have no open accounts yet.");
			}
		} 
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
		return accountMap;
	}

	@Override
	public List<Transaction> getAllTransactions() throws BankException {
		List<Transaction> transactionList = new ArrayList<Transaction>();
		try(Connection connection = OracleConnection.getConnection()){
			String sql = "select t.trans_id, t.type, t.amount, t.aid1,t.aid2,t.tdate from transactions t order by t.trans_id";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				Transaction t = new Transaction();
				t.setAid1(resultSet.getString("aid1"));
				t.setAid2(resultSet.getString("aid2"));
				t.setAmount(resultSet.getInt("amount"));
				t.setDate(resultSet.getDate("tdate"));
				t.setTrans_id(resultSet.getString("trans_id"));
				t.setType(resultSet.getString("type"));
				transactionList.add(t);
			}
			if (transactionList.size() == 0) {
				throw new BankException("There are no transactions.");
			}
		}
		catch(ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
		return transactionList;
	}

	@Override
	public String newUser(String username, String password, int iscustomer, int isemployee) throws BankException {
		// TODO Auto-generated method stub
		try (Connection connection = OracleConnection.getConnection()){
			String sql = "{call registeruser(?, ?,?,?,?)}";
			CallableStatement callableStatement = connection.prepareCall(sql);
			
			callableStatement.registerOutParameter(1, java.sql.Types.VARCHAR);
			callableStatement.setString(2, username);
			callableStatement.setString(3, password);
			callableStatement.setInt(4, iscustomer);
			callableStatement.setInt(5, isemployee);
			callableStatement.execute();
			return callableStatement.getString(1);
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
	}

	@Override
	public void registerCustomer(String user_id) throws BankException{
		// TODO Auto-generated method stub
		try (Connection connection = OracleConnection.getConnection()){
			String sql = "update users u set u.iscustomer=1 where u.user_id=?";
			CallableStatement callableStatement = connection.prepareCall(sql);
			callableStatement.setString(1, user_id);
			callableStatement.execute();
		}
		catch (ClassNotFoundException | SQLException e) {
			throw new BankException("Internal error occurred.");
		}
	}
}

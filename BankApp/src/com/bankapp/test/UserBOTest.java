package com.bankapp.test;

//import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.bankapp.exception.BankException;
import com.bankapp.to.Account;
import com.bankapp.bo.impl.UserBOImpl;

class UserBOTest {
	private static UserBOImpl bo;
	
	@BeforeAll
	public static void instantiateBank() {
		bo = new UserBOImpl();
	}
	
	@Test
	public void assertAccountsAreReturned() {
		assertNotNull(bo.getAllAccounts());
	}
	
	@Test
	public void assertGetUserReturnsWhenGivenValidUsername() {
		assertNotNull(bo.getUser("tee"));
	}
	
	@Test 
	public void assertGetUserReturnsNullWhenGivenInvalidUsername() {
		assertNull(bo.getUser("InvalidUsername"));
	}
	
	@Test
	public void withdrawThrowsExceptionWhenGivenNegativeAmount() {
		Assertions.assertThrows(BankException.class, () ->{bo.withdraw(-1, new Account() );});
	}
	
	@Test
	public void withdrawThrowsExceptionWhenGivenAmountGreaterThanAccountBalance() {
		Assertions.assertThrows(BankException.class, () ->{bo.withdraw(70, new Account(68) );});
	}
	
	@Test
	public void depositThrowsExceptionWhenGivenNegativeAmount() {
		Assertions.assertThrows(BankException.class, () ->{bo.deposit(-1, new Account() );});
	}
	
	@Test
	public void assertCustomersAreReturned() {
		assertNotNull(bo.getAllCustomers());
	}
	
	@Test
	public void assertCustomerAccountsAreNullWhenGivenInvalidUserID() {
		assertNull(bo.getAllCustomerAccounts("6868"));
	}
	
	@Test
	public void assertCustomerAccountsAreReturned() {
		assertNotNull(bo.getAllCustomerAccounts("1"));
	}
	
	@Test
	public void assertTransactionsAreReturned() {
		assertNotNull(bo.getAllTransactions());
	}
	
	@AfterAll
	public static void clearBank() {
		bo = null;
	}
}

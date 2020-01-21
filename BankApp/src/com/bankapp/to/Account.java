package com.bankapp.to;

public class Account {
	private String aid;
	private String user_id;
	private String name;
	private int balance;
	public Account() {	}
	public Account(int bal) {balance=bal;}
	@Override
	public String toString() {
		return "Account [aid=" + aid + ", user_id=" + user_id + ", name=" + name + ", balance=" + balance
				+ "]";
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	
}

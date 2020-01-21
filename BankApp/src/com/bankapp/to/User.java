package com.bankapp.to;

public class User {
	private String user_id;
	private String username;
	private String password;
	private int isCustomer;
	private int isEmployee;
	public User() {}
	@Override
	public String toString() {
		return "User [user_id = " + user_id + ", username = " 
				+ username + ", password = " + password + ", isCustomer = " + isCustomer +"]";
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getIsCustomer() {
		return isCustomer;
	}
	public void setIsCustomer(int isCustomer) {
		this.isCustomer = isCustomer;
	}
	public int getIsEmployee() {
		return isEmployee;
	}
	public void setIsEmployee(int isEmployee) {
		this.isEmployee = isEmployee;
	}
	
}

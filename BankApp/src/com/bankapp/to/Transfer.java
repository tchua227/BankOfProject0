package com.bankapp.to;

public class Transfer {
	private String tid;
	private String aid1;
	private String aid2;
	private int amount;
	private String username;
	
	public Transfer() {}

	@Override
	public String toString() {
		return "Transfer [tid=" + tid + ", aid1=" + aid1 + ", aid2=" + aid2 + ", amount=" + amount +  "]";
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getAid1() {
		return aid1;
	}

	public void setAid1(String aid1) {
		this.aid1 = aid1;
	}

	public String getAid2() {
		return aid2;
	}

	public void setAid2(String aid2) {
		this.aid2 = aid2;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
}

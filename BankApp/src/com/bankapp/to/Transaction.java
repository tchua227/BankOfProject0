package com.bankapp.to;

import java.util.Date;

public class Transaction {
	private String trans_id;
	private String type;
	private int amount;
	private String aid1;
	private String aid2;
	private Date date;
	public Transaction() {}
	@Override
	public String toString() {
		return "Transaction [trans_id=" + trans_id + ", type=" + type + ", amount=" + amount + ", aid1=" + aid1
				+ ", aid2=" + aid2 + ", date=" + date+"]";
	}
	public String getTrans_id() {
		return trans_id;
	}
	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}

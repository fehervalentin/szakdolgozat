package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;


public class Player implements Serializable	{

	private static final long serialVersionUID = 1L;
	
	protected String userName;
	
	protected long regDate;
	
	protected BigDecimal balance;
	
	public Player() {
		
	}
	
	public Player(String userName, BigDecimal balance, long regDate) {
		this.userName = userName;
		this.balance = balance;
		this.regDate = regDate;
	}

	public Player(String username) {
		this.userName = username;
	}

	public String getUserName() {
		return userName;
	}

	public long getRegDate() {
		return regDate;
	}

	public BigDecimal getBalance() {
		return balance;
	}
}

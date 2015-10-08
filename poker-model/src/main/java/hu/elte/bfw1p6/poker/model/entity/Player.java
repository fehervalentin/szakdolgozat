package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;


public class Player implements Serializable	{

	private static final long serialVersionUID = 1L;
	
	public Player() {
		
	}

	protected String userName;

	protected long regDate;

	protected BigDecimal balance;

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

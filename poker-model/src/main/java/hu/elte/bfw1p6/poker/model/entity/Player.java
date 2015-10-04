package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Player implements Serializable	{

	private static final long serialVersionUID = 1L;
	
	@Column(name="username")
	protected String userName;
	
	@Column(name="regdate")
	protected long regDate;
	
	@Column(name="balance")
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

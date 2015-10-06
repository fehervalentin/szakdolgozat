package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public class Player implements Serializable	{

	private static final long serialVersionUID = 1L;
	
	public Player() {
		
	}
	
	@NotNull
	@Column(name="username")
	protected String userName;
	
	@NotNull
	@Column(name="reg_date")
	protected long regDate;
	
	@NotNull
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

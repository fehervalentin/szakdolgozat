package hu.elte.bfw1p6.poker.model.entity;

import java.math.BigDecimal;

public class User extends Player{

	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String password;
	
	public User(String username) {
		super(username);
	}
	
	public User() {
		super();
	}
	
	public long getId() {
		return id;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setAmount(BigDecimal balance) {
		this.balance = balance;
	}
	
	public void setRegDate(long regDate) {
		this.regDate = regDate;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}

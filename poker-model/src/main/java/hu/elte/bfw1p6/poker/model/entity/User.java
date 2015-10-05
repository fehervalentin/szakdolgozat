package hu.elte.bfw1p6.poker.model.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table
@Entity
public class User extends Player{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private int id;
	
	@NotNull
	@Column(name = "password")
	private String password;
	
	@NotNull
	@Column(name = "salt")
	private String salt;
	
	public User(String username) {
		super(username);
	}
	
	public long getId() {
		return id;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getSalt() {
		return salt;
	}

	public void setAmount(BigDecimal balance) {
		this.balance = balance;
	}
	
	public void setRegDate(long regDate) {
		this.regDate = regDate;
	}
}

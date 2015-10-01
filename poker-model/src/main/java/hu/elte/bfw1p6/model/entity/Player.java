package hu.elte.bfw1p6.model.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Player implements Serializable	{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 	
	private String userName;
	private long regDate;

	public Player(String username) {
		this.userName = username;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getRegDate() {
		return regDate;
	}

	public void setRegDate(long regDate) {
		this.regDate = regDate;
	}


}

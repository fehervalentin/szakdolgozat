package hu.elte.bfw1p6.poker.model.entity;

import java.math.BigDecimal;

public class User extends Player implements EntityWithId {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String password;

	public User(String username) {
		super(username);
	}

	public User() {
		super();
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setRegDate(long regDate) {
		this.regDate = regDate;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setAmount(BigDecimal balance) {
		this.balance = balance;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return userName;
		case 1:
			return balance;
		case 2:
			return regDate;
		case 3:
			return password;
		default:
			return null;
		}
	}

	@Override
	public void set(int columnIndex, Object value) {
		switch (columnIndex) {
		case 0: {
			setUserName((String) value);
			break;
		}
		case 1: {
			setAmount((BigDecimal) value);
			break;
		}
		case 2: {
			setRegDate((Long) value);
			break;
		}
		case 3: {
			setPassword((String) value);
			break;
		}
		}
	}
}

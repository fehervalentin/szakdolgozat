package hu.elte.bfw1p6.model;

import java.io.Serializable;

public class LoginUser implements Serializable{

	private static final long serialVersionUID = -4231963293150345880L;
	
	private String userName;
	private String password;

	public LoginUser(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

package hu.elte.bfw1p6.persist.model;

import hu.elte.bfw1p6.model.entity.Player;

public class User extends Player{

	private static final long serialVersionUID = 1L;
	
	
	private long id;
	private String password;
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
	
//	public Player getPlayer() {
//		
//	}
	
	public String getPassword() {
		return password;
	}
	
	public String getSalt() {
		return salt;
	}
}

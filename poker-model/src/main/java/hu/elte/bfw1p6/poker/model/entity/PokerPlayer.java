package hu.elte.bfw1p6.poker.model.entity;

import java.math.BigDecimal;

import com.cantero.games.poker.texasholdem.Player;

/**
 * Olyan speciális objektum, amelyet leküldhetünk a kliensnek, amely egy User entitást reprezentál.
 * @author feher
 *
 */
public class PokerPlayer extends Player {

	private static final long serialVersionUID = -7074017817656740948L;

	/**
	 * A játékos felhasználó neve.
	 */
	protected String userName;
	
	/**
	 * A játékos regisztráció dátuma.
	 */
	protected long regDate;
	
	/**
	 * A játékos egyenlege.
	 */
	protected BigDecimal balance;

	/**
	 * A felhasználó admin-e.
	 */
	protected Boolean admin;
	
	public PokerPlayer() {
		
	}
	
	public PokerPlayer(String userName, BigDecimal balance, long regDate) {
		this.userName = userName;
		this.balance = balance;
		this.regDate = regDate;
	}

	public PokerPlayer(String username) {
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
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Boolean getAdmin() {
		return admin;
	}
}
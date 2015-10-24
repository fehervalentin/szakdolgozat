package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cantero.games.poker.texasholdem.Player;

public class PokerPlayer extends Player implements Serializable {

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
}

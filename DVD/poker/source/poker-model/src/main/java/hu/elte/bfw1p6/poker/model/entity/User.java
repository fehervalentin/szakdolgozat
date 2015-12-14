package hu.elte.bfw1p6.poker.model.entity;

import java.math.BigDecimal;

import com.cantero.games.poker.texasholdem.Player;

/**
 * A ténylegesen letárolandó felhasználói fiók.
 * @author feher
 *
 */
public class User extends Player implements EntityWithId {

	private static final long serialVersionUID = 8433545627454578662L;
	
	public static final String TABLE_NAME = "users";

	private Integer id;
	
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

	/**
	 * A felhasználó hashelt jelszava.
	 */
	private String password;

	public User() {

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

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return id;
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
		case 4:
			return admin;
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
			setBalance((BigDecimal) value);
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
		case 4: {
			setAdmin((Boolean) value);
			break;
		}
		}
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
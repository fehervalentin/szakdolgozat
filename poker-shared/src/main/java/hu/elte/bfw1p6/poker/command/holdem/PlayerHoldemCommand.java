package hu.elte.bfw1p6.poker.command.holdem;

import java.io.Serializable;
import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerCommandType;

/**
 * Az osztály valósítja meg a játékosok által küldendő utasításokat
 * @author feher
 *
 */
public class PlayerHoldemCommand implements PokerCommand, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HoldemPlayerCommandType playerCommandType;
	private BigDecimal amount;
	
	private String sender;
	
	/**
	 * Konstruktor
	 * @param playerCommandType A Command típusa
	 * @param amount RAISE esetén az emelendő összeg, CALL esetén a maradék összeg (ami még hiányzik az egyenlítéshez)
	 */
	public PlayerHoldemCommand(HoldemPlayerCommandType playerCommandType, BigDecimal amount) {
		this.playerCommandType = playerCommandType;
		this.amount = amount;
	}
	
	/**
	 * Csak RAISE esetén lehessen elkérni, vagy nullt adjon vissza...?
	 * @return
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	
	/**
	 * 
	 * @return A játékos által választott utasítás
	 */
	public HoldemPlayerCommandType getPlayerCommandType() {
		return playerCommandType;
	}
	
	/**
	 * 
	 * @return Az utasítást küldő felhasználó neve
	 */
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
}

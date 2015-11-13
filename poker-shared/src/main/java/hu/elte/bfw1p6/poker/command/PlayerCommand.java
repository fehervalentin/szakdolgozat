package hu.elte.bfw1p6.poker.command;

import java.math.BigDecimal;

/**
 * Olyan utasítások, amelyeket a kommunikáció során a kliensek (játékosok) küldenek. 
 * @author feher
 *
 */
public abstract class PlayerCommand implements PokerCommand {

	private static final long serialVersionUID = -6222563485679670229L;

	/**
	 * A megadandó tét.
	 */
	protected BigDecimal callAmount;
	
	/**
	 * Az emelendő tét.
	 */
	protected BigDecimal raiseAmount;
	
	/**
	 * Az üzenetet küldő játékos neve.
	 */
	protected String sender;
	
	/**
	 * Ki van éppen soron.
	 */
	protected int whosOn;
	
	/**
	 * Ki lépett ki.
	 */
	protected int whosQuit;
	
	protected boolean winnerCommand;
	
	protected int clientsCount;
	
	@Override
	public int getWhosOn() {
		return whosOn;
	}
	
	public abstract String getCommandType();
	
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setWhosOn(int whosOn) {
		this.whosOn = whosOn;
	}
	
	public int getWhosQuit() {
		return whosQuit;
	}

	public BigDecimal getCallAmount() {
		return callAmount;
	}

	public BigDecimal getRaiseAmount() {
		return raiseAmount;
	}
	
	public void setWinnerCommand(boolean winnerCommand) {
		this.winnerCommand = winnerCommand;
	}
	
	public boolean isWinnerCommand() {
		return winnerCommand;
	}

	public int getClientsCount() {
		return clientsCount;
	}

	public void setClientsCount(int clientsCount) {
		this.clientsCount = clientsCount;
	}
}
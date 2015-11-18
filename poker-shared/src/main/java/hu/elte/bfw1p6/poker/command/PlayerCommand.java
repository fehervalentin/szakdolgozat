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
	
	/**
	 * Ha winner körben CHECK-el valaki, az nem szokványos CHECK.
	 */
	protected boolean winnerCommand;
	
	/**
	 * Hány játékos van az adott körben.
	 */
	protected int clientsCount;
	
	@Override
	public int getWhosOn() {
		return whosOn;
	}
	
	public abstract String getCommandType();
	
	/**
	 * Ha egy játékos BLIND típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param callAmount a vak mértéke
	 */
	public abstract void setUpBlindCommand(BigDecimal callAmount);
	
	/**
	 * Ha egy játékos CHECK típusú utasítást küld, akkor ezt a metódust kell használni.
	 */
	public abstract void setUpCheckCommand();
	
	/**
	 * Ha egy játékos CALL típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param callAmount a megadandó tét
	 */
	public abstract void setUpCallCommand(BigDecimal callAmount);
	
	/**
	 * Ha egy játékos RAISE típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param callAmount a megadandó tét
	 * @param raiseAmount az emelendő tét
	 */
	public abstract void setUpRaiseCommand(BigDecimal callAmount, BigDecimal raiseAmount);
	
	/**
	 * Ha egy játékos FOLD típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param youAreNth a játékos sorszáma
	 */
	public abstract void setUpFoldCommand(int youAreNth);
	
	/**
	 * Ha egy játékos QUIT típusú utasítást küld, akkor ezt a metódust kell használni.
	 * @param tempNth a játékos sorszáma
	 * @return this
	 */
	public abstract PlayerCommand setUpQuitCommand(String sender, int youAreNth);
	
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
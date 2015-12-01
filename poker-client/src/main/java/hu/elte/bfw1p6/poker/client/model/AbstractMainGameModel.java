package hu.elte.bfw1p6.poker.client.model;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.session.PokerSession;

/**
 * A póker játék kliens oldali játék közbeni absztrakt modelje.
 * @author feher
 *
 */
public abstract class AbstractMainGameModel {

	/**
	 * A kliens sessionje.
	 */
	protected PokerSession pokerSession;
	
	/**
	 * A szerver kliens oldali csonkja.
	 */
	protected PokerRemote pokerRemote;

	/**
	 * Pókertábla entitás.
	 */
	protected PokerTable pokerTable;
	
	/**
	 * A szerver-kliens kommunikációjáért felelős objektum.
	 */
	protected CommunicatorController communicatorController;

	/**
	 * Hanyadik vagyok a körben.
	 */
	protected int youAreNth;

	/**
	 * Hány játékos van velem együtt.
	 */
	protected int players;

	/**
	 * A tartozásom az asztal felé, amit CALL vagy RAISE esetén meg kell adnom.
	 */
	protected BigDecimal myDebt;
	
	public AbstractMainGameModel(CommunicatorController communicatorController) {
		this.pokerSession = Model.getPokerSession();
		this.pokerRemote = Model.getPokerRemote();
		this.youAreNth = -1;
		this.pokerTable = Model.getParamPokerTable();
		this.myDebt = pokerTable.getBigBlind();
		this.communicatorController = communicatorController;
	}

	public String getUserName() {
		return pokerSession.getUser().getUserName();
	}
	
	public BigDecimal getBalance() {
		return pokerSession.getUser().getBalance();
	}
	
	/**
	 * Csatlakozás a kijelölt asztalhoz.
	 * @param observer a csatlakozni kívánó kliens
	 * @throws RemoteException
	 */
	public void connectToTable(PokerRemoteObserver observer) throws RemoteException {
		pokerRemote.connectToTable(pokerSession.getId(), pokerTable, observer);
	}

	/**
	 * A dealer mellett közvetlenül balra ülő játékos köteles kis vakot betenni.
	 * @param houseCommand az utasítás
	 * @return ha nekem kell betenni a kis vakot, akkor true, különben false.
	 */
	protected boolean areYouTheSmallBlind(HouseCommand houseCommand) {
		return youAreNth == ((houseCommand.getDealer() + 1) % players);
	}

	/**
	 * A dealer mellett kettővel balra ülő játékos köteles nagy vakot betenni.
	 * @param houseCommand az utasítás
	 * @return ha nekem kell betenni a nagy vakot, akkor true, különben false.
	 */
	protected boolean areYouTheBigBlind(HouseCommand houseCommand) {
		return youAreNth == ((houseCommand.getDealer() + 2) % players);
	}
	
	/**
	 * Ha BLIND típusú utasítás jött a szervertől
	 * @param houseCommand a szerver utasítás
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws RemoteException 
	 */
	public void receivedBlindHouseCommand(HouseCommand houseCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		myDebt = pokerTable.getBigBlind();
		youAreNth = houseCommand.getNthPlayer();
		players = houseCommand.getPlayers();
		if (areYouTheSmallBlind(houseCommand)) {
			tossBlind(false);
		} else if (areYouTheBigBlind(houseCommand)) {
			tossBlind(true);
		}
	}

	/**
	 * Utasítás küldése a játéktábla szervernek.
	 * @param playerCommand az utasítás
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	protected void sendCommandToTable(PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		playerCommand.setSender(pokerSession.getUser().getUserName());
		pokerRemote.sendPlayerCommand(pokerSession.getId(), pokerTable, communicatorController, playerCommand);
		pokerSession.refreshBalance(pokerRemote.refreshBalance(pokerSession.getId()));
	}
	
	/**
	 * A vakot rakja be az asztalra.
	 * @param bigBlind ha nagy vakot szeretnénk berakni, akkor az értéke true, különben false
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws RemoteException 
	 */
	protected abstract void tossBlind(Boolean bigBlind) throws PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * CALL típusú utasítás küldése a szervernek.
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendCallCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * CHECK típusú utasítás küldése a szervernek.
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendCheckCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * RAISE típusú utasítás küldése a szervernek.
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendRaiseCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * FOLD típusú utasítás küldése a szervenek.
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendFoldCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * QUIT típusú utasítás küldése a szervernek.
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendQuitCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException;

	/**
	 * DEAL típusú utasítás érkezett a szervertől.
	 * @param houseCommand az utasítás
	 */
	public void receivedDealHouseCommand(HouseCommand houseCommand) {
		pokerSession.getUser().setCards(houseCommand.getCards());
	}
	
	/**
	 * RAISE típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	public void receivedRaisePlayerCommand(PlayerCommand playerCommand) {
		myDebt = playerCommand.getSender().equals(getUserName()) ? BigDecimal.ZERO : myDebt.add(playerCommand.getRaiseAmount());
	}

	public BigDecimal getMyDebt() {
		return myDebt;
	}
	
	public int getYouAreNth() {
		return youAreNth;
	}
}
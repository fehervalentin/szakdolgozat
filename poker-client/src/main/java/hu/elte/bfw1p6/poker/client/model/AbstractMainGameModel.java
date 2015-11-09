package hu.elte.bfw1p6.poker.client.model;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

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
	 * A szerver-kliens komminukációért felelős objektum.
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
		this.myDebt = pokerTable.getDefaultPot();
		this.communicatorController = communicatorController;

		System.out.println("Ki vagyok: " + getUserName());
	}

	public String getUserName() {
		return pokerSession.getPlayer().getUserName();
	}
	
	public BigDecimal getBalance() {
		return pokerSession.getPlayer().getBalance();
	}
	
	/**
	 * Csatlakozás a kijelölt asztalhoz.
	 * @param observer a csatlakozni kívánó kliens
	 * @throws RemoteException
	 * @throws PokerTooMuchPlayerException
	 * @throws PokerUnauthenticatedException
	 */
	public void connectToTable(PokerRemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException, PokerUnauthenticatedException {
		pokerRemote.connectToTable(pokerSession.getId(), pokerTable, observer);
	}

	/**
	 * A dealer mellett közvetlenül balra ülő játékos köteles kis vakot betenni.
	 * @return ha nekem kell betenni a kis vakot, akkor true, különben false.
	 */
	protected boolean areYouTheSmallBlind(HouseCommand houseCommand) {
		return youAreNth == ((houseCommand.getDealer() + 1) % players);
	}

	/**
	 * A dealer mellett kettővel balra ülő játékos köteles nagy vakot betenni.
	 * @return ha nekem kell betenni a nagy vakot, akkor true, különben false.
	 */
	protected boolean areYouTheBigBlind(HouseCommand houseCommand) {
		return youAreNth == ((houseCommand.getDealer() + 2) % players);
	}
	
	/**
	 * Ha BLIND utasítás jött a szervertől
	 * @param houseHoldemCommand a szerver utasítás
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws PokerUnauthenticatedException 
	 * @throws RemoteException 
	 */
	public void receivedBlindHouseCommand(HouseCommand houseCommand) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException, RemoteException {
		myDebt = pokerTable.getDefaultPot();
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
	 * @throws PokerUnauthenticatedException
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	protected void sendCommandToTable(PlayerCommand playerCommand) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException, RemoteException {
		playerCommand.setSender(pokerSession.getPlayer().getUserName());
		pokerRemote.sendPlayerCommand(pokerSession.getId(), pokerTable, communicatorController, playerCommand);
		pokerSession.refreshBalance(pokerRemote.refreshBalance(pokerSession.getId()));
//		System.out.println("uj balance: " + pokerSession.getPlayer().getBalance());
	}
	
	/**
	 * A vakot rakja be az asztalra.
	 * @param bigBlind ha nagy vakot szeretnénk berakni, akkor az értéke true, különben false
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws PokerUnauthenticatedException 
	 * @throws RemoteException 
	 */
	protected abstract void tossBlind(Boolean bigBlind) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * CALL típusú utasítás küldése a szervernek.
	 * @throws PokerUnauthenticatedException
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendCallCommand() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * CHECK típusú utasítás küldése a szervernek.
	 * @throws PokerUnauthenticatedException
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendCheckCommand() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * RAISE típusú utasítás küldése a szervernek.
	 * @param amount az emelendő összeg
	 * @throws PokerUnauthenticatedException
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendRaiseCommand(BigDecimal amount) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * FOLD típusú utasítás küldése a szervenek.
	 * @throws PokerUnauthenticatedException
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendFoldCommand() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException, RemoteException;
	
	/**
	 * QUIT típusú utasítás küldése a szervernek.
	 * @throws PokerUnauthenticatedException
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public abstract void sendQuitCommand() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException, RemoteException;

	/**
	 * DEAL típusú utasítás érkezett a szervertől.
	 * @param houseCommand az utasítás
	 */
	public void receivedDealHouseCommand(HouseCommand houseCommand) {
		pokerSession.getPlayer().setCards(houseCommand.getCards());
	}
	
	/**
	 * RAISE típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	public void receivedRaisePlayerCommand(PlayerCommand playerCommand) {
		// és mi van ha én magam emeltem...
		// ha én magam emeltem, akkor a szerver elszámolta a teljes adósságom
		myDebt = playerCommand.getSender().equals(getUserName()) ? BigDecimal.ZERO : myDebt.add(playerCommand.getRaiseAmount());
	}

	/**
	 * FOLD típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	public void receivedFoldPlayerCommand(PlayerCommand playerCommand) {
		if (youAreNth > playerCommand.getWhosQuit()) {
			--youAreNth;
		}
	}
	
	/**
	 * QUIT típusú utasítás érkezett egy játékostól.
	 * @param playerCommand az utasítás
	 */
	public void receivedQuitPlayerCommand(PlayerCommand playerCommand) {
		if (youAreNth > playerCommand.getWhosQuit()) {
			--youAreNth;
		}
	}

	public BigDecimal getMyDebt() {
		return myDebt;
	}
	
	public int getYouAreNth() {
		return youAreNth;
	}
}

package hu.elte.bfw1p6.poker.client.model;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public abstract class AbstractMainGameModel {

	protected PokerSession pokerSession;
	protected PokerRemote pokerRemote;

	protected PokerTable pokerTable;
	
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
	 * A tartozásom az asztal felé, amit <b>CALL</b> vagy <b>RAISE</b> esetén meg kell adnom.
	 */
	protected BigDecimal myDebt;
	
	public AbstractMainGameModel(CommunicatorController communicatorController) {
		this.pokerSession = Model.getInstance().getPokerSession();
		this.pokerRemote = RMIRepository.getInstance().getPokerRemote();
		this.youAreNth = -1;
		this.pokerTable = ConnectTableHelper.getInstance().getPokerTable();
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
	
	public void connectToTable(RemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException, PokerUnauthenticatedException {
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
	
	public void sendCommandToTable(PlayerCommand playerCommand) throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		playerCommand.setSender(pokerSession.getPlayer().getUserName());
		pokerRemote.sendPlayerCommand(pokerSession.getId(), pokerTable, communicatorController, playerCommand);
		pokerSession.refreshBalance(pokerRemote.refreshBalance(pokerSession.getId()));
//		System.out.println("uj balance: " + pokerSession.getPlayer().getBalance());
	}
	
	/**
	 * Ha BLIND utasítás jött a szervertől
	 * @param houseHoldemCommand a szerver utasítás
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws PokerUnauthenticatedException 
	 */
	public void receivedBlindHouseCommand(HouseCommand houseCommand) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		myDebt = pokerTable.getDefaultPot();
		youAreNth = houseCommand.getNthPlayer();
		players = houseCommand.getPlayers();
		// első körben az a dealer, aki elsőként csatlakozott, roundonként +1
//		System.out.println("Hanyadik játékos vagy a szerveren: " + youAreNth);
//		System.out.println("Aktuális dealer: " + houseHoldemCommand.getDealer());
		if (areYouTheSmallBlind(houseCommand)) {
//			System.out.println("Betettem a kis vakot");
			tossBlind(false);
		} else if (areYouTheBigBlind(houseCommand)) {
//			System.out.println("Betettem a nagy vakot");
			tossBlind(true);
		}
		// nagyvaktól eggyel balra ülő kezd
//		System.out.println("Az éppen soron levő játékos: " + houseHoldemCommand.getWhosOn());
	}

	protected void sendPlayerCommand(PlayerCommand playerCommand) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		try {
			sendCommandToTable(playerCommand);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * A vakot rakja be az asztalra.
	 * @param bigBlind ha nagy vakot szeretnénk berakni, akkor az értéke true, különben false
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws PokerUnauthenticatedException 
	 */
	protected abstract void tossBlind(Boolean bigBlind) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException;
	
	public abstract void call() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException;
	
	public abstract void check() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException;
	
	public abstract void raise(BigDecimal amount) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException;
	
	public abstract void fold() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException;
	
	public abstract void quit() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException;
	
	public void receivedBlindPlayerCommand(PlayerCommand playerCommand) {
		// TODO Auto-generated method stub
		
	}

	public void receivedDealHouseCommand(HouseCommand houseCommand) {
		pokerSession.getPlayer().setCards(houseCommand.getCards());
	}

	public void receivedCallPlayerCommand(PlayerCommand playerCommand) {
		// TODO Auto-generated method stub
		
	}

	public void receivedCheckPlayerCommand(PlayerCommand playerCommand) {
		// TODO Auto-generated method stub
		
	}
	
	public void receivedRaisePlayerCommand(PlayerCommand playerCommand) {
		// és mi van ha én magam emeltem...
		// ha én magam emeltem, akkor a szerver elszámolta a teljes adósságom
		myDebt = playerCommand.getSender().equals(getUserName()) ? BigDecimal.ZERO : myDebt.add(playerCommand.getRaiseAmount());
	}

	public void receivedFoldPlayerCommand(PlayerCommand playerCommand) {
		if (youAreNth > playerCommand.getWhosQuit()) {
			--youAreNth;
		}
	}
	
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

package hu.elte.bfw1p6.poker.client.model;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemPlayerCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public class MainGameModel {

	private PokerSession pokerSession;
	private PokerRemote pokerRemote;

	private PokerTable pokerTable;
	
	private CommunicatorController communicatorController;

	/**
	 * Hanyadik vagyok a körben.
	 */
	private int youAreNth;

	/**
	 * Hány játékos van velem együtt.
	 */
	private int players;

	/**
	 * A tartozásom az asztal felé, amit <b>CALL</b> vagy <b>RAISE</b> esetén meg kell adnom.
	 */
	private BigDecimal myDebt;
	
	public MainGameModel(CommunicatorController communicatorController) {
		this.pokerSession = Model.getInstance().getPokerSession();
		this.pokerRemote = RMIRepository.getInstance().getPokerRemote();
		this.youAreNth = -1;
		this.pokerTable = ConnectTableHelper.getInstance().getPokerTable();
		this.myDebt = pokerTable.getDefaultPot();
		this.communicatorController = communicatorController;

		System.out.println("Ki vagyok: " + getUserName());
	}

	public void connectToTable(RemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException, PokerUnauthenticatedException {
		pokerRemote.connectToTable(pokerSession.getId(), pokerTable, observer);
	}

	public void sendCommandToTable(PlayerHoldemCommand playerHoldemCommand) throws RemoteException, PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		playerHoldemCommand.setSender(pokerSession.getPlayer().getUserName());
		pokerRemote.sendPlayerCommand(pokerSession.getId(), pokerTable, communicatorController, playerHoldemCommand);
		pokerSession.refreshBalance(pokerRemote.refreshBalance(pokerSession.getId()));
		System.out.println("uj balance: " + pokerSession.getPlayer().getBalance());
	}

	public String getUserName() {
		return pokerSession.getPlayer().getUserName();
	}

	/**
	 * Ha BLIND utasítás jött a szervertől
	 * @param houseHoldemCommand a szerver utasítás
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws PokerUnauthenticatedException 
	 */
	public void blind(CommunicatorController commController, HouseHoldemCommand houseHoldemCommand) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		myDebt = pokerTable.getDefaultPot();
		youAreNth = houseHoldemCommand.getNthPlayer();
		players = houseHoldemCommand.getPlayers();
		// első körben az a dealer, aki elsőként csatlakozott, roundonként +1
		System.out.println("Hanyadik játékos vagy a szerveren: " + youAreNth);
		System.out.println("Aktuális dealer: " + houseHoldemCommand.getDealer());
		if (areYouTheSmallBlind(houseHoldemCommand)) {
			System.out.println("Betettem a kis vakot");
			smallBlind();
		} else if (areYouTheBigBlind(houseHoldemCommand)) {
			System.out.println("Betettem a nagy vakot");
			bigBlind();
		}
		// nagyvaktól eggyel balra ülő kezd
		System.out.println("Az éppen soron levő játékos: " + houseHoldemCommand.getWhosOn());
	}

	public void setMyDebt(BigDecimal myDebt) {
		this.myDebt = myDebt;
	}

	/**
	 * A vakot rakja be az asztalra
	 * @param divider ha nagy vakot rakunk be, akkor az értéke 1 legyen, ha kis vakot, akkor az értéke 2 legyen. Vagy lehet boolean, és akkor convert to int, majd +1...
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws PokerUnauthenticatedException 
	 */
	private void tossBlind(int divider) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		if (divider == 0) return;
		BigDecimal amount = pokerTable.getDefaultPot().divide(new BigDecimal(divider));
		myDebt = myDebt.subtract(amount);
		sendPlayerCommand(HoldemPlayerCommandType.BLIND, amount, null, -1);
	}

	/**
	 * Kis vakot rakja be.
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws PokerUnauthenticatedException 
	 */
	@Deprecated
	private void smallBlind() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		BigDecimal amount = pokerTable.getDefaultPot().divide(new BigDecimal(2));
		myDebt = myDebt.subtract(amount);
		sendPlayerCommand(HoldemPlayerCommandType.BLIND, amount, null, -1);
	}

	/**
	 * Nagy vakot rakja be.
	 * @throws PokerUserBalanceException 
	 * @throws PokerDataBaseException 
	 * @throws PokerUnauthenticatedException 
	 */
	@Deprecated
	private void bigBlind() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		myDebt = myDebt.subtract(pokerTable.getDefaultPot());
		sendPlayerCommand(HoldemPlayerCommandType.BLIND, pokerTable.getDefaultPot(), null, -1);
	}


	/**
	 * A dealer mellett közvetlenül balra ülő játékos köteles kis vakot betenni.
	 * @return ha nekem kell betenni a kis vakot, akkor true, különben false.
	 */
	private boolean areYouTheSmallBlind(HouseHoldemCommand houseHoldemCommand) {
		return youAreNth == ((houseHoldemCommand.getDealer() + 1) % players);
	}

	/**
	 * A dealer mellett kettővel balra ülő játékos köteles nagy vakot betenni.
	 * @return ha nekem kell betenni a nagy vakot, akkor true, különben false.
	 */
	private boolean areYouTheBigBlind(HouseHoldemCommand houseHoldemCommand) {
		return youAreNth == ((houseHoldemCommand.getDealer() + 2) % players);
	}

	public void call() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		BigDecimal amount = BigDecimal.ZERO.add(myDebt);
		myDebt = myDebt.subtract(amount);
		sendPlayerCommand(HoldemPlayerCommandType.CALL, amount, null, -1);
	}

	public void check() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		sendPlayerCommand(HoldemPlayerCommandType.CHECK, null, null, -1);
	}

	public void raise(BigDecimal amount) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		sendPlayerCommand(HoldemPlayerCommandType.RAISE, myDebt, amount, -1);
	}

	public void fold() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		int tempNth = youAreNth;
		youAreNth = -1;
		sendPlayerCommand(HoldemPlayerCommandType.FOLD, null, null, tempNth);
	}

	public void quit() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		sendPlayerCommand(HoldemPlayerCommandType.QUIT, null, null, youAreNth);		
	}

	public void receivedFoldCommand(PlayerHoldemCommand playerHoldemCommand) {
		if (youAreNth > playerHoldemCommand.getWhosQuit()) {
			--youAreNth;
		}
	}

	public void receivedRaiseCommand(PlayerHoldemCommand playerHoldemCommand) {
		// és mi van ha én magam emeltem...
		// ha én magam emeltem, akkor a szerver elszámolta a teljes adósságom
		myDebt = !playerHoldemCommand.getSender().equals(getUserName()) ? myDebt.add(playerHoldemCommand.getRaiseAmount()) : BigDecimal.ZERO;
	}

	public void receivedQuitCommand(PlayerHoldemCommand playerHoldemCommand) {
		if (youAreNth > playerHoldemCommand.getWhosQuit()) {
			--youAreNth;
		}
	}
	
	public BigDecimal getMyDebt() {
		return myDebt;
	}
	
	public int getYouAreNth() {
		return youAreNth;
	}
	

	private void sendPlayerCommand(HoldemPlayerCommandType type, BigDecimal callAmount, BigDecimal raiseAmount, Integer whosQuit) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		
		PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(type, callAmount, raiseAmount, whosQuit);
		try {
			sendCommandToTable(playerHoldemCommand);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*new Thread() {

			@Override
			public void run() {
				try {
					PlayerHoldemCommand playerHoldemCommand = new PlayerHoldemCommand(type, callAmount, raiseAmount, whosQuit);
					sendCommandToTable(communicatorController, playerHoldemCommand);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (PokerUnauthenticatedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PokerDataBaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PokerUserBalanceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}}.start();*/
	}

	public PokerPlayer getPlayer() {
		return pokerSession.getPlayer();
	}

	public void receivedBlindCommand(PlayerHoldemCommand playerHoldemCommand) {
		// TODO Auto-generated method stub
		
	}

	public void receivedCallCommand(PlayerHoldemCommand playerHoldemCommand) {
		// TODO Auto-generated method stub
		
	}

	public void receivedCheckCommand(PlayerHoldemCommand playerHoldemCommand) {
		// TODO Auto-generated method stub
		
	}

	public void player(HouseHoldemCommand houseHoldemCommand) {
		pokerSession.getPlayer().setCards(new Card[]{houseHoldemCommand.getCard1(), houseHoldemCommand.getCard2()});
	}
}

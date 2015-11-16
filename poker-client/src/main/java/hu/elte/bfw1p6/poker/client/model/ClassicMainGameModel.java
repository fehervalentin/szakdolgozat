package hu.elte.bfw1p6.poker.client.model;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.List;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;

/**
 * A póker játék kliens oldali játék közbeni classic modelje.
 * @author feher
 *
 */
public class ClassicMainGameModel extends AbstractMainGameModel {

	public ClassicMainGameModel(CommunicatorController communicatorController) {
		super(communicatorController);
	}
	
	@Override
	protected void tossBlind(Boolean bigBlind) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		BigDecimal amount = pokerTable.getDefaultPot().divide(new BigDecimal(bigBlind ? 1 : 2));
		myDebt = myDebt.subtract(amount);
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpBlindCommand(amount);
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendCallCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		BigDecimal amount = BigDecimal.ZERO.add(myDebt);
		myDebt = myDebt.subtract(amount);
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpCallCommand(amount);
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendCheckCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpCheckCommand();
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendRaiseCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpRaiseCommand(myDebt, pokerTable.getDefaultPot());
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendFoldCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpFoldCommand(youAreNth);
		sendCommandToTable(playerCommand);
	}

	@Override
	public void sendQuitCommand() throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpQuitCommand(youAreNth);
		sendCommandToTable(playerCommand);	
	}

	/**
	 * CHANGE típusú utasítás küldése a szervernek.
	 * @param markedCards a cserélendő kártyák sorszámai
	 * @throws PokerUnauthenticatedException
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 * @throws RemoteException
	 */
	public void sendChangeCommand(List<Integer> markedCards) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpChangeCommand(markedCards);
		sendCommandToTable(playerCommand);
	}

	/**
	 * DEAL2 típusú utasítás érkezett a szervertől.
	 * @param classicHouseCommand az utasítás
	 */
	public void receivedDeal2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		pokerSession.getPlayer().setCards(classicHouseCommand.getCards());
	}
}
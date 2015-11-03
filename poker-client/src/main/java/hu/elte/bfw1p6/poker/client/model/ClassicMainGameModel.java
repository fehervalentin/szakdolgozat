package hu.elte.bfw1p6.poker.client.model;

import java.math.BigDecimal;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;

public class ClassicMainGameModel extends AbstractMainGameModel {

	public ClassicMainGameModel(CommunicatorController communicatorController) {
		super(communicatorController);
	}
	
	@Override
	protected void tossBlind(Boolean bigBlind) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		BigDecimal amount = pokerTable.getDefaultPot().divide(new BigDecimal(bigBlind ? 1 : 2));
		myDebt = myDebt.subtract(amount);
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpBlindCommand(amount);
		sendPlayerCommand(playerCommand);
	}

	@Override
	public void call() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		BigDecimal amount = BigDecimal.ZERO.add(myDebt);
		myDebt = myDebt.subtract(amount);
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpCallCommand(amount);
		sendPlayerCommand(playerCommand);
	}

	@Override
	public void check() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpCheckCommand();
		sendPlayerCommand(playerCommand);
	}

	@Override
	public void raise(BigDecimal amount) throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpRaiseCommand(myDebt, amount);
		sendPlayerCommand(playerCommand);
	}

	@Override
	public void fold() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		int tempNth = youAreNth;
		youAreNth = -1;
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpFoldCommand(tempNth);
		sendPlayerCommand(playerCommand);
	}

	@Override
	public void quit() throws PokerUnauthenticatedException, PokerDataBaseException, PokerUserBalanceException {
		ClassicPlayerCommand playerCommand = new ClassicPlayerCommand();
		playerCommand.setUpQuitCommand(youAreNth);
		sendPlayerCommand(playerCommand);	
	}

}

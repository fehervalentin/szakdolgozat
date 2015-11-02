package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.HousePokerCommand;
import hu.elte.bfw1p6.poker.command.PlayerPokerCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHousePokerCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerPokerCommand;
import hu.elte.bfw1p6.poker.command.type.ClassicHousePokerCommandType;
import hu.elte.bfw1p6.poker.command.type.ClassicPlayerPokerCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public class ClassicPokerTableServer extends AbstractPokerTableServer<ClassicHousePokerCommandType, ClassicHousePokerCommand, ClassicPlayerPokerCommandType, ClassicPlayerPokerCommand> {

	private static final long serialVersionUID = 804318360089503038L;

	/**
	 * Aktuális utasítás típusa.
	 */
	private ClassicHousePokerCommandType actualHouseCommandType;

	protected ClassicPokerTableServer(PokerTable pokerTable) throws RemoteException {
		super(pokerTable);
	}

	@Override
	protected void startRound() {
		//ha elegen vagyunk az asztalnál, akkor indulhat a játék
		if (clients.size() >= minPlayer) {
			prepareNextRound();
			//a vakokat kérem be legelőször
			//actualHoldemHouseCommandType = HoldemHouseCommandType.BLIND;
			//			actualHouseCommandType = ClassicHouseCommandType.values()[0];
			// be kell kérni a vakokat
			collectBlinds();
			// két lap kézbe
			dealCardsToPlayers(5);
		}
	}

	@Override
	protected void nextRound() throws RemoteException {
		// ha már kijött a river és az utolsó körben (rivernél) már mindenki nyilatkozott legalább egyszer, akkor új játszma kezdődik
		System.out.println("VotedPlayers: " + votedPlayers);
		System.out.println("Players in round: " + playersInRound);
		if (playersInRound == 1 || (actualHouseCommandType == ClassicHousePokerCommandType.BLIND && votedPlayers >= playersInRound)) {
			//TODO: itt is kell értékelni, hogy ki nyert
			startRound();
		} else {
			// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
			if (votedPlayers >= playersInRound) {
				ClassicHousePokerCommand classicHouseCommand = new ClassicHousePokerCommand();
				// flopnál, turnnél, rivernél mindig a kisvak kezdi a gondolkodást! (persze kivétel, ha eldobta a lapjait, de akkor úgy is lecsúsznak a helyére
				whosOn = (dealer + 1 + foldCounter) % playersInRound;
				switch (actualHouseCommandType.getActual()) {
				case BET: {
					classicHouseCommand.setUpBetCommand(whosOn);
					break;
				}
				case CHANGE: {
					classicHouseCommand.setUpChangeCommand(whosOn);
					break;
				}
				case BET2: {
					classicHouseCommand.setUpBet2Command(whosOn);
					break;
				}
				case WINNER: {
					winner(classicHouseCommand);
				}
				default:
					break;
				}
				System.out.println("Next round");
				notifyClients(classicHouseCommand);
				nextStep();
				votedPlayers = 0;
			}
		}

	}

	@Override
	protected void winner(HousePokerCommand<ClassicHousePokerCommandType> houseCommand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected HousePokerCommand<ClassicHousePokerCommandType> getNewCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void receivePlayerCommand(RemoteObserver client,
			PlayerPokerCommand<ClassicPlayerPokerCommandType> playerCommand)
					throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		// TODO Auto-generated method stub
		
	}

}

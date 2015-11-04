package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import hu.elte.bfw1p6.poker.command.classic.type.ClassicHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

public class ClassicPokerTableServer extends AbstractPokerTableServer {

	private static final long serialVersionUID = 3009724030721806069L;

	/**
	 * Épp milyen utasítást fog kiadni a szerver (hol tartunk a körben).
	 */
	private ClassicHouseCommandType actualClassicHouseCommandType;

	protected ClassicPokerTableServer(PokerTable pokerTable) throws RemoteException {
		super(pokerTable);
	}
	
	@Override
	protected void prepareNewRound() {
		actualClassicHouseCommandType = ClassicHouseCommandType.values()[0];
	}

	@Override
	protected void nextStep() {
		actualClassicHouseCommandType = ClassicHouseCommandType.values()[(actualClassicHouseCommandType.ordinal() + 1) % ClassicHouseCommandType.values().length];
	}
	
	@Override
	protected HouseCommand houseDealCommandFactory(Card[] cards) {
		ClassicHouseCommand classicHouseCommand = new ClassicHouseCommand();
		classicHouseCommand.setUpDealCommand(cards, whosOn);
		return classicHouseCommand;
	}
	
	@Override
	protected HouseCommand houseBlindCommandFactory(int nthPlayer, int players, int dealer, int whosOn, List<String> clientsNames) {
		ClassicHouseCommand classicHouseCommand = new ClassicHouseCommand();
		classicHouseCommand.setUpBlindCommand(nthPlayer, clients.size(), dealer, whosOn, clientsNames);
		return classicHouseCommand;
	}

	@Override
	protected void nextRound() throws RemoteException {
		// ha már kijött a river és az utolsó körben (rivernél) már mindenki nyilatkozott legalább egyszer, akkor új játszma kezdődik
				System.out.println("VotedPlayers: " + votedPlayers);
				System.out.println("Players in round: " + playersInRound);
				if (playersInRound == 1 || (actualClassicHouseCommandType == ClassicHouseCommandType.values()[0] && votedPlayers >= playersInRound)) {
					//TODO: itt is kell értékelni, hogy ki nyert
					startRound();
				} else {
					// ha már mindenki nyilatkozott legalább egyszer (raise esetén újraindul a kör...)
					if (votedPlayers >= playersInRound) {
						ClassicHouseCommand classicHouseCommand = new ClassicHouseCommand();
						// flopnál, turnnél, rivernél mindig a kisvak kezdi a gondolkodást! (persze kivétel, ha eldobta a lapjait, de akkor úgy is lecsúsznak a helyére
						whosOn = (dealer + 1 + foldCounter) % playersInRound;
						switch (actualClassicHouseCommandType) {
						case BET: {
							classicHouseCommand.setUpBetCommand(whosOn);
							break;
						}
						case CHANGE: {
							classicHouseCommand.setUpChangeCommand(whosOn);
							break;
						}
						case DEAL2: {
							//TODO: itt osztom ki az új lapokat a játékosokat, de ahhoz tudnom kell, hogy ki milyen lapot akar kicserélni...
							//classicHouseCommand.setUpDeal2Command(cards, whosOn);
							break;
						}
						case BET2: {
							classicHouseCommand.setUpBet2Command(whosOn);
							break;
						}
						case WINNER: {
							winner(classicHouseCommand);
							break;
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
	protected void receivePlayerCommand(RemoteObserver client, PlayerCommand playerCommand) throws PokerDataBaseException, PokerUserBalanceException, RemoteException {
		// ha valid klienstől érkezik üzenet, azt feldolgozzuk, körbeküldjük
		if (clients.contains(client)) {
			ClassicPlayerCommand classicPlayerCommand = (ClassicPlayerCommand)playerCommand;
			switch(classicPlayerCommand.getPlayerCommandType()) {
			case BLIND: {
				receivedBlindPlayerCommand(classicPlayerCommand);
				break;
			}
			case CALL: {
				receivedCallPlayerCommand(classicPlayerCommand);
				break;
			}
			case CHECK: {
				receivedCheckPlayerCommand(classicPlayerCommand);
				break;
			}
			case FOLD: {
				receivedFoldPlayerCommand(classicPlayerCommand);
				break;
			}
			case RAISE: {
				receivedRaisePlayerCommand(classicPlayerCommand);
				break;
			}
			case QUIT: {
				receivedQuitPlayerCommand(client, classicPlayerCommand);
				break;
			}
			case CHANGE: {
				receivedChangePlayerCommand(classicPlayerCommand);
			}
			default:
				break;
			}
			endOfReceivePlayerCommand(classicPlayerCommand);
		}
	}
	
	private void receivedChangePlayerCommand(ClassicPlayerCommand classicPlayerCommand) {
		
	}

	@Override
	protected void winner(HouseCommand houseCommand) {
		// TODO Auto-generated method stub

	}
}
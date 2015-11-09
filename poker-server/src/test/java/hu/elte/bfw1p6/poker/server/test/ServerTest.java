package hu.elte.bfw1p6.poker.server.test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.server.HoldemPokerTableServer;

public class ServerTest {

	@Test (expected = PokerTooMuchPlayerException.class)
	public void TooMuchPlayerTest() throws RemoteException, PokerTooMuchPlayerException {
		int maxPlayers = 2;
		List<PokerRemoteObserver> clients = new ArrayList<>();
		PokerTable pokerTable = mock(PokerTable.class);
		HoldemPokerTableServer sv = new HoldemPokerTableServer(pokerTable);
		
		when(pokerTable.getMaxPlayers()).thenReturn(maxPlayers);
		
		for (int i = 0; i <= pokerTable.getMaxPlayers(); i++) {
			clients.add(mock(PokerRemoteObserver.class));
			sv.join(clients.get(i), null);
		}
	}
	
	@Test
	public void CommandTest() throws RemoteException, PokerTooMuchPlayerException {
		int maxPlayers = 2;
		PokerTable pokerTable = mock(PokerTable.class);
		HoldemPokerTableServer sv = new HoldemPokerTableServer(pokerTable);
		
		when(pokerTable.getMaxPlayers()).thenReturn(maxPlayers);
		
		for (int i = 0; i < pokerTable.getMaxPlayers(); i++) {
			PokerRemoteObserver client = new PokerRemoteObserver() {
				
				@Override
				public void update(Object updateMsg) throws RemoteException {
					Assert.assertThat(updateMsg, instanceOf(HoldemHouseCommand.class));
//					HouseHoldemCommand command = (HouseHoldemCommand)updateMsg;
//					Assert.assertTrue(command.getHouseCommandType() == HoldemHouseCommandType.BLIND);
				}
			};
//			sv.join(client);
		}
	}
}

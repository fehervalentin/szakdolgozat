package hu.elte.bfw1p6.poker.server.test;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import org.junit.Test;
import static org.mockito.Mockito.*;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.server.HoldemPokerTableServer;

public class ServerTest {

	//	private CommunicatorController a;

	@Test (expected = PokerTooMuchPlayerException.class)
	public void joinTest() throws RemoteException, PokerTooMuchPlayerException {
		RemoteObserver a = new RemoteObserver() {

			@Override
			public void update(Object observable, Object updateMsg) throws RemoteException {
				// TODO Auto-generated method stub

			}
		};
		RemoteObserver b = new RemoteObserver() {

			@Override
			public void update(Object observable, Object updateMsg) throws RemoteException {
				// TODO Auto-generated method stub

			}
		};
		RemoteObserver c = new RemoteObserver() {

			@Override
			public void update(Object observable, Object updateMsg) throws RemoteException {
				// TODO Auto-generated method stub

			}
		};
		PokerTable pokerTable = mock(PokerTable.class);
		when(pokerTable.getDefaultPot()).thenReturn(BigDecimal.TEN);
		when(pokerTable.getId()).thenReturn(12);
		when(pokerTable.getMaxPlayers()).thenReturn(2);
		HoldemPokerTableServer sv = new HoldemPokerTableServer(pokerTable);
		sv.join(a);
		sv.join(b);
		sv.join(c);
	}
}

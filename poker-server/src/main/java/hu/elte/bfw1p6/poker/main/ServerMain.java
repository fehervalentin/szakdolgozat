package hu.elte.bfw1p6.poker.main;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.server.PokerRemoteImpl;

public class ServerMain {

	public static void main(String[] args) {
		try {
			new PokerRemoteImpl();
		} catch (RemoteException | PokerDataBaseException e) {
			e.printStackTrace();
		}
	}
}

package hu.elte.bfw1p6.poker.main;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.server.PokerRemoteImpl;

/**
 * A szerver belépési pontja.
 * @author feher
 *
 */
public class ServerMain {

	public static void main(String[] args) {
		try {
			new PokerRemoteImpl(args);
		} catch (RemoteException | PokerDataBaseException e) {
			e.printStackTrace();
		}
	}
}
package hu.elte.bfw1p6.poker.main;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.server.PokerRemoteImpl;

public class ServerMain {

	public static void main(String[] args) {
		try {
			PokerRemote pokerRemote = new PokerRemoteImpl();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

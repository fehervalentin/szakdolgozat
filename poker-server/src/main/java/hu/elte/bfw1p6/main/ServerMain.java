package hu.elte.bfw1p6.main;


import java.rmi.RemoteException;

import hu.elte.bfw1p6.login.PokerLoginRemoteImpl;
import hu.elte.bfw1p6.rmi.PokerRemote;
import hu.elte.bfw1p6.rmi.PokerRemoteImpl;
import hu.elte.bfw1p6.rmi.security.PokerLoginRemote;


public class ServerMain {

	public static void main(String[] args) {
		PokerRemote pokerRemote = new PokerRemoteImpl();
//		System.out.println("A szerver elindult");
		try {
			PokerLoginRemote pokerLogin = new PokerLoginRemoteImpl(pokerRemote);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//PokerRemoteImpl pokerServer = new PokerRemoteImpl();
	}

}

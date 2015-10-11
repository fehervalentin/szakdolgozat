package hu.elte.bfw1p6.poker.main;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.login.PokerLoginRemoteImpl;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.rmi.PokerRemoteImpl;
import hu.elte.bfw1p6.poker.rmi.security.PokerLoginRemote;


public class ServerMain {

	public static void main(String[] args) {
//		System.out.println("Working Directory = " + System.getProperty("user.dir"));
//		String valami = "file:///" + System.getProperty("user.dir") + "\\src\\main\\java\\hu\\elte\\bfw1p6\\poker\\server\\";
//		System.out.println(valami);
//		System.setProperty("java.rmi.server.codebase", valami);
		
		PokerRemote pokerRemote = PokerRemoteImpl.getInstance();
//		System.out.println("A szerver elindult");
		try {
			PokerLoginRemote pokerLogin = new PokerLoginRemoteImpl(pokerRemote);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package hu.elte.bfw1p6.poker.main;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.server.PokerRemoteImpl;

public class ServerMain {

	public static void main(String[] args) {
		//		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		//		String valami = "file:///" + System.getProperty("user.dir") + "\\src\\main\\java\\hu\\elte\\bfw1p6\\poker\\server\\";
		//		System.out.println(valami);
		//		System.setProperty("java.rmi.server.codebase", valami);

		//		System.out.println("A szerver elindult");
		try {
			PokerRemote pokerRemote = new PokerRemoteImpl();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package hu.elte.bfw1p6.poker.main;

import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.login.PokerLoginRemoteImpl;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.rmi.PokerRemoteImpl;
import hu.elte.bfw1p6.poker.rmi.security.PokerLoginRemote;


public class ServerMain {

	public static void main(String[] args) {
//		System.out.println("a szerver elindult");
//		User user = new User("Flat");
//		user.setPassword("1234");
//		user.setRegDate(new Date().getTime());
//		user.setSalt("#!%e'");
//		user.setAmount(new BigDecimal(20));
//		UserDAO userdao = new UserDAO();
//		userdao.persistUser(user);
		
		PokerRemote pokerRemote = new PokerRemoteImpl();
//		System.out.println("A szerver elindult");
		try {
			PokerLoginRemote pokerLogin = new PokerLoginRemoteImpl(pokerRemote);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//PokerRemoteImpl pokerServer = new PokerRemoteImpl();
		System.out.println("a szerver leállt");
	}

}

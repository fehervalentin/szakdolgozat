package hu.elte.bfw1p6.poker.client.model;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;
import hu.elte.bfw1p6.poker.rmi.security.PokerLoginRemote;


public class Model {

	private PokerLoginRemote pokerLoginRemote;
	private PokerRemote pokerRemote;
	
	private UUID sessionId;

	public Model() {
		pokerLoginRemote = RMIRepository.getInstance().getPokerLoginRemote();
	}

	public void login(String username, String password) {
		try {
			sessionId = pokerLoginRemote.login(username, password);
			pokerRemote = pokerLoginRemote.getPokerRemote(sessionId);
//			RMIRepository.getInstance().setPokerRemote(pokerRemote);
//			pokerRemote = RMIRepository.getInstance().getPokerRemote();
			System.out.println(pokerRemote.sayHello());
		} catch (RemoteException | SecurityException | PokerInvalidUserException e) {
			e.printStackTrace();
		}
	}

	public boolean registration(String username, String password) {
		try {
			pokerLoginRemote.registration(username, password);
			return true;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void createTable(PTable t) throws RemoteException {
		login("admin", "admin");
		pokerRemote.createTable(t);
	}

	public List<PTable> getTables() {
		List<PTable> tables = null;
		try {
			tables = pokerRemote.getTables();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tables;
	}

	/*public List<Table> getTables() {
		return pokerRemote.getTables();
	}*/
}

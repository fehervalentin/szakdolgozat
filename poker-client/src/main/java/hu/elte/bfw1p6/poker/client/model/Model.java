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
	
	private static Model instance = null;

	private PokerLoginRemote pokerLoginRemote;
	private PokerRemote pokerRemote;

	private UUID sessionId;

	private Model() {
		pokerLoginRemote = RMIRepository.getInstance().getPokerLoginRemote();
	}
	
	public static Model getInstance() {
		if(instance == null) {
			instance = new Model();
		}
		return instance;
	}

	public void login(String username, String password) throws RemoteException, PokerInvalidUserException {
		sessionId = pokerLoginRemote.login(username, password);
		pokerRemote = pokerLoginRemote.getPokerRemote(sessionId);
		//			RMIRepository.getInstance().setPokerRemote(pokerRemote);
		//			pokerRemote = RMIRepository.getInstance().getPokerRemote();
		//System.out.println(pokerRemote.sayHello());
	}

	public void registration(String username, String password) throws RemoteException {
		pokerLoginRemote.registration(username, password);
	}

	public void createTable(PTable t) throws RemoteException, PokerInvalidUserException {
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

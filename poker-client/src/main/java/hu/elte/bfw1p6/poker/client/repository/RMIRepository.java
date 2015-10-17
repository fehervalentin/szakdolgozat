package hu.elte.bfw1p6.poker.client.repository;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.properties.PokerProperties;
import hu.elte.bfw1p6.poker.rmi.PokerRemote;

public class RMIRepository {
	private static RMIRepository instance = null;

	//	private Registry registry;
	private PokerRemote pokerRemote;
	private PokerSession pokerSession;

	private final String SVNAME;
	private final String PORT;

	private PokerProperties pokerProperties;

	private RMIRepository() {
		pokerProperties = PokerProperties.getInstance();
		System.out.println("Kliens: " + this.toString());
		SVNAME =  pokerProperties.getProperty("name");
		PORT = pokerProperties.getProperty("rmiport");
		try {
			//			registry = LocateRegistry.getRegistry(Integer.valueOf(pokerProperties.getProperty("rmiport")));
			//			pokerRemote = (PokerRemote) registry.lookup(pokerProperties.getProperty("name"));

			pokerRemote = (PokerRemote) Naming.lookup("//localhost:" + PORT + "/" + SVNAME);

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static RMIRepository getInstance() {
		if(instance == null) {
			instance = new RMIRepository();
		}
		return instance;
	}

	public PokerRemote getPokerRemote() {
		return pokerRemote;
	}

	public void setPokerSession(PokerSession pokerSession) {
		this.pokerSession = pokerSession;
	}

	public PokerSession getPokerSession() {
		return pokerSession;
	}

	public PokerSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}
}

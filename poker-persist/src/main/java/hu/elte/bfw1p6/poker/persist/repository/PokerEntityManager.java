package hu.elte.bfw1p6.poker.persist.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PokerEntityManager {
	private static PokerEntityManager instance = null;

	private final static String PU_NAME = "Poker-PU";

	private EntityManagerFactory emf;

	private PokerEntityManager() {
//		EntityManagerHelper.getEntityManager();
		emf = Persistence.createEntityManagerFactory(PU_NAME);
	}

	public static PokerEntityManager getInstance() {
		System.out.println("elkérem");
		if(instance == null) {
			instance = new PokerEntityManager();
		}
		return instance;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}
}

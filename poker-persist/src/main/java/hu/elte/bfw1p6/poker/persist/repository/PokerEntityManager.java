package hu.elte.bfw1p6.poker.persist.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PokerEntityManager {
	private static PokerEntityManager instance = null;
	
	private final static String PU_NAME = "Poker-PU";

	private EntityManagerFactory emf;
	private EntityManager em;
	
	private PokerEntityManager() {
	}

	public static PokerEntityManager getInstance() {
		System.out.println("elkérem");
		if(instance == null) {
			instance = new PokerEntityManager();
		}
		return instance;
	}
	
	public EntityManager getEntityManager() {
		emf = Persistence.createEntityManagerFactory(PU_NAME);
		em = emf.createEntityManager();
		return em;
	}
}

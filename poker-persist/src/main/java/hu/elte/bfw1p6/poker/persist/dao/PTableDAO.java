package hu.elte.bfw1p6.poker.persist.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.persist.repository.EntityManagerHelper;
import hu.elte.bfw1p6.poker.persist.repository.PokerEntityManager;


public class PTableDAO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private EntityManagerFactory emf;
	private EntityManager em;
	
	public PTableDAO() {
		em = EntityManagerHelper.getEntityManager();
		//emf = PokerEntityManager.getInstance().getEntityManagerFactory();
		//em = emf.createEntityManager();
	}
	
	public void persistTable(PTable t) {
//		em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
		em.close();
	}
	
	public List<PTable> getTables() {
//		em = emf.createEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT t FROM PTABLE t");
		em.close();
		return (List<PTable>)query.getResultList();
	}
}

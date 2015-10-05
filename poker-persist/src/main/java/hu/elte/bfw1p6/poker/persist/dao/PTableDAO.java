package hu.elte.bfw1p6.poker.persist.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.model.entity.PTable;
import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.PokerEntityManager;


public class PTableDAO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EntityManagerFactory emf;
	private EntityManager em;
	
	public PTableDAO() {
		emf = PokerEntityManager.getInstance().getEntityManagerFactory();
		em = emf.createEntityManager();
	}
	
	public void persistTable(PTable t) {
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
		em.close();
	}
	
	public void modifyPassword(int id, String oldPassword, String newPassword) {
		User u = (User)em.find(User.class, id);
		String salt = generateSalt();
		u.setSalt(salt);
		u.setPassword(BCrypt.hashpw(newPassword, salt));
		em.getTransaction().begin();
		em.persist(u);
		em.getTransaction().commit();
		em.close();
	}
	
	private String generateSalt() {
		return BCrypt.gensalt();
	}

	public List<PTable> getTables() {
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT t FROM PTABLE t");
		return (List<PTable>)query.getResultList();
	}
}

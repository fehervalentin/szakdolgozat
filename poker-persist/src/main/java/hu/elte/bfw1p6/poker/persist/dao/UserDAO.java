package hu.elte.bfw1p6.poker.persist.dao;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.PokerEntityManager;


public class UserDAO {

	private EntityManagerFactory emf;
	private EntityManager em;
	
	public UserDAO() {
		emf = PokerEntityManager.getInstance().getEntityManagerFactory();
		em = emf.createEntityManager();
	}
	
	public void persistUser(String username, String password) {
		User u = new User(username);
		String salt = generateSalt();
		u.setSalt(salt);
		u.setPassword(BCrypt.hashpw(u.getPassword(), salt));
		u.setAmount(new BigDecimal(0));
		u.setRegDate((new Date()).getTime());
		em.getTransaction().begin();
		em.persist(u);
		em.getTransaction().commit();
		em.close();
//		emf.close();
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
//		emf.close();
	}
	
	private String generateSalt() {
		return BCrypt.gensalt();
	}
}

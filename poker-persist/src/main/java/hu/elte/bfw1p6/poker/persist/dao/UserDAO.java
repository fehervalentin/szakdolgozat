package hu.elte.bfw1p6.poker.persist.dao;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.mindrot.jbcrypt.BCrypt;

import hu.elte.bfw1p6.poker.model.entity.User;
import hu.elte.bfw1p6.poker.persist.repository.PokerEntityManager;


public class UserDAO {

	private EntityManagerFactory emf;
	private EntityManager em;

	public UserDAO() {
		emf = PokerEntityManager.getInstance().getEntityManagerFactory();
	}

	public void persistUser(String username, String password) {
		em = emf.createEntityManager();
		em.getTransaction().begin();
		User u = new User(username);
		//String salt = generateSalt();
		//u.setSalt(salt);
		u.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		u.setAmount(new BigDecimal(0));
		u.setRegDate((new Date()).getTime());
		em.persist(u);
		em.getTransaction().commit();
		em.close();
	}

	public void modifyPassword(int id, String oldPassword, String newPassword) {
		em = emf.createEntityManager();
		em.getTransaction().begin();
		User u = (User)em.find(User.class, id);
		u.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
		em.persist(u);
		em.getTransaction().commit();
		em.close();
	}

	public User findUserByUserName(String username) throws NoResultException {
		em = emf.createEntityManager();
		em.getTransaction().begin();
		User u = (User)em.createQuery("SELECT u FROM User u where u.userName = :paramUserName").setParameter("paramUserName", username).getSingleResult();
		em.close();
		return u;
	}
}

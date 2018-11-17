import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class DatabaseManager {
	
	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("UBP");
	
	public static Long addNewUser(UserData userData)
	{
		EntityManager em=emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(userData);
		em.flush();
		em.getTransaction().commit();
		em.close();
		return userData.getId();
	}
	
	public static Long getUserId(String name, String password)
	{
		EntityManager em=emf.createEntityManager();
		TypedQuery<UserData> q = em.createQuery("SELECT u FROM UserData u where u.name = :name",UserData.class);
		q.setParameter("name", name);
		UserData userData = q.getSingleResult();
		em.close();
		if(userData != null)
		{
			boolean check=userData.isCorrectPassword(password);
			if(check)
				return userData.getId();
			return null;
		}
		return null;
	}
	
	public static void changePassword(Long id, String newPassword)
	{
		EntityManager em=emf.createEntityManager();
		UserData userData = em.find(UserData.class, id);
		em.getTransaction().begin();
		userData.changePassword(newPassword);
		//em.merge(userData);
		em.flush();
		em.getTransaction().commit();
		em.close();
	}
	
	public static void changeName(Long id, String newName)
	{
		EntityManager em=emf.createEntityManager();
		UserData userData=em.find(UserData.class, id);
		em.getTransaction().begin();
		userData.changeName(newName);
		em.flush();
		em.getTransaction().commit();
		em.close();
	}
	
	public static String logInUser(String name, String password)
	{
		//EntityManager em=emf.createEntityManager();
		Long id=getUserId(name,password);
		if(id == null)
			return null;
		UserLogin userLogin = new UserLogin();
		userLogin.setUserId(id);
		try {
			String loginHash = CryptoUtils.hashPassword(null);
			userLogin.setLoginHash(loginHash);
			EntityManager em=emf.createEntityManager();
			em.getTransaction().begin();
			em.persist(userLogin);
			em.flush();
			em.getTransaction().commit();
			em.close();
			return loginHash;
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
	
}

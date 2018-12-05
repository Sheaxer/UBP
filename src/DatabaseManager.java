import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

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
	
	public static PublicKey getPublicKey(Long id)
	{
		EntityManager em=emf.createEntityManager();
		UserData userData=em.find(UserData.class, id);
		em.close();
		if(userData!=null)
			return userData.getPublicKey();
		return null;
	}
	
	public static PrivateKey getPrivateKey(Long id)
	{
		EntityManager em=emf.createEntityManager();
		UserData userData=em.find(UserData.class, id);
		em.close();
		if(userData!=null)
			return userData.getPrivateKey();
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
	
	public static String logInUser(Long id)
	{
		//EntityManager em=emf.createEntityManager();
		//Long id=getUserId(name,password);
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
	
	public static Long getUserIdFromHash(String loginHash)
	{
		EntityManager em=emf.createEntityManager();
		TypedQuery<UserLogin> q = em.createQuery("SELECT u from UserLogin u WHERE u.loginHash = :loginHash", UserLogin.class);
		q.setParameter("loginHash", loginHash);
		UserLogin userLogin = q.getSingleResult();
		em.close();
		if(userLogin != null)
		{
			return userLogin.getUserId();
		}
		return null;
	}
	
	public static void logoutUser(String loginHash)
	{
		EntityManager em=emf.createEntityManager();
		TypedQuery<UserLogin> q = em.createQuery("SELECT u from UserLogin u WHERE u.loginHash = :loginHash", UserLogin.class);
		q.setParameter("loginHash", loginHash);
		UserLogin userLogin = q.getSingleResult();
		if(userLogin != null)
		{
			em.getTransaction().begin();
			em.remove(userLogin);
			em.flush();
			em.getTransaction().commit();
			em.close();
		}
	}

	public static List<String> getOtherUsers(Long id) {
		// TODO Auto-generated method stub
		EntityManager em=emf.createEntityManager();
		TypedQuery<String> q = em.createQuery("SELECT u.name FROM UserData u WHERE u.id <> :f",String.class);
		q.setParameter("f", id);
		List<String> res = q.getResultList();
		em.close();
		return res;
	}
	
	public static Long getUserIdFromName(String name)
	{
		EntityManager em=emf.createEntityManager();
		TypedQuery<Long> q = em.createQuery("SELECT u.id FROM UserData u WHERE u.name = :name",Long.class);
		q.setParameter("name", name);
		Long ret = q.getSingleResult();
		em.close();
		return ret;
	}
	
	public static List<Object[]> getFilesOfUser(Long id)
	{
		EntityManager em=emf.createEntityManager();
		TypedQuery<UserFile> q = em.createQuery("SELECT f FROM UserFile f WHERE f.creator.creatorId = :a",UserFile.class);
		q.setParameter("a", id);
		List<UserFile> userList = q.getResultList();
		/*for(UserFile us: userList)
			System.out.println("I AM HERE " + us.getFileName());*/
		List<Object[]> retList = new ArrayList<>();
		
		for(int i=0; i< userList.size(); i++ )
		{
			Object[] o = new Object[3];
			TypedQuery<String> q1 = em.createQuery("SELECT u.name FROM UserData u WHERE u.id=:b",String.class);
			q1.setParameter("b", userList.get(i).getRecipient());
			String name = q1.getSingleResult();
			System.out.println("WE HAVE ONE JOB " + name + " " + userList.get(i).getCreator().createTime.toString() + " " + userList.get(i).getFileName());
			o[0] = name;
			o[1] = userList.get(i).getCreator().createTime;
			o[2] = userList.get(i).getFileName();
			retList.add(o);
		}
		em.close();
		return retList;
	}
	
	public static List<Object[]> getFilesForUser(Long id)
	{
		EntityManager em=emf.createEntityManager();
		TypedQuery<UserFile> q = em.createQuery("SELECT f FROM UserFile f WHERE f.recipient = :a",UserFile.class);
		q.setParameter("a", id);
		List<UserFile> userList = q.getResultList();
		
		
		
		List<Object[]> retList = new ArrayList<>();
		
		for(int i=0; i< userList.size(); i++ )
		{
			TypedQuery<String> q1 = em.createQuery("SELECT u.name FROM UserData u WHERE u.id=:b",String.class);
			q1.setParameter("b", userList.get(i).getCreator().creatorId);
			Object[] o = new Object[3];
			o[0] = q1.getSingleResult();
			o[1] = userList.get(i).getCreator().createTime;
			o[2] = userList.get(i).getFileName();
			retList.add(o);
		}
		em.close();
		return retList;
	}
	
	public static void addNewUserFile(Long creatorId, Long recipientId, String fileName, byte[] fileBytes)
	{
		EntityManager em=emf.createEntityManager();
		UserFile u= new UserFile();
		u.setCreator(creatorId);
		u.setFileBytes(fileBytes);
		u.setFileName(fileName);
		u.setRecipient(recipientId);
		em.getTransaction().begin();
		em.persist(u);
		em.getTransaction().commit();
		em.close();
	}
	
	public static byte[] getFileContent(Creator c)
	{
		EntityManager em = emf.createEntityManager();
		UserFile u = em.find(UserFile.class,c);
		em.close();
		return u.getFileBytes();
	}
	
	public static String getFileName(Creator c)
	{
		EntityManager em = emf.createEntityManager();
		UserFile u = em.find(UserFile.class, c);
		em.close();
		return u.getFileName();
	}
	
	/*public static String getFileName(Creator c)
	{
		EntityManager em = emf.createEntityManager();
		TypedQuery<String> q = em.createQuery("SELECT u.fileName FROM UserFile u WHERE u.id = :id", String.class);
		q.setParameter("id", id);
		String ret = q.getSingleResult();
		em.close();
		em.close();
		return ret;
	}*/
	
	//public static Object[] getFileData()
	
	public static String getUserNameFromId(Long id)
	{
		EntityManager em=emf.createEntityManager();
		TypedQuery<String> q = em.createQuery("SELECT u.name FROM UserData u WHERE u.id = :id",String.class);
		q.setParameter("id", id);
		String ret = q.getSingleResult();
		em.close();
		return ret;
	}
	
	public static boolean checkIfFileIsForUser(Creator c, Long id)
	{
		EntityManager em=emf.createEntityManager();
		UserFile f = em.find(UserFile.class, c);
		Long checkId = f.getRecipient();
		boolean result = checkId.equals(id);
		em.close();
		return result;
	}
	
	public static List<Comment> getComments(Creator c)
	{
		EntityManager em=emf.createEntityManager();
		UserFile u = em.find(UserFile.class, c);
		em.close();
		return u.getComments();
	}

	public static void addComment(Creator c, Long id, String message) {
		// TODO Auto-generated method stub
		EntityManager em=emf.createEntityManager();
		UserFile u = em.find(UserFile.class, c);
		if(u == null)
			return;
		em.getTransaction().begin();
		Comment com = new Comment();
		com.create(id, message);
		em.persist(com);
		u.addComment(com);
		em.getTransaction().commit();
		em.close();
		return;
	}
}

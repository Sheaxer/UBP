

import java.io.Serializable;
import java.lang.Long;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: UserData
 *
 */
@Entity
public class UserData implements Serializable {

	@Transient
	public static final int SALT_SIZE=16;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private static final long serialVersionUID = 1L;
	
	private String passwordHash;
	
	
	@Lob
	private byte[] publicKeyBytes;
	
	@Lob
	private byte[] privateKeyBytes;
	
	private String name;
	
	public UserData(String name, String password) throws Exception {
		super();
		
		this.name=name;
		this.passwordHash = CryptoUtils.hashPassword(password);	
	}   
	
	
	
	public UserData()
	{
		super();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public boolean isCorrectPassword(String testPassword)
	{
		try {
			if((this.passwordHash == null) || (this.passwordHash.isEmpty()))
				return true;
			boolean check = CryptoUtils.validatePassword(testPassword,this.passwordHash);
			return check;
			
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	public void changePassword(String oldPassword, String newPassword)
	{
		try {
			
			this.passwordHash = CryptoUtils.hashPassword(newPassword);
			
		}
		catch(Exception e)
		{
			return;
		}
	}
	
	public void changeName(String newName)
	{
		
		this.name = newName;
		
	}
		
	public PublicKey getPublicKey()
	{
		if(this.publicKeyBytes == null)
			return null;
		return(CryptoUtils.readPublicKey(this.publicKeyBytes));
	}
	
	public PrivateKey getPrivateKey()
	{
		if(this.privateKeyBytes == null)
			return null;
		try {
			return(CryptoUtils.readPrivateKey(this.privateKeyBytes));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void setKeys(KeyPair pair)
	{
		this.publicKeyBytes = pair.getPublic().getEncoded();
		this.privateKeyBytes = pair.getPrivate().getEncoded();
	}
	
	
}



import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: UserLogin
 *
 */
@Entity
@IdClass(UserLogin.class)
public class UserLogin implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public UserLogin() {
		super();
	}
	@Id
	private Long userId;
	
	@Id
	private String loginHash;
	
	public void setUserId(Long id)
	{
		this.userId = id;
	}
	
	public void setLoginHash(String loginHash)
	{
		this.loginHash = loginHash;
	}
	
	public String getLoginHash()
	{
		return this.loginHash;
	}
	
   
}

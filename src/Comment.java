


import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Comment
 *
 */
@Entity
public class Comment implements Serializable, Comparable<Comment> {

	
	private static final long serialVersionUID = 1L;

	public Comment() {
		super();
	}
	
	@EmbeddedId
	@AttributeOverrides({
	@AttributeOverride(name="creatorId", column=@Column(name="comment_creator_ID")),
	@AttributeOverride(name="createTime", column=@Column(name="comment_create_time"))
	})
	protected Creator creator;
	
	@Column(columnDefinition = "TEXT")
	private String message;
	
	@ManyToOne
	private UserFile file;
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public void create(Long creatorId,String message)
	{
		Creator c = new Creator();
		c.createTime = OffsetDateTime.now();
		c.creatorId = creatorId;
		this.message = message;
		this.creator = c;
	}
	
	public Long getCreatorId()
	{
		if(this.creator == null)
			return null;
		return this.creator.creatorId;
	}
	
	public OffsetDateTime getCreateTime()
	{
		if(this.creator == null)
			return null;
		return this.creator.createTime;
	}
	
	public void setFile(UserFile file)
	{
		this.file=file;
	}
	
	public UserFile getFile()
	{
		return this.file;
	}

	public int compareTo(Comment o) {
	    return this.creator.createTime.compareTo(o.creator.createTime);
	  }
}

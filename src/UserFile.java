

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: UserFile
 *
 */
@Entity

public class UserFile implements Serializable {

	
	private static final long serialVersionUID = 1L;

	
	@EmbeddedId
	private Creator creator;
	
	@Lob
	@Basic(fetch=FetchType.LAZY)
	private byte[] fileBytes;
	
	@OneToMany(mappedBy="file",fetch=FetchType.LAZY)
	private List<Comment> comments = new ArrayList<>();
	
	private String fileName;
	
	private Long recipient;
	
	public void setCreator(Creator c)
	{
		this.creator =c;
	}
	
	public void setCreator(Long id)
	{
		Creator c = new Creator();
		c.createTime = OffsetDateTime.now();
		c.creatorId = id;
		this.creator = c;
	}
	
	public void setCreator(Long id, OffsetDateTime time)
	{
		Creator c = new Creator();
		c.createTime = time;
		c.creatorId=id;
		this.creator=c;
	}
	
	public Creator getCreator()
	{
		return this.creator;
	}

	public byte[] getFileBytes() {
		return fileBytes;
	}




	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}




	public String getFileName() {
		return fileName;
	}




	public void setFileName(String fileName) {
		this.fileName = fileName;
	}




	public Long getRecipient() {
		return recipient;
	}




	public void setRecipient(Long recipient) {
		this.recipient = recipient;
	}

	public void addComment(Comment c)
	{
		this.comments.add(c);
		c.setFile(this);
	}
	
	public List<Comment> getComments()
	{
		Collections.sort(this.comments);
		return this.comments;
	}


	public UserFile() {
		
	}
   
}

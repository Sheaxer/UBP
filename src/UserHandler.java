
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
@WebServlet("/userSection")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10 // 500 MB
//2 GB
) // 3 GB
public class UserHandler extends HttpServlet {
	
	private static String DIR = "C:\\uploads";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
		
		HttpSession session = req.getSession(false);
		if(session == null)
		{
			resp.sendRedirect("index.jsp");
		}
		String loginHash = (String) session.getAttribute("loginHash");
		Long id = DatabaseManager.getUserIdFromHash(loginHash);
		//System.out.println("My id is" + id);
		List<String> otherUsers = DatabaseManager.getOtherUsers(id);
		JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
		
		
		
		JsonArrayBuilder userBuilder = Json.createArrayBuilder();
		
		for(String userName: otherUsers)
		{
			//System.out.println("Other name is" + userName);
			userBuilder.add(userName);
		}
		//System.out.println("JSON = " + userBuilder.build().toString());
		JsonArrayBuilder fileOfUserBuilder = Json.createArrayBuilder();
		
		List<Object[]> fileFromUserInfos = DatabaseManager.getFilesOfUser(id);
		
		for(Object[] fileInfo: fileFromUserInfos)
		{
			
			JsonObjectBuilder fileInfoBuilder = Json.createObjectBuilder();
			fileInfoBuilder.add("recipientName", (String)fileInfo[0]);
			fileInfoBuilder.add("createTime", ((OffsetDateTime)fileInfo[1]).toString());
			fileInfoBuilder.add("name", (String)fileInfo[2]);
			fileOfUserBuilder.add(fileInfoBuilder);
			//System.out.println("I MA HERE");
			//System.out.println((String)fileInfo[0]);
		}
		
		JsonArrayBuilder fileForUserBuilder = Json.createArrayBuilder();
		
		List<Object[]> fileForUserInfos = DatabaseManager.getFilesForUser(id);
		
		for(Object[] fileInfo: fileForUserInfos)
		{
			JsonObjectBuilder fileInfoBuilder = Json.createObjectBuilder();
			fileInfoBuilder.add("creatorName", (String)fileInfo[0]);
			fileInfoBuilder.add("createTime", ((OffsetDateTime) fileInfo[1]).toString());
			fileInfoBuilder.add("name", (String)fileInfo[2]);
			fileForUserBuilder.add(fileInfoBuilder);
			//System.out.println("I MA HERE");
			//System.out.println((String)fileInfo[0]);
		}
		
		responseBuilder.add("otherUsers", userBuilder);
		responseBuilder.add("filesFromUser", fileOfUserBuilder);
		responseBuilder.add("filesForUser", fileForUserBuilder);
		//resp.getWriter().println(userBuilder.build().toString());
		//resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		//out.println("FUCK YOU");
		String r = responseBuilder.build().toString();
		//System.out.println("R: "+ r);
		// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
		out.println(r);
		out.flush();
		return;
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doPost(req, resp);
		
		HttpSession session = req.getSession(false);
		if(session == null)
		{
			resp.sendRedirect("index.jsp");
			return;
		}
		String loginHash = (String) session.getAttribute("loginHash");
		Long id = DatabaseManager.getUserIdFromHash(loginHash);
		
		
		
		String mode = req.getParameter("mode");
		if((mode == null) || (mode.isEmpty()))
		{
			resp.sendRedirect("users.jsp");
		}
		String fileName;
		//Long fileId;
		OffsetDateTime createTime;
		Long creatorId;
		String creatorName;
		Creator c;
		switch(mode)
		{
		case "encrypt":
			Part filePart = req.getPart("fileName");
			fileName = Paths.get(getFileName(filePart)).getFileName().toString();
			String recipient = req.getParameter("recipient");
			if((recipient == null)||(recipient.isEmpty()))
			{
				resp.sendRedirect("users.jsp");
				return;
			}
			Long recipientId = DatabaseManager.getUserIdFromName(recipient);
			PublicKey publicKey = DatabaseManager.getPublicKey(recipientId);
			
			try {
				filePart.write(DIR + File.separator + fileName);
				File inputFile = new File(DIR + File.separator + fileName);
				File outputFile = new File(DIR + File.separator + fileName + ".enc");
				CryptoUtils.encryptAsymetric(publicKey, inputFile, outputFile);
				
				outputFile = new File(DIR + File.separator + fileName + ".enc");
				byte[] outputFileBytes  = CryptoUtils.fileToBytes(outputFile);
				
				OffsetDateTime t=DatabaseManager.addNewUserFile(id, recipientId, fileName,outputFileBytes);
				PrintWriter out = resp.getWriter();
				JsonObjectBuilder b = Json.createObjectBuilder();
				b.add("recipient", recipient);
				b.add("createTime", t.toString());
				b.add("fileName", fileName);
				String r = b.build().toString();
				out.println(r);
				out.flush();
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				if ((new File(DIR + File.separator + fileName).exists()))
						(new File(DIR + File.separator + fileName)).delete();
				if((new File(DIR + File.separator + fileName + ".enc")).exists())
					(new File(DIR + File.separator + fileName + ".enc")).delete();
			}
			
			//resp.sendRedirect("users.jsp");
			break;
		case "decrypt":
			//System.out.println("Decrypting");
			createTime = OffsetDateTime.parse(req.getParameter("createTime"));
			creatorName = req.getParameter("creatorName");
			creatorId = DatabaseManager.getUserIdFromName(creatorName);
			c = new Creator();
			c.createTime=createTime;
			c.creatorId = creatorId;
			//fileId = (Long)req.getAttribute("fileId");
			/*if(fileId == null)
			{
				
			}*/
			boolean check = DatabaseManager.checkIfFileIsForUser(c, id);
			if(!check)
			{
				req.setAttribute("message", "Selected file is not for user");
				req.getRequestDispatcher("/users.jsp").forward(req, resp);
				return;
			}
			byte[] fileBytes = DatabaseManager.getFileContent(c);
			PrivateKey privateKey = DatabaseManager.getPrivateKey(id);
			fileName = DatabaseManager.getFileName(c);
			//System.out.println("Filename is= " + fileName);
			try 
			{
				//FileUtils.writeByteArrayToFile(new File("pathname"), fileBytes);
				File inputFile = new File(DIR + File.separator + fileName + ".dec");
				
				FileOutputStream fos = new FileOutputStream(inputFile);
				fos.write(fileBytes);
				
				File outputFile = new File(DIR + File.separator + fileName);
				CryptoUtils.decryptAsymetric(privateKey, inputFile, outputFile);
				outputFile = new File(DIR + File.separator + fileName);
				
				ServletOutputStream sos = resp.getOutputStream();
				//decrypted = new File(UPLOAD_DIRECTORY + File.separator + decryptFileName);
				resp.setContentLength((int) outputFile.length());
				resp.setHeader("Content-disposition", "attachment; filename=" + fileName);
				FileInputStream in = new FileInputStream(outputFile);
				byte[] buffer = new byte[4096];
				int bytesRead = -1;

				while ((bytesRead = in.read(buffer)) != -1) {
					sos.write(buffer, 0, bytesRead);
				}
				in.close();
				sos.close();
				
			}
			catch(Exception e)
			{
				
			}
			finally 
			{
				if((new File(DIR + File.separator + fileName + ".dec").exists()))
						(new File(DIR + File.separator + fileName + ".dec")).delete();
				if((new File(DIR + File.separator + fileName).exists()))
					(new File(DIR + File.separator + fileName)).delete();
						
			}
			break;
		case "comments":
			//System.out.println("Mode is " + ((String) req.getAttribute("mode")));
			creatorName = req.getParameter("creatorName");
			
			createTime = OffsetDateTime.parse( req.getParameter("createTime"));
			
			creatorId = DatabaseManager.getUserIdFromName(creatorName);
			c = new Creator();
			c.createTime=createTime;
			c.creatorId = creatorId;
			List<Comment> comments = DatabaseManager.getComments(c);
			JsonArrayBuilder respBuilder = Json.createArrayBuilder();
			Map<Long,String> idMap = new HashMap<>();
			idMap.put(creatorId, creatorName);
			for(Comment comment: comments)
			{
				Long uId = comment.getCreatorId();
				JsonObjectBuilder objBuilder = Json.createObjectBuilder();
				objBuilder.add("createTime", comment.getCreateTime().toString());
				objBuilder.add("message", comment.getMessage());
				if(!idMap.containsKey(uId))
					idMap.put(uId, DatabaseManager.getUserNameFromId(uId));
				objBuilder.add("creator",idMap.get(uId) );
				
				respBuilder.add(objBuilder.build());
			}
			String responseJSON = respBuilder.build().toString();
			//System.out.println("Comments are ? " + responseJSON);
			resp.getWriter().write(responseJSON);
			break;
		case "addComment":
			createTime = OffsetDateTime.parse( req.getParameter("createTime"));
			creatorName = req.getParameter("creatorName");
			creatorId = DatabaseManager.getUserIdFromName(creatorName);
			String message =  req.getParameter("message");
			System.out.println("Date is " + createTime.toString()+ "\nName is " + creatorName + "\nMessage is:\n" + message);
			
			c = new Creator();
			c.createTime=createTime;
			c.creatorId = creatorId;
			
			DatabaseManager.addComment(c,id,message);
		}
		
		
	}
	
	
	private String getFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		System.out.println("content-disposition header= " + contentDisp);
		String[] tokens = contentDisp.split(";");
		for (String token : tokens) {
			if (token.trim().startsWith("filename")) {
				return token.substring(token.indexOf("=") + 2, token.length() - 1);
			}
		}
		return "";
	}
	
	
}

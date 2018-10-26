import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;


@WebServlet("/Upload")
@MultipartConfig(fileSizeThreshold=1024*1024*10, 	// 10 MB 
maxFileSize=1024*1024*50,      	// 50 MB
maxRequestSize=1024*1024*100)   	// 100 MB
public class FileUploadHandler extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 205242440643911308L;
	
	private final String UPLOAD_DIRECTORY = "C:\\uploads";
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		System.out.println(UPLOAD_DIRECTORY);
		/*for(Part p: request.getParts())
		{
			System.out.println(p.getName());
		}*/
		//System.out.println(request.getContentType().toString());
		System.out.println("REQUEST IS ="+request.getContentType().toString());
		
		if(ServletFileUpload.isMultipartContent(request))
		{
			//System.out.println("YES");
			
			/*for(Part p : request.getParts())
			{
				System.out.println(p.getName());
				
			}*/
			//
			String key = request.getParameter("key");
			Part filePart = request.getPart("fileName");
			String fileName = Paths.get(getFileName(filePart)).getFileName().toString();
			//String fileName = filePart.getSubmittedFileName();
			System.out.println("Filename is =" + fileName);
			System.out.println("Key is = " + key);
			
			
			//
			filePart.write(UPLOAD_DIRECTORY + File.separator + fileName);
			File temp = new File(UPLOAD_DIRECTORY + File.separator + fileName);
			File encrypted = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc");
			byte[] salt;
			try {
				salt = CryptoUtils.encrypt(key, temp, encrypted);
				File saltFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + "-salt.txt");
				FileOutputStream saltOutput = new FileOutputStream(saltFile);
				saltOutput.write(salt);
				saltOutput.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			temp.delete();
			
		}
		else
		{
			String fileName = request.getParameter("fileName");
			File file = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc");
			String key = request.getParameter("key");
			if(file.exists() && !file.isDirectory())
			{
				response.setContentType("text/plain");
		        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		        File decrypted= new File(UPLOAD_DIRECTORY + File.separator + fileName);
		        File saltedFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + "-salt.txt");
		        FileInputStream saltInput = new FileInputStream(saltedFile);
		        byte[] salt = new byte[CryptoUtils.SALTSIZE];
		        saltInput.read(salt);
		        saltInput.close();
		        try {
					CryptoUtils.decrypt(key, file, decrypted, salt);
					decrypted =  new File(UPLOAD_DIRECTORY + File.separator + fileName);
					System.out.println("Writing repsonse");
					FileInputStream in = new FileInputStream(decrypted);
					byte[] buffer = new byte[4096];
					OutputStream out = response.getOutputStream();
					
					response.setContentLength((int) decrypted.length());
					int bytesRead = -1;
			         
			        while ((bytesRead = in.read(buffer)) != -1) {
			            out.write(buffer, 0, bytesRead);
			        }
					in.close();
					decrypted.delete();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
			}
		}
		
		
		/*if(ServletFileUpload.isMultipartContent(request)){
			try {
				List<FileItem> multipart =  new ServletFileUpload(new DiskFileItemFactory()).parseRequest(new ServletRequestContext(request));
				System.out.println("I AM HERE");
				File temp = null;
				File encrypted = null;
				String  key = null;
				
				for(FileItem item: multipart)
				{
					if(!item.isFormField())
					{
						String name = new File(item.getName()).getName();
						System.out.println("mame is " + name);
						temp = new File(UPLOAD_DIRECTORY + File.separator + name);
						item.write(temp);
						encrypted = new File(UPLOAD_DIRECTORY + File.separator + name + ".enc");
					}
					else
					{
						if(item.getFieldName().equals("fname"))
							key = item.getString();
					}
				}
				CryptoUtils.encrypt(key, temp, encrypted);
				//response.setContentType("text/plain");
				response.setContentType("text/plain");
				OutputStream out = response.getOutputStream();
			    //response.
				
				
				FileInputStream in = new FileInputStream(encrypted);
				byte[] buffer = new byte[4096];
				int length;
				
				while((length = in.read(buffer)) > 0)
				{
					out.write(buffer,0,length);
					System.out.println(buffer);
				}
				in.close();
				out.flush();
				
				request.setAttribute("message", "File Uploaded Successfully");
				
				out.write("ay".getBytes());
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				request.setAttribute("message", "File enc failed due to " + e);
			}
					//
		} else
		{
			System.out.println("IS NOT MULTIPART");
			request.setAttribute("message", "Sorry");
		}*/
	
	}
	
	private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }
}

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

public class FileUploadHandler extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String UPLOAD_DIRECTORY = "C:/uploads";
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		if(ServletFileUpload.isMultipartContent(request)){
			try {
				List<FileItem> multipart =  new ServletFileUpload(new DiskFileItemFactory()).parseRequest(new ServletRequestContext(request));
			
				File temp = null;
				File encrypted = null;
				String  key = null;
				
				for(FileItem item: multipart)
				{
					if(!item.isFormField())
					{
						String name = new File(item.getName()).getName();
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
			request.setAttribute("message", "Sorry");
		}
	
	}
	
}

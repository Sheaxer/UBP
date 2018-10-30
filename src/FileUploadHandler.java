import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.SecretKey;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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

		System.out.println("REQUEST IS ="+request.getContentType().toString());
		
		if(ServletFileUpload.isMultipartContent(request))
		{
			String mode = request.getParameter("mode");
			Part filePart = request.getPart("fileName");
			String fileName = Paths.get(getFileName(filePart)).getFileName().toString();
			//String fileName = filePart.getSubmittedFileName();
			System.out.println("Filename is =" + fileName);
			System.out.println("Mode is = " + mode);
			filePart.write(UPLOAD_DIRECTORY + File.separator + fileName);
			File temp = new File(UPLOAD_DIRECTORY + File.separator + fileName);
			SecretKey secretKey;
			String cipher = request.getParameter("cipher");
			//
			switch(mode) {
				case "encrypt":
					//filePart.write(UPLOAD_DIRECTORY + File.separator + fileName);
					
					File encrypted = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc");
					switch(cipher)
					{
						case "symetric":
							try {
								secretKey = CryptoUtils.encrypt(temp, encrypted,false);
								File secretKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".key");
							
								CryptoUtils.writeKeytoFile(secretKey, secretKeyFile);
								String[] fileNames = {fileName + ".enc",fileName + ".key"};
								byte[] zip = zipFiles(fileNames);
								ServletOutputStream sos = response.getOutputStream();
								response.setContentType("application/zip");
								response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "-enc.zip");
								response.setContentLength(zip.length);
								sos.write(zip);
								sos.flush();
		                //temp = new File(UPLOAD_DIRECTORY + File.separator + fileName);
								new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc").delete();
						//temp.delete();
						//encrypted.delete();
								new File(UPLOAD_DIRECTORY + File.separator + fileName + ".key").delete();
						
								sos.close();
							}
							catch (Exception e) {
								// TODO Auto-generated catch block
											e.printStackTrace();
				        
							} 
							break;
						case "asymetric":
							
						try {
							KeyPair keyPair= CryptoUtils.encryptAsymetric(temp, encrypted);
							File privateKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".prkey");
							File publicKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".pubkey");
							CryptoUtils.writeKeyPairToFile(keyPair, publicKeyFile ,privateKeyFile);
							privateKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".prkey");
							publicKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".pubkey");
							
							String[] fileNames = {fileName + ".enc",fileName + ".prkey"};
							byte[] zip = zipFiles(fileNames);
							ServletOutputStream sos = response.getOutputStream();
							response.setContentType("application/zip");
							response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "-enc.zip");
							response.setContentLength(zip.length);
							sos.write(zip);
							sos.flush();
							sos.close();
							new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc").delete();
							new File(UPLOAD_DIRECTORY + File.separator + fileName + ".prkey").delete();
							new File(UPLOAD_DIRECTORY + File.separator + fileName + ".pubkey").delete();
							
							
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
							
							
							
							
					}
					//temp.delete();
					break;
				case "decrypt":
					Part KeyPart = request.getPart("key");
					switch(cipher)
					{
					case "symetric":
						try {
							
							String secretKeyFileName = Paths.get(getFileName(KeyPart)).getFileName().toString();
							KeyPart.write(UPLOAD_DIRECTORY + File.separator+ secretKeyFileName );
							secretKey = CryptoUtils.readSymetricKeyFromFile(new File(UPLOAD_DIRECTORY + File.separator + secretKeyFileName));

							new File(UPLOAD_DIRECTORY + secretKeyFileName).delete();
							String decryptFileName = fileName.substring(0,fileName.lastIndexOf('.'));
							File decrypted = new File(UPLOAD_DIRECTORY + File.separator+ decryptFileName); // dangerous should fix
							CryptoUtils.decrypt(secretKey, temp, decrypted);							
							response.setContentType("text/plain");
							response.setHeader("Content-disposition", "attachment; filename=" + decryptFileName);
							ServletOutputStream sos = response.getOutputStream();
							decrypted = new File(UPLOAD_DIRECTORY + File.separator + decryptFileName);
							response.setContentLength((int)decrypted.length() );
							FileInputStream in = new FileInputStream(decrypted);
							byte[] buffer = new byte[4096];
							int bytesRead = -1;
			         
							while ((bytesRead = in.read(buffer)) != -1) {
								sos.write(buffer, 0, bytesRead);
							}
							in.close();
							sos.close();
							decrypted.delete();
							new File(UPLOAD_DIRECTORY + File.separator+ secretKeyFileName).delete();
					
					//secretKey.destroy();
						} catch (Exception e)
							{
								e.printStackTrace();
							}
						break;
					case "asymetric":
						try {
							String privateKeyFileName = Paths.get(getFileName(KeyPart)).getFileName().toString();
							KeyPart.write(UPLOAD_DIRECTORY + File.separator+ privateKeyFileName );
							PrivateKey privateKey = CryptoUtils.readPrivateKeyFile(new File(UPLOAD_DIRECTORY + File.separator + privateKeyFileName));

							new File(UPLOAD_DIRECTORY + privateKeyFileName).delete();
							String decryptFileName = fileName.substring(0,fileName.lastIndexOf('.'));
							File decrypted = new File(UPLOAD_DIRECTORY + File.separator+ decryptFileName); // dangerous should fix
							CryptoUtils.decryptAsymetric(privateKey, temp, decrypted);							
							response.setContentType("text/plain");
							response.setHeader("Content-disposition", "attachment; filename=" + decryptFileName);
							ServletOutputStream sos = response.getOutputStream();
							decrypted = new File(UPLOAD_DIRECTORY + File.separator + decryptFileName);
							response.setContentLength((int)decrypted.length() );
							FileInputStream in = new FileInputStream(decrypted);
							byte[] buffer = new byte[4096];
							int bytesRead = -1;
		         
							while ((bytesRead = in.read(buffer)) != -1) {
								sos.write(buffer, 0, bytesRead);
							}
							in.close();
							sos.close();
							decrypted.delete();
							new File(UPLOAD_DIRECTORY + File.separator+ privateKeyFileName).delete();
				
						} 
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					new File(UPLOAD_DIRECTORY + File.separator + fileName).delete();
			}
			
			
		}
		else
		{
			
		}
		
		
		
	
	}
	
	private byte[] zipFiles(String[] files) throws IOException 
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];

        for (String fileName : files) {
            FileInputStream fis = new FileInputStream(UPLOAD_DIRECTORY + File.separator + fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);

            zos.putNextEntry(new ZipEntry(fileName));

            int bytesRead;
            while ((bytesRead = bis.read(bytes)) != -1) {
                zos.write(bytes, 0, bytesRead);
            }
            zos.closeEntry();
            bis.close();
            fis.close();
        }
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();

        return baos.toByteArray();
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

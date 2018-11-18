import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
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
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

@WebServlet("/Upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10 // 500 MB
// 2 GB
) // 3 GB
public class FileUploadHandler extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 205242440643911308L;

	private final String UPLOAD_DIRECTORY = "C:\\uploads";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// check if the user is logged in
		HttpSession session = request.getSession(false); // get session
		if(session == null) {
			// user is not logged in
			response.sendRedirect("index.jsp");
			return;
		}
		String loginHash = (String) session.getAttribute("loginHash");
		Long id = DatabaseManager.getUserIdFromHash(loginHash);
		// process file upload
		System.out.println(UPLOAD_DIRECTORY);

		System.out.println("REQUEST IS =" + request.getContentType().toString());
		String mode = request.getParameter("mode");
		if (ServletFileUpload.isMultipartContent(request)) {
			
			Part filePart = request.getPart("fileName");
			String fileName = Paths.get(getFileName(filePart)).getFileName().toString();
			if (fileName.isEmpty()) {
				System.out.println("Nonexistent file -- aborting");
				String message = "No file set";
				request.setAttribute("message", message);
				if (mode.equals("encrypt"))
					request.getRequestDispatcher("/encrypt.jsp").forward(request, response);
				else
					request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
				return;
			}

			// String fileName = filePart.getSubmittedFileName();
			System.out.println("Filename is =" + fileName);
			System.out.println("Mode is = " + mode);
			filePart.write(UPLOAD_DIRECTORY + File.separator + fileName);
			File temp = new File(UPLOAD_DIRECTORY + File.separator + fileName);
			SecretKey secretKey;
			String cipher = request.getParameter("cipher");
			switch (mode) {
			case "encrypt":
				// filePart.write(UPLOAD_DIRECTORY + File.separator + fileName);

				File encrypted = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc");
				switch (cipher) {
				case "symetric":
					try {
						long startTime = System.currentTimeMillis();
						secretKey = CryptoUtils.encrypt(temp, encrypted, false);
						long stopTime = System.currentTimeMillis();
						long elapsedTime = stopTime - startTime;
						System.out.println("Time is " + elapsedTime);
						
						File secretKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".key");

						CryptoUtils.writeKeytoFile(secretKey, secretKeyFile);
						String[] fileNames = { fileName + ".enc", fileName + ".key" };
						byte[] zip = zipFiles(fileNames);
						
						ServletOutputStream sos = response.getOutputStream();
						response.setContentType("application/zip");
						response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "-enc.zip");
						response.setContentLength(zip.length);
						sos.write(zip);
						sos.flush();

						sos.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					}
					finally
					{
						new File(UPLOAD_DIRECTORY + File.separator + fileName).delete();
						if(new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc").exists())
							new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc").delete();
						if(new File(UPLOAD_DIRECTORY + File.separator + fileName + ".key").exists())
							new File(UPLOAD_DIRECTORY + File.separator + fileName + ".key").delete();
					}
					break;
				case "asymetric":
					// String keyFileName =
					//Part keyPart = request.getPart("key");
					String keyName = null;

					try {
						//String[] fileNames = null;
						//keyName = Paths.get(getFileName(keyPart)).getFileName().toString();
						//if (!keyName.isEmpty()) {

							//keyPart.write(UPLOAD_DIRECTORY + File.separator + keyName);
							//File publicKeyFile = new File(UPLOAD_DIRECTORY + File.separator + keyName);
							//PublicKey publicKey = CryptoUtils.readPublicKey(publicKeyFile);
							PublicKey publicKey =  DatabaseManager.getPublicKey(id);
							// error = CryptoUtils.getError();
							if (publicKey == null) {
								System.out.println("Invalid public key format");
								String message = "Invalid public key format";
								request.setAttribute("message", message);
								request.getRequestDispatcher("/encrypt.jsp").forward(request, response);
								return;
							} else {
								long startTime = System.currentTimeMillis();
								CryptoUtils.encryptAsymetric(publicKey, temp, encrypted);
								long stopTime = System.currentTimeMillis();
								long elapsedTime = stopTime - startTime;
								System.out.println("Time is " + elapsedTime);
								encrypted = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc");
								response.setContentType("text/plain");
								response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".enc");
								ServletOutputStream sos = response.getOutputStream();
								//decrypted = new File(UPLOAD_DIRECTORY + File.separator + decryptFileName);
								response.setContentLength((int) encrypted.length());
								FileInputStream in = new FileInputStream(encrypted);
								byte[] buffer = new byte[4096];
								int bytesRead = -1;

								while ((bytesRead = in.read(buffer)) != -1) {
									sos.write(buffer, 0, bytesRead);
								}
								in.close();
								sos.close();
								//fileNames = new String[] { fileName + ".enc" };
							}
						/*} else {
							long startTime = System.currentTimeMillis();
							KeyPair keyPair = CryptoUtils.encryptAsymetric(temp, encrypted);
							long stopTime = System.currentTimeMillis();
							long elapsedTime = stopTime - startTime;
							System.out.println("Time is " + elapsedTime);
							
							File privateKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".prkey");
							File publicKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".pubkey");
							CryptoUtils.writeKeyPairToFile(keyPair, publicKeyFile, privateKeyFile);

							privateKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".prkey");
							publicKeyFile = new File(UPLOAD_DIRECTORY + File.separator + fileName + ".pubkey");
							fileNames = new String[] { fileName + ".enc", fileName + ".prkey", fileName + ".pubkey" };
						}*/

						/*if (fileNames != null) {
							byte[] zip = zipFiles(fileNames);
							ServletOutputStream sos = response.getOutputStream();
							response.setContentType("application/zip");
							response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "-enc.zip");
							response.setContentLength(zip.length);
							sos.write(zip);
							sos.flush();
							sos.close();
						}*/
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						new File(UPLOAD_DIRECTORY + File.separator + fileName).delete();
						if(new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc").exists())
							new File(UPLOAD_DIRECTORY + File.separator + fileName + ".enc").delete();
						if (new File(UPLOAD_DIRECTORY + File.separator + keyName).exists()) 
							new File(UPLOAD_DIRECTORY + File.separator + keyName).delete();
						if(new File(UPLOAD_DIRECTORY + File.separator + fileName + ".prkey").exists())
							new File(UPLOAD_DIRECTORY + File.separator + fileName + ".prkey").delete();
						if(new File(UPLOAD_DIRECTORY + File.separator + fileName + ".pubkey").exists())
							new File(UPLOAD_DIRECTORY + File.separator + fileName + ".pubkey").delete();
						

					}

				}
				// temp.delete();
				break;
			case "decrypt":
				Part keyPart = request.getPart("key");
				String secretKeyFileName = "";
				String decryptFileName= "";
				
				switch (cipher) {
				case "symetric":
					try {

						secretKeyFileName = Paths.get(getFileName(keyPart)).getFileName().toString();
						if (secretKeyFileName.isEmpty()) {
							System.out.println("No key set");
							String message = "No key set";
							request.setAttribute("message", message);
							request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
							return;
						}
						keyPart.write(UPLOAD_DIRECTORY + File.separator + secretKeyFileName);
						secretKey = CryptoUtils.readSymetricKeyFromFile(
								new File(UPLOAD_DIRECTORY + File.separator + secretKeyFileName));
						if (secretKey == null) {
							
							System.out.println("No private key is set");
							String message = "No private key is set";
							request.setAttribute("message", message);
							request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
						}
						
						decryptFileName = fileName.substring(0, fileName.lastIndexOf('.'));
						File decrypted = new File(UPLOAD_DIRECTORY + File.separator + decryptFileName); // dangerous
																										// should fix

						long startTime = System.currentTimeMillis();

						CryptoUtils.decrypt(secretKey, temp, decrypted);

						long stopTime = System.currentTimeMillis();
						long elapsedTime = stopTime - startTime;
						System.out.println("Time is " + elapsedTime);
						
						response.setContentType("text/plain");
						response.setHeader("Content-disposition", "attachment; filename=" + decryptFileName);
						ServletOutputStream sos = response.getOutputStream();
						decrypted = new File(UPLOAD_DIRECTORY + File.separator + decryptFileName);
						response.setContentLength((int) decrypted.length());
						FileInputStream in = new FileInputStream(decrypted);
						byte[] buffer = new byte[4096];
						int bytesRead = -1;

						while ((bytesRead = in.read(buffer)) != -1) {
							sos.write(buffer, 0, bytesRead);
						}
						in.close();
						sos.close();
						
						

						// secretKey.destroy();
					} catch (Exception e) {
						String message = "File is not decrypted by selected key";
						request.setAttribute("message", message);
						request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
						e.printStackTrace();
					} finally {
						new File(UPLOAD_DIRECTORY + File.separator + fileName).delete();
						if((!secretKeyFileName.isEmpty())&&(new File(UPLOAD_DIRECTORY + File.separator + secretKeyFileName).exists()))
							new File(UPLOAD_DIRECTORY + File.separator + secretKeyFileName).delete();
						if((!decryptFileName.isEmpty()) &&(new File(UPLOAD_DIRECTORY + File.separator + decryptFileName).exists()))
							new File(UPLOAD_DIRECTORY + File.separator + decryptFileName).delete();
					}
					break;
				case "asymetric":
					//String privateKeyFileName="";
					try {
						//privateKeyFileName = Paths.get(getFileName(keyPart)).getFileName().toString();
						/*if (privateKeyFileName.isEmpty()) {
							
							System.out.println("No private key is set");
							String message = "No private key is set";
							request.setAttribute("message", message);
							request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
							return;
						}*/
						//keyPart.write(UPLOAD_DIRECTORY + File.separator + privateKeyFileName);
						/*PrivateKey privateKey = CryptoUtils
								.readPrivateKey(new File(UPLOAD_DIRECTORY + File.separator + privateKeyFileName));*/
						PrivateKey privateKey = DatabaseManager.getPrivateKey(id);
						if (privateKey == null) {
							
							String message = "Invalid private key format";
							request.setAttribute("message", message);
							request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
							return;
						}

						
						decryptFileName = fileName.substring(0, fileName.lastIndexOf('.'));
						File decrypted = new File(UPLOAD_DIRECTORY + File.separator + decryptFileName); // dangerous
																										// should fix

						long startTime = System.currentTimeMillis();

						CryptoUtils.decryptAsymetric(privateKey, temp, decrypted);

						long stopTime = System.currentTimeMillis();
						long elapsedTime = stopTime - startTime;
						System.out.println("Time is " + elapsedTime);
						
						response.setContentType("text/plain");
						response.setHeader("Content-disposition", "attachment; filename=" + decryptFileName);
						ServletOutputStream sos = response.getOutputStream();
						decrypted = new File(UPLOAD_DIRECTORY + File.separator + decryptFileName);
						response.setContentLength((int) decrypted.length());
						FileInputStream in = new FileInputStream(decrypted);
						byte[] buffer = new byte[4096];
						int bytesRead = -1;

						while ((bytesRead = in.read(buffer)) != -1) {
							sos.write(buffer, 0, bytesRead);
						}
						in.close();
						sos.close();
						
						

					} catch (Exception e) {
						
						String message = "File not encrypted by sent key";
						request.setAttribute("message", message);
						request.getRequestDispatcher("/decrypt.jsp").forward(request, response);
						System.out.println("File not encrypted by sent key");
						e.printStackTrace();
					}
					finally
					{
						new File(UPLOAD_DIRECTORY + File.separator + fileName).delete();	
						
						/*if((!privateKeyFileName.isEmpty())&&(new File(UPLOAD_DIRECTORY + File.separator + privateKeyFileName).exists()))
							new File(UPLOAD_DIRECTORY + File.separator + privateKeyFileName).delete();*/
						
						if((!decryptFileName.isEmpty())&&(new File(UPLOAD_DIRECTORY + File.separator + decryptFileName).exists()))
							new File(UPLOAD_DIRECTORY + File.separator + decryptFileName).delete();
					}
				}
				
			
				
				
			}

		} else 
		{
			System.out.println("I GOT TO DOWNLOAD");
		
			PublicKey publicKey =  DatabaseManager.getPublicKey(id);
			PrivateKey privateKey = DatabaseManager.getPrivateKey(id);
			String name = (String) session.getAttribute("username");
			File publicKeyFile = new File(UPLOAD_DIRECTORY + File.separator + name + ".pubkey");
			File privateKeyFile = new File(UPLOAD_DIRECTORY + File.separator + name + ".prkey");
			KeyPair keyPair = new KeyPair(publicKey,privateKey);
			try {
				CryptoUtils.writeKeyPairToFile(keyPair, publicKeyFile, privateKeyFile);
				String[] fileNames = {name + ".pubkey", name + ".prkey"};
				byte[] zip = zipFiles(fileNames);
				ServletOutputStream sos = response.getOutputStream();
				response.setContentType("application/zip");
				response.setHeader("Content-Disposition", "attachment; filename=" + name + "-keys.zip");
				response.setContentLength(zip.length);
				sos.write(zip);
				sos.flush();
				sos.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				if(new File(UPLOAD_DIRECTORY + File.separator + name + ".pubkey").exists())
					new File(UPLOAD_DIRECTORY + File.separator + name + ".pubkey").delete();
				if(new File(UPLOAD_DIRECTORY + File.separator + name + ".prkey").exists())
					new File(UPLOAD_DIRECTORY + File.separator + name + ".prkey").delete();
			}
		}
			

	}

	private byte[] zipFiles(String[] files) throws IOException {
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

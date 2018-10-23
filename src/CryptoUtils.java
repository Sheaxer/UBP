import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.NoSuchPaddingException;

public class CryptoUtils {

	
	private static final String ALGORITHM = "AES" ;
	private static final String TRANSFORMATION = "AES";
	
	public static void encrypt(String key, File inputFile, File outputFile)
	throws Exception
	{
		doCrypto ( Cipher.ENCRYPT_MODE, key , inputFile , outputFile ) ;
	}
	
	public static void decrypt(String key, File inputFile, File outputFile) throws Exception
	{
		doCrypto(Cipher.DECRYPT_MODE,key,inputFile,outputFile);
	}
	
	private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws Exception {
		// TODO Auto-generated method stub
		try {
			Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cipherMode, secretKey);
			FileInputStream in = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			in.read(inputBytes);
			
			byte[] outputBytes = cipher.doFinal(inputBytes);
			
			FileOutputStream out = new FileOutputStream(outputFile);
			out.write(outputBytes);
			
			
			in.close();
			out.close();
			
		} catch(NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex)
		{
			throw new Exception ("Error encrypting/decrypting file "+ex.getMessage());
		}
	}
}

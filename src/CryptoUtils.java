import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

public class CryptoUtils {

	
	private static final String ALGORITHM = "AES" ;
	//private static final String TRANSFORMATION = "PBKDF2WithHmacSHA1";
	//protected static final int SALTSIZE = 16;
	/*public static byte[] encrypt(String key, File inputFile, File outputFile)
	throws Exception
	{
		byte[] salt;
		salt=doCrypto ( Cipher.ENCRYPT_MODE, key , inputFile , outputFile );
		System.out.println("SALT IS= "+salt.toString());
		return salt;
	}*/
	
	public static void decrypt(SecretKey secretKey, File inputFile, File outputFile) throws Exception
	{
		//SecretKey secretKey = generateKey();
		doCrypto(Cipher.DECRYPT_MODE,secretKey,inputFile,outputFile,false);
	}
	
	public static SecretKey encrypt(File inputFile, File outputFile, boolean append) throws Exception
	{
		SecretKey secretKey = generateKey();
		doCrypto(Cipher.ENCRYPT_MODE,secretKey,inputFile,outputFile,append);
		return secretKey;
	}
	
	/*public static void decrypt(String key, File inputFile, File outputFile, byte[] salt) throws Exception
	{
		SecretKey secretKey = generateKey(key, salt);
		return;
	}*/
	
	private static SecretKey generateKey() throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		/*byte[] salt = getSalt();
		SecretKeyFactory factory = SecretKeyFactory.getInstance(TRANSFORMATION);
		KeySpec spec = new PBEKeySpec(null,salt,65536,256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);*/
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);
		SecretKey secretKey = new SecretKeySpec(key,ALGORITHM);
		return secretKey;
	}
	
	public  static void doCrypto(int mode,SecretKey secretKey, File inputFile, File outputFile, boolean append) throws Exception {
		// TODO Auto-generated method stub
		try {
			
			/*if(salt == null)
				salt=getSalt();
			SecretKey secretKey= generateKey(key,salt);
			//Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);*/
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(mode, secretKey);
			FileInputStream in = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			in.read(inputBytes);
			
			byte[] outputBytes = cipher.doFinal(inputBytes);
			
			FileOutputStream out = new FileOutputStream(outputFile,append);
			out.write(outputBytes);
			
			
			in.close();
			out.close();
			//return salt;
			
		} catch(NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex)
		{
			throw new Exception ("Error encrypting/decrypting file "+ex.getMessage());
		}
	}
	
	
	/*private static byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		//sr.setSeed(System.currentTimeMillis());
        byte[] salt = new byte[SALTSIZE];
        sr.nextBytes(salt);
        return salt;
	}*/
}

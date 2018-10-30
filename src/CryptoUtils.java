import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;



import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import java.security.interfaces.RSAPrivateKey;

public class CryptoUtils {

	
	private static final String ALGORITHM = "AES" ;
	private static final String ASYMETRIC_ALGORITHM = "RSA";
	private static final int KEY_SIZE = 16;
	private static final int ASYMETRIC_KEY_SIZE = 2048;
	
	public static void encryptAsymetric(PublicKey publicKey, File inputFile, File outputFile) throws Exception
	{
		SecretKey secretKey = generateKey();
		Cipher cipherAsymetric  = Cipher.getInstance(ASYMETRIC_ALGORITHM);
		cipherAsymetric.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedSecretKey = cipherAsymetric.doFinal(secretKey.getEncoded());
		FileOutputStream output = new FileOutputStream(outputFile);
		output.write(encryptedSecretKey);
		output.close();
		encrypt(secretKey, inputFile, outputFile, true);
	}
	
	
	
	public static KeyPair encryptAsymetric(File inputFile, File outputFile) throws Exception
	{
		KeyPair keyPair = generateKeyPair();
		encryptAsymetric(keyPair.getPublic(), inputFile, outputFile);
		return keyPair;
	}
	
	public static void decryptAsymetric(PrivateKey privateKey, File inputFile, File outputFile) throws Exception
	{
		int rsaBlockSize = ((RSAPrivateKey) privateKey).getModulus().bitLength()/8;
		byte[] rsaBlock = new byte[rsaBlockSize];
		FileInputStream in = new FileInputStream(inputFile);
		byte[] fileBytes = new byte[(int) inputFile.length()];
		in.read(fileBytes);
		System.arraycopy(fileBytes,0, rsaBlock, 0, rsaBlockSize);
		
		Cipher asymetricCipher = Cipher.getInstance(ASYMETRIC_ALGORITHM);
		asymetricCipher.init(Cipher.DECRYPT_MODE, privateKey);
		
		byte[] secretKeyBytes = asymetricCipher.doFinal(rsaBlock);;
		
		
		SecretKey secretKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, ALGORITHM);
		
		byte[] inputBytes = Arrays.copyOfRange(fileBytes, rsaBlockSize, fileBytes.length);
		
		doCrypto(Cipher.DECRYPT_MODE, secretKey, inputBytes, outputFile, false);
		
	}
	
	
	
	
	public static void decrypt(SecretKey secretKey, File inputFile, File outputFile) throws Exception
	{
		//SecretKey secretKey = generateKey();
		doCrypto(Cipher.DECRYPT_MODE,secretKey,inputFile,outputFile,false);
	}
	
	public static SecretKey encrypt(File inputFile, File outputFile, boolean append) throws Exception
	{
		SecretKey secretKey = generateKey();
		encrypt(secretKey, inputFile, outputFile,append);
		return secretKey;
	}
	
	
	
	private static byte[]  fileToBytes(File inputFile) throws Exception
	{
		try {
			FileInputStream in = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			in.read(inputBytes);
			in.close();
			return inputBytes;
		}
		catch(IOException e) {
			throw new Exception ("Error processing input file " + e.getMessage(), e);
		}
	}
	
	public static void encrypt(SecretKey secretKey, File inputFile, File outputFile, boolean append) throws Exception
	{
		//SecretKey secretKey = generateKey();
		doCrypto(Cipher.ENCRYPT_MODE,secretKey,inputFile,outputFile,append);
		//return secretKey;
	}
	
	
	private static SecretKey generateKey() throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[KEY_SIZE];
		secureRandom.nextBytes(key);
		SecretKey secretKey = new SecretKeySpec(key,ALGORITHM);
		return secretKey;
	}
	
	public static void doCrypto(int mode,SecretKey secretKey, File inputFile, File outputFile, boolean append) throws Exception
	{
		byte[] inputBytes = fileToBytes(inputFile);
		doCrypto(mode, secretKey, inputBytes, outputFile, append);
	}
	
	public  static void doCrypto(int mode,SecretKey secretKey, byte[] inputBytes, File outputFile, boolean append) throws Exception {
		// TODO Auto-generated method stub
		try {
			
		
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(mode, secretKey);
			
			byte[] outputBytes = cipher.doFinal(inputBytes);
			
			FileOutputStream out = new FileOutputStream(outputFile,append);
			out.write(outputBytes);
			
			
			//in.close();
			out.close();
			//return salt;
			
		} catch(NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex)
		{
			throw new Exception ("Error encrypting/decrypting file "+ex.getMessage());
		}
	}
	
	private static KeyPair generateKeyPair() throws Exception
	{
		KeyPairGenerator gen = KeyPairGenerator.getInstance(ASYMETRIC_ALGORITHM);
		gen.initialize(ASYMETRIC_KEY_SIZE);
		return gen.genKeyPair();
	}
	
	
	
	public static void writeKeytoFile(SecretKey key, File output) throws Exception
	{
		FileOutputStream secretKeyFileOutput = new FileOutputStream(output);
		ObjectOutputStream secretKeyOutput = new ObjectOutputStream(secretKeyFileOutput);
		secretKeyOutput.writeObject(key);
		secretKeyOutput.close();
		secretKeyFileOutput.close();
	}
	
	public static SecretKey readSymetricKeyFromFile(File inputFile) throws Exception
	{
		FileInputStream secretKeyFileInput = new FileInputStream(inputFile); 
		ObjectInputStream secretKeyObjectInput = new ObjectInputStream(secretKeyFileInput);
		SecretKey secretKey = (SecretKey)secretKeyObjectInput.readObject();
		secretKeyObjectInput.close();
		secretKeyFileInput.close();
		return secretKey;
	}
	
	/*public static PrivateKey readPrivateKey(File inputFile) throws Exception
	{
		FileInputStream secretKeyFileInput = new FileInputStream(inputFile); 
		ObjectInputStream secretKeyObjectInput = new ObjectInputStream(secretKeyFileInput);
		PrivateKey privateKey = (PrivateKey)secretKeyObjectInput.readObject();
		secretKeyObjectInput.close();
		secretKeyFileInput.close();
		return privateKey;
	}*/
	
	public static PublicKey readPublicKey(File inputFile) throws Exception
	{
		byte[] inputBytes = fileToBytes(inputFile);
		X509EncodedKeySpec ks = new X509EncodedKeySpec(inputBytes);
		KeyFactory kf = KeyFactory.getInstance(ASYMETRIC_ALGORITHM);
		PublicKey pub = kf.generatePublic(ks);
		return pub;
	}
	
	public static PrivateKey readPrivateKey (File inputFile) throws Exception
	{
		byte[] inputBytes = fileToBytes(inputFile);
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(inputBytes);
		KeyFactory kf = KeyFactory.getInstance(ASYMETRIC_ALGORITHM);
		PrivateKey pvt = kf.generatePrivate(ks);
		return pvt;
	}
	
	
	public static void writeKeyPairToFile(KeyPair keyPair, File publicKey, File privateKey) throws Exception
	{
		//String outFile = ...;
		FileOutputStream out = new FileOutputStream(publicKey);
		out.write(keyPair.getPublic().getEncoded());
		out.close();

		out = new FileOutputStream(privateKey);
		out.write(keyPair.getPrivate().getEncoded());
		out.close();
	}
	
	public static void writeKeyPairtoFilePEM(KeyPair keyPair, File publicKey, File privateKey) throws Exception
	{
		Base64.Encoder encoder = Base64.getEncoder();
		Writer writer = new FileWriter(privateKey);
		writer.write("-----BEGIN RSA PRIVATE KEY-----\n");
		writer.write(encoder.encodeToString(keyPair.getPrivate().getEncoded()));
		writer.close();
		
		writer = new FileWriter(publicKey);
		writer.write("-----BEGIN RSA PUBLIC KEY-----\n");
		writer.write(encoder.encodeToString(keyPair.getPublic().getEncoded()));
		writer.write("\n-----END RSA PUBLIC KEY-----\n");
		writer.close();
	}
	
	
	
}


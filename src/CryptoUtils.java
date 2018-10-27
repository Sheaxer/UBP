import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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
	private static final String TRANSFORMATION = "PBKDF2WithHmacSHA1";
	protected static final int SALTSIZE = 16;

	private static final String ASYMETRIC_ALGORITHM = "RSA";

	/*public static byte[] encrypt(String key, File inputFile, File outputFile)
	throws Exception
	{
		byte[] salt;
		salt=doCrypto ( Cipher.ENCRYPT_MODE, key , inputFile , outputFile );
		System.out.println("SALT IS= "+salt.toString());
		return salt;
	}*/
	
	public static void decrypt(String key, File inputFile, File outputFile, byte[] salt) throws Exception
	{
		doCrypto(Cipher.DECRYPT_MODE,key,inputFile,outputFile,salt);
	}
	
	public static byte[] encrypt(String key, File inputFile, File outputFile) throws Exception
	{
		return doCrypto(Cipher.ENCRYPT_MODE,key,inputFile,outputFile,null);
	}

	public static void encryptAsymetric(PublicKey publicKey, File inputFile, File outputFile) throws Exception {
		try {
			// vygenerujeme nahodny kluc a salt
			// metoda getSalt generuje nahodne byty pomocou PRNG
			// mozeme ju zavolat dva krat, alebo raz a vyziadat si dvojnasobny pocet bytov (treba trochu upravit kod)
			// v jednom pripade dva krat inicializujeme PRNG a v druhom musime rozdelovat array
			// salt aj key budeme nakoniec sifrovat pomocou RSA, takze spojit musime tak ci tak
			// TL;DR: jedno riesenie je pravdepodobne zanedbatelne rychlejsie, takze to spravime bez uprav kodu pre getSalt
			byte[] salt = getSalt();
			byte[] randomKey = getSalt();

			byte[] keyAndSalt = new byte[2*SALTSIZE];
			System.arraycopy(randomKey, 0, keyAndSalt, 0, randomKey.length);
			System.arraycopy(salt, 0, keyAndSalt, SALTSIZE, salt.length);

			// len pre istotu overime, ci dlzka kluca + saltu je <= dlzka public kluca
			if(publicKey instanceof RSAPublicKey) {
				assert SALTSIZE*8*2 <= ((RSAPublicKey) publicKey).getModulus().bitLength();
			}

			// zasifrujeme kluc + salt pomocou RSA
			Cipher cipherAsymetric = Cipher.getInstance(ASYMETRIC_ALGORITHM);
			cipherAsymetric.init(Cipher.ENCRYPT_MODE, publicKey);

			byte[] encryptedBytes = cipherAsymetric.doFinal(keyAndSalt);

			FileOutputStream out = new FileOutputStream(outputFile);
			out.write(encryptedBytes);

			out.close();

			// zasifrujem vstupny subor pomocou AES a pridam ho za zasifrovany kluc + salt
			doCrypto(Cipher.ENCRYPT_MODE, new String(randomKey), inputFile, outputFile, salt, true);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex) {
			throw new Exception ("Error encrypting/decrypting file " + ex.getMessage());
		}
		catch (Exception innerEx) {
			throw new Exception ("Inner exception: " + innerEx.getMessage());
		}
	}
	
	/*public static void decrypt(String key, File inputFile, File outputFile, byte[] salt) throws Exception
	{
		SecretKey secretKey = generateKey(key, salt);
		return;
	}*/
	
	private static SecretKey generateKey(String password,byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		SecretKeyFactory factory = SecretKeyFactory.getInstance(TRANSFORMATION);
		KeySpec spec = new PBEKeySpec(password.toCharArray(),salt,65536,256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
		return secretKey;
	}

	public static byte[] doCrypto(int mode,String key, File inputFile, File outputFile, byte[] salt) throws Exception {
		// pretazena funkcie pre kompatibilitu s existujucim kodom
		return doCrypto(mode, key, inputFile, outputFile, salt, false);
	}

	public  static byte[] doCrypto(int mode,String key, File inputFile, File outputFile, byte[] salt, boolean appendOutput) throws Exception {
		try {
			
			if(salt == null)
				salt=getSalt();
			SecretKey secretKey = generateKey(key,salt);
			//Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(mode, secretKey);
			FileInputStream in = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			in.read(inputBytes);
			
			byte[] outputBytes = cipher.doFinal(inputBytes);
			
			FileOutputStream out = new FileOutputStream(outputFile, appendOutput);
			out.write(outputBytes);
			
			
			in.close();
			out.close();
			return salt;
			
		} catch(NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException ex)
		{
			throw new Exception ("Error encrypting/decrypting file "+ex.getMessage());
		}
	}
	
	
	private static byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		//sr.setSeed(System.currentTimeMillis());
        byte[] salt = new byte[SALTSIZE];
        sr.nextBytes(salt);
        return salt;
	}

	// ziskanie public/ private klucov podla https://stackoverflow.com/questions/11410770/load-rsa-public-key-from-file
	public static PrivateKey getPrivateKeyFromDER(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(ASYMETRIC_ALGORITHM);
		return kf.generatePrivate(spec);
	}

	public static PublicKey getPublicKeyFromDER(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(ASYMETRIC_ALGORITHM);
		return kf.generatePublic(spec);
	}
}

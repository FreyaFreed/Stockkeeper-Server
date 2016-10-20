package stockkeeper.encryption;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptionUtils {
	
	public static String xform = "RSA/ECB/PKCS1Padding";
	
	public static void main(String[] args) {
		try
		{
			//String xform = "RSA/ECB/PKCS1Padding";
			KeyPair pair = generateKeypair();
			writeToFile(pair.getPrivate(), "private.key", true);
			writeToFile(pair.getPublic(), "public.key", true);	
		}
		catch(Exception e)
		{

		}

	}
	public static byte[] toBytes(Object obj)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{			 
			ObjectOutput out = null;
			out = new ObjectOutputStream(bos);   
			out.writeObject(obj);
			out.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();

		}
		return bos.toByteArray();
	}
	public static Object fromBytes(byte[] bytes)
	{
		Object o = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			o = in.readObject(); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return o; 
	}
	public static byte[] encrypt(byte[] inpBytes, Key key,
			String xform) throws Exception {
		Cipher cipher = Cipher.getInstance(xform);
		cipher.init(Cipher.ENCRYPT_MODE, key);		    
		return cipher.doFinal(inpBytes);
	}
	public static byte[] decrypt(byte[] inpBytes, Key key,
			String xform) throws Exception{
		Cipher cipher = Cipher.getInstance(xform);
		cipher.init(Cipher.DECRYPT_MODE, key);		
		return cipher.doFinal(inpBytes);
	}
	public static KeyPair generateKeypair()
	{
		KeyPairGenerator kpg = null;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(3072); // 512 is the keysize.

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		KeyPair kp = kpg.generateKeyPair();
		return kp;
		//PublicKey pubk = kp.getPublic();
		// PrivateKey prvk = kp.getPrivate();
	}

	public static void writeToFile(Key key, String pathname, boolean overwrite) throws IOException 
	{
		File filePublicKeyOld = new File(pathname);
		if(overwrite && filePublicKeyOld != null)
		{
			filePublicKeyOld.delete();
			System.out.println("Overwritten old key.");
		}
		else if (!overwrite && filePublicKeyOld != null)
			throw new IOException("File already exists and overwriting disabled");
			
		
		FileOutputStream fos = new FileOutputStream(pathname);
		Key originalKey = key;
		fos.write(toBytes(originalKey));		
		
		if(getKeyFromFile(pathname).equals(originalKey))
			System.out.println("Successfully wrote key to file");
		else
			System.out.println("Key in file does not match original key");

	}
	
	
	public static Key getKeyFromFile(String pathname) throws IOException
	{
		File fileKey = new File(pathname);
		FileInputStream fis = new FileInputStream(fileKey);
		byte[] KeyByte = new byte[(int)fileKey.length()];
		fis.read(KeyByte);		
		return (Key)fromBytes(KeyByte);		
	}
	
	public static byte[] getByteFromStream(DataInputStream stream) throws IOException
	{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = stream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();
		
	}
	/* Keysize = 0 will generate default size key */
	public static SecretKey generateAESKey(int keysize)
	{
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance("AES");
			if (keysize == 0)							
				kg.init(keysize);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return kg.generateKey();
		
	}




}

package stockkeeper.network;

import java.io.Serializable;
import java.security.Key;
import java.util.UUID;

import javax.crypto.SecretKey;

import stockkeeper.encryption.EncryptionUtils;

public class EncryptedMessage implements Serializable {	
	
	
	public EncryptedMessage(Object message, UUID playerUUID, SecretKey key) {
		byte [] byteMessage = EncryptionUtils.toBytes(message);
		try {
			this.message = EncryptionUtils.encrypt(byteMessage, key, key.getAlgorithm());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.playerUUID = playerUUID;
	}
	public byte[] message;	
	public UUID playerUUID;
	
	public Object decrypt(Key key) throws Exception
	{
		byte [] byteMessage = EncryptionUtils.decrypt(message, key, key.getAlgorithm())	;
		return EncryptionUtils.fromBytes(byteMessage);
	}

}

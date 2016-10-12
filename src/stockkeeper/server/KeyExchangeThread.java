package stockkeeper.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import stockkeeper.data.UserCredentials;
import stockkeeper.encryption.EncryptionUtils;
import stockkeeper.network.KeyExchangeMessage;
import stockkeeper.network.KeyExchangeMessage.MessageType;

public class KeyExchangeThread extends Thread {
	public KeyExchangeThread(KeyPair keys_, Map<UUID, SecretKey> secretKeys_) {
		keys = keys_;
		secretKeys = secretKeys_;
	}
	KeyPair keys;
	Map<UUID, SecretKey> secretKeys;
	private boolean running = true;

	@Override
	public void run() {
		ServerSocket keySocket = null;

		try {
			keySocket = new ServerSocket(55556);
			while(running)
			{
				//Socket socket = keySocket.accept();				
				handleConnection(keySocket.accept());
				//handleConnection(socket);
				//handleConnection(socket);
				//System.out.println(secretKeys.isEmpty());
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}


		super.run();
	}
	
	public void stopListening()
	{
		running = false;		
	}

	private void handleConnection(Socket accept) throws IOException, ClassNotFoundException {
		ObjectInputStream incommingMessage = new ObjectInputStream(accept.getInputStream());
		KeyExchangeMessage message = (KeyExchangeMessage)incommingMessage.readObject();
		
		switch(message.messageType)
		{
		case PUBLICKEY_REQUEST:
			sendPublicKey(accept);
			break;
		case USER_CREDENTIALS:
			createUserSession(accept, message);
			break;	
		
		}
		ObjectInputStream incommingMessage2 = new ObjectInputStream(accept.getInputStream());
		KeyExchangeMessage message2 = (KeyExchangeMessage)incommingMessage.readObject();
		
		switch(message2.messageType)
		{
		case PUBLICKEY_REQUEST:
			sendPublicKey(accept);
			break;
		case USER_CREDENTIALS:
			createUserSession(accept, message2);
			break;
		
		
		}
		
	}

	private void createUserSession(Socket accept, KeyExchangeMessage message) {
		byte[] decryptedMessage = null;
		try {
			decryptedMessage = EncryptionUtils.decrypt(message.message, keys.getPrivate(), "RSA/ECB/PKCS1Padding");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserCredentials credentials = (UserCredentials)EncryptionUtils.fromBytes(decryptedMessage);
		if(verifyUser())
			secretKeys.put(credentials.user, credentials.key);
		
		System.out.println("Session started for user:" + credentials.user.toString());
		
		
	
	}

	private boolean verifyUser() {
		// TODO Auto-generated method stub
		return true;
	}

	private void sendPublicKey(Socket accept) throws IOException {
		
		KeyExchangeMessage message = new KeyExchangeMessage(MessageType.PUBLICKEY_RESPONSE, EncryptionUtils.toBytes(keys.getPublic()));
		
		ObjectOutputStream outboundMessage = new ObjectOutputStream(accept.getOutputStream());
		outboundMessage.writeObject(message);
	}

}

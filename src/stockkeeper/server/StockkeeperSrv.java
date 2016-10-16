package stockkeeper.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertPathChecker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import stockkeeper.network.ChestContentsMessage;
import stockkeeper.network.ChestGroupReturnMessage;
import stockkeeper.network.ConnectionFailedMessage;
import stockkeeper.network.CountMessage;
import stockkeeper.network.CountReturnMessage;
import stockkeeper.network.EncryptedMessage;
import stockkeeper.network.FindItemMessage;
import stockkeeper.network.FindItemReturnMessage;
import stockkeeper.network.GroupChangedMessage;
import stockkeeper.network.GroupChangedReturn;
import stockkeeper.network.InvalidPasswordMessage;
import stockkeeper.network.InviteGroupMessage;
import stockkeeper.network.InviteGroupReturnMessage;
import stockkeeper.network.InviteMessage;
import stockkeeper.network.InviteReturnMessage;
import stockkeeper.network.MakeGroupMessage;
import stockkeeper.network.MakeGroupReturnMessage;
import stockkeeper.network.RegisterMessage;
import stockkeeper.network.RegisterReturnMessage;
import stockkeeper.network.StockKeeperMessage;
import stockkeeper.network.StockKeeperMessage.MessageType;
import stockkeeper.network.StockkeeperReturnMessage;
import stockkeeper.network.checkChestGroupMessage;
import stockkeeper.sql.StockkeeperSQL;
import stockkeeper.data.Position;
import stockkeeper.encryption.EncryptionUtils;;

public class StockkeeperSrv {
	private static final int MAKEGROUP_LEVEL = 2;
	private static final int INVITEGROUP_LEVEL = 1;
	
	static StockkeeperSQL SQL =new StockkeeperSQL();
	static KeyPair keys;
	static Timer timer = new Timer();
	static Map<String, Integer> activeInvites = new HashMap<String, Integer>();
	static Map<UUID, SecretKey> secretKeys = new HashMap<UUID, SecretKey>();
	public static Logger LOG = Logger.getLogger(StockkeeperSrv.class.getName());
	

	public static void main(String[] args) {
		
		
		 FileHandler fh;
		try {
			fh = new FileHandler("Stockkeeper.log");
			 LOG.addHandler(fh);
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
	    // LOG.addHandler(fh);
		LOG.setLevel(Level.FINE);		
		//Generate keypair to be used for sharing secrets
		keys = EncryptionUtils.generateKeypair();
		//Create a map that holds a secret for every client
		SQL.createTables();		
		
		KeyExchangeThread keyExchange = new KeyExchangeThread(keys, secretKeys);
		keyExchange.start();
		
		ServerSocket serverSocket = null;
		try { 
			serverSocket = new ServerSocket(55555);			
		
		while (true) {
			listen(serverSocket.accept());						
			
		}
		} catch (IOException e) {
			LOG.log(Level.SEVERE,"",e);			
		}
		
	}
	public static void listen(Socket socket)
	{
		try {
			
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			LOG.info(socket.getInetAddress().toString() + " sent a message");
			EncryptedMessage encryptedMessage = (EncryptedMessage)inStream.readObject();
			Key key = secretKeys.get(encryptedMessage.playerUUID);
			StockKeeperMessage message = (StockKeeperMessage)encryptedMessage.decrypt(key);
			LOG.info("Valid "+ message.messageType.toString() + " from user: " + message.userName +"@" + socket.getInetAddress().toString() );
			handleMessage(socket, message); 
			
			
			
			//socket.close();
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Failed to process message from " + socket.getInetAddress().toString(), e);
			connectionFailed(socket);
			
			
		}
	}
	private static void connectionFailed(Socket socket) {
		
		try {
			ObjectOutputStream returnMessage = new ObjectOutputStream(socket.getOutputStream());
			ConnectionFailedMessage connectFailed = new ConnectionFailedMessage();
			returnMessage.writeObject(new ConnectionFailedMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.log(Level.WARNING, "Can't reach " + socket.getInetAddress().toString(), e);
		}
		
	}
	private static void handleMessage(Socket socket, StockKeeperMessage message) throws IOException {
		ObjectOutputStream returnMessage = new ObjectOutputStream(socket.getOutputStream());
		
		switch(message.messageType)
		{
		case CHESTCONTENTS:  
			if(SQL.verifyUser(message.playerUUID, message.password))
				handleChestMessage(message, returnMessage);
			else
				handleInvalidPassword(message, returnMessage, socket);
			break;
		case COUNT:
			if(SQL.verifyUser(message.playerUUID, message.password))
				handlecountMessage(message, returnMessage);
			else
				handleInvalidPassword(message, returnMessage, socket);
			break;
		case INVITE:
			if(SQL.verifyUser(message.playerUUID, message.password))
				handleInviteMessage((InviteMessage)message, returnMessage);
			else
				handleInvalidPassword(message, returnMessage, socket);
			break;
		case REGISTER:
			handleRegistration(message, returnMessage);
			break;
		case MAKEGROUP:
			if(SQL.verifyUser(message.playerUUID, message.password))
				handleMakeGroup(message, returnMessage);
			else
				handleInvalidPassword(message, returnMessage, socket);
			break;
		case INVITEGROUP:
			if(SQL.verifyUser(message.playerUUID, message.password))
				handleInviteGroup(message, returnMessage);
			else
				handleInvalidPassword(message, returnMessage, socket);
			break;
		case CHECKGROUP:
			if(SQL.verifyUser(message.playerUUID, message.password))
				handleCheckGroup(message, returnMessage);
			else
				handleInvalidPassword(message, returnMessage, socket);
			break;
		case GROUPCHANGED:
			if(SQL.verifyUser(message.playerUUID, message.password))
				handleGroupChanged(message, returnMessage);
			break;
		case FINDITEM:
			if(SQL.verifyUser(message.playerUUID, message.password))
				handleFindItem(message, returnMessage);
			else
				handleInvalidPassword(message, returnMessage, socket);
			break;
		}
		returnMessage.close();
		
	}
	private static void handleInvalidPassword(StockKeeperMessage message, ObjectOutputStream returnMessage, Socket socket) throws IOException {
		LOG.warning("User " +  message.userName + "@" + socket.getInetAddress().toString() + " used an invalid password" );
		InvalidPasswordMessage invalidPassword = new InvalidPasswordMessage();
		try {
			returnMessage.writeObject(new EncryptedMessage(invalidPassword, message.playerUUID, secretKeys.get(message.playerUUID)));
			LOG.info("Sent "+ message.messageType.toString() + " to: "  + message.userName);
		} catch (IOException e) {			
			LOG.warning("Was unable to send "+ message.messageType.toString() + " to: " + message.userName) ;
		}
		
	}
	private static void handleFindItem(StockKeeperMessage message, ObjectOutputStream returnMessage) {
		
		String itemName = (String)message.getField("itemName");
		FindItemReturnMessage findItem = new FindItemReturnMessage(SQL.findItem(itemName));
		
		try {
			returnMessage.writeObject(new EncryptedMessage(findItem, message.playerUUID, secretKeys.get(message.playerUUID)));
			LOG.info("Sent "+ message.messageType.toString() + " to: "  + message.userName);
		} catch (IOException e) {			
			LOG.warning("Was unable to send "+ message.messageType.toString() + " to: " + message.userName) ;
		}
		
	}
	private static void handleGroupChanged(StockKeeperMessage message, ObjectOutputStream returnMessage) {
		SQL.changeChestGroup(message);
		try {
			returnMessage.writeObject(new EncryptedMessage(new GroupChangedReturn(), message.playerUUID, secretKeys.get(message.playerUUID)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.warning("Was unable to send "+ message.messageType.toString() + " to: " + message.userName) ;
		}
		
	}
	private static void handleCheckGroup(StockKeeperMessage message, ObjectOutputStream returnMessage) {
		
		String group = SQL.checkGroup(message);
		ChestGroupReturnMessage checkGroup = new ChestGroupReturnMessage(group);
		try {
			returnMessage.writeObject(new EncryptedMessage(checkGroup, message.playerUUID, secretKeys.get(message.playerUUID)));
			LOG.info("Sent "+ message.messageType.toString() + " to: "  + message.userName);
		} catch (IOException e) {			
			LOG.warning("Was unable to send "+ message.messageType.toString() + " to: " + message.userName) ;
		}
	}
	private static void handleChestMessage(StockKeeperMessage message, ObjectOutputStream returnMessage)
			 {
		//ChestContentsMessage contents = (ChestContentsMessage)message; 
		SQL.updateChest(message); 
		StockkeeperReturnMessage chestContents = new StockkeeperReturnMessage(stockkeeper.network.StockkeeperReturnMessage.MessageType.CHESTCONTENTS);
		try {
			returnMessage.writeObject(new EncryptedMessage(chestContents, message.playerUUID, secretKeys.get(message.playerUUID)));
			LOG.info("Sent "+ message.messageType.toString() + " to: "  + message.userName);
		} catch (IOException e) {			
			LOG.warning("Was unable to send "+ message.messageType.toString() + " to: " + message.userName) ;
		}
	}
	private static void handlecountMessage(StockKeeperMessage message, ObjectOutputStream returnMessage)
			 {
		
		String itemName = (String)message.getField("itemName");
		int result = SQL.countItem(message);
		CountReturnMessage countReturn = new CountReturnMessage(result, itemName);
		try {
			returnMessage.writeObject(new EncryptedMessage(countReturn, message.playerUUID, secretKeys.get(message.playerUUID)));
			LOG.info("Sent "+ message.messageType.toString() + " to: "  + message.userName);
		} catch (IOException e) {			
			LOG.warning("Was unable to send "+ message.messageType.toString() + " to: " + message.userName) ;
		}
		
	}
	private static void handleInviteGroup(StockKeeperMessage message, ObjectOutputStream returnMessage) {
		int msgGroupLevel = (int)message.getField("grouplevel");
		int grouplevel =SQL.getGroupLevel(message);
		if(grouplevel >= INVITEGROUP_LEVEL && grouplevel > msgGroupLevel)
		{
			SQL.addToGroup(message);
			try {
				returnMessage.writeObject(new EncryptedMessage(new InviteGroupReturnMessage(), message.playerUUID, secretKeys.get(message.playerUUID)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private static void handleMakeGroup(StockKeeperMessage message, ObjectOutputStream returnMessage) {
		if(SQL.getUserLevel(message.playerUUID) >= MAKEGROUP_LEVEL)
		{
			SQL.makeGroup(message);
			try {
				returnMessage.writeObject(new EncryptedMessage(new MakeGroupReturnMessage(), message.playerUUID, secretKeys.get(message.playerUUID)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
		}
		
	}
	private static void handleRegistration(StockKeeperMessage message, ObjectOutputStream returnMessage) {
		String masterCode = "6d02c658-2806-4b70-9e2d-be1cfed7329e";
		
		String inviteCode = (String)message.getField("inviteCode");
		if(activeInvites.containsKey(inviteCode))
		{
			SQL.registerUser(message.playerUUID, message.password, activeInvites.get(inviteCode));
			try {
				returnMessage.writeObject(new EncryptedMessage(new RegisterReturnMessage(true), message.playerUUID, secretKeys.get(message.playerUUID)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(inviteCode.equals(masterCode))
		{
			SQL.registerUser(message.playerUUID, message.password, 5);	
			try {
				returnMessage.writeObject(new EncryptedMessage(new RegisterReturnMessage(true), message.playerUUID, secretKeys.get(message.playerUUID)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				returnMessage.writeObject(new EncryptedMessage(new RegisterReturnMessage(false).message = "" , message.playerUUID, secretKeys.get(message.playerUUID)));
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		
	}
	private static void handleInviteMessage(StockKeeperMessage message, ObjectOutputStream returnMessage) {
		
		int level = (int)message.getField("level");
		if(SQL.hasInviteLevel(message.playerUUID, level))
		{
			final String inviteCode = UUID.randomUUID().toString();
			activeInvites.put(inviteCode, level);
			
			timer.schedule(new TimerTask() {
				  @Override
				  public void run() {
				    activeInvites.remove(inviteCode);				    
				  }
				}, TimeUnit.MINUTES.toMillis(30));
			try {
				returnMessage.writeObject(new InviteReturnMessage(inviteCode));
				LOG.info("Sent "+ message.messageType.toString() + " to: "  + message.userName);
			} catch (IOException e) {			
				LOG.warning("Was unable to send "+ message.messageType.toString() + " to: " + message.userName) ;
			}		
		}
	}
	
}

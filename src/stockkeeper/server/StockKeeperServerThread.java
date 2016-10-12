package stockkeeper.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.PrivateKey;

import stockkeeper.data.Stack;
import stockkeeper.encryption.EncryptionUtils;
import stockkeeper.network.ChestContentsMessage;
import stockkeeper.network.CountMessage;
import stockkeeper.network.CountReturnMessage;

import stockkeeper.network.StockKeeperMessage;
import stockkeeper.network.StockKeeperMessage.MessageType;
import stockkeeper.sql.StockkeeperSQL;

public class StockKeeperServerThread extends Thread{
	private Socket socket = null;
	static StockkeeperSQL SQL =new StockkeeperSQL();
	PrivateKey key;

    public StockKeeperServerThread(Socket socket) {
        super("StockKeeperServerThread");
        this.socket = socket;
        
			//key = EncryptionUtils.getPrivatecKeyFromFile("private.key");
		
    }
    
    public void run() {

    	
    	try {
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			
			
					ByteArrayOutputStream buffer = new ByteArrayOutputStream();

					int nRead;
					byte[] data = new byte[16384];

					while ((nRead = inStream.read(data, 0, data.length)) != -1) {
					  buffer.write(data, 0, nRead);
					}

					buffer.flush();

					
			
						
			byte[] inMessage = buffer.toByteArray();
			byte[] decryptedMessage = EncryptionUtils.decrypt(inMessage, key, EncryptionUtils.xform);
    		
			StockKeeperMessage message = (StockKeeperMessage)EncryptionUtils.fromBytes(decryptedMessage);
			ObjectOutputStream returnMessage = new ObjectOutputStream(socket.getOutputStream());
			switch(message.messageType)
			{
			case CHESTCONTENTS:        	
				ChestContentsMessage contents = (ChestContentsMessage)message; 
				SQL.updateChest(contents); 

				returnMessage.writeObject(new StockKeeperMessage(MessageType.CHESTCONTENTS));
				break;

			case COUNT:
				CountMessage countMessage = (CountMessage)message;
				int result = SQL.countItem(countMessage);
				//ObjectOutputStream returnMessage = new ObjectOutputStream(socket.getOutputStream());        		
				returnMessage.writeObject(new CountReturnMessage(result, countMessage.itemName));
				break;
			} 
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
	

}

package stockkeeper.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.Key;

import stockkeeper.network.EncryptedMessage;
import stockkeeper.network.StockKeeperMessage;

public class MessageRunnable implements Runnable {
	
	Socket socket;
	public MessageRunnable(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try
		{
		//socket.setTcpNoDelay(true);
		
		ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
		StockkeeperSrv.LOG.info(socket.getInetAddress().toString() + " sent a message");
		EncryptedMessage encryptedMessage = (EncryptedMessage)inStream.readObject();
		
		Key key = StockkeeperSrv.secretKeys.get(encryptedMessage.playerUUID);
		StockKeeperMessage message = (StockKeeperMessage)encryptedMessage.decrypt(key);
		StockkeeperSrv.LOG.info("Valid "+ message.messageType.toString() + " from user: " + message.userName +"@" + socket.getInetAddress().toString() );
		StockkeeperSrv.handleMessage(socket, message);
		
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StockkeeperSrv.connectionFailed(socket);
		}
		
	}

}

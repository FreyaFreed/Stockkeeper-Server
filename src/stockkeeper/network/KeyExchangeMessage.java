package stockkeeper.network;

import java.io.Serializable;

public class KeyExchangeMessage implements Serializable {
	
	public enum MessageType {
		PUBLICKEY_REQUEST,
		PUBLICKEY_RESPONSE,
		USER_CREDENTIALS,
		SESSION_ID
		

	}	
	public MessageType messageType;
	public byte[] message;
	
	public KeyExchangeMessage(MessageType messageType_)
	{
		messageType = messageType_;
		
	}
	public KeyExchangeMessage(MessageType messageType_, byte[] message_)
	{
		messageType = messageType_;
		message = message_;
		
	}

}

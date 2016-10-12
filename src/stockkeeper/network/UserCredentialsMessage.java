package stockkeeper.network;

public class UserCredentialsMessage extends KeyExchangeMessage {

	public UserCredentialsMessage(MessageType messageType_) {
		super(messageType_);
		// TODO Auto-generated constructor stub
	}
	byte[] encryptedCredentials;

}

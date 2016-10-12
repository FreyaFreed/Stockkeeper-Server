package stockkeeper.network;

public class InvalidPasswordMessage extends StockkeeperReturnMessage {

	public InvalidPasswordMessage() {
		super(MessageType.INVALID_PASSWORD);		
	}

}

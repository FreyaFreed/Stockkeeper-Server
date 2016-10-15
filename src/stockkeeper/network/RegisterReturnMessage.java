package stockkeeper.network;

public class RegisterReturnMessage extends StockkeeperReturnMessage {

	public RegisterReturnMessage(boolean success) {
		super(MessageType.REGISTER);		
		this.success = success;		
		
	}
	
	
	

}

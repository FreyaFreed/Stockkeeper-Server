package stockkeeper.network;

public class RegisterReturnMessage extends StockkeeperReturnMessage {

	public RegisterReturnMessage(String registrationMessage, boolean success) {
		super(MessageType.REGISTER);
		this.registrationMessage = registrationMessage;
		this.success = success;		
		
	}
	String registrationMessage;
	boolean success;
	

}

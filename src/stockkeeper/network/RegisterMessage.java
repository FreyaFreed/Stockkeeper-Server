package stockkeeper.network;

public class RegisterMessage extends StockKeeperMessage {

	public String inviteCode;

	public RegisterMessage(String inviteCode) {
		super(MessageType.REGISTER);
		this.inviteCode = inviteCode;
	}
	

}

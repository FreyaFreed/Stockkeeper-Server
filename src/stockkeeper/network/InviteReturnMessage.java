package stockkeeper.network;

public class InviteReturnMessage extends StockkeeperReturnMessage {
	
	public String InviteCode;

	public InviteReturnMessage(String inviteCode) {
		super(MessageType.INVITE);
		InviteCode = inviteCode;
	}

}

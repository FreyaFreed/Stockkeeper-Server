package stockkeeper.network;

import java.io.Serializable;

public class StockkeeperReturnMessage implements Serializable {
	public enum MessageType {
		CHESTCONTENTS,
		COUNT, INVITE, REGISTER, CHECKGROUP, FINDITEM, INVALID_PASSWORD, CONNECTION_FAILED, GROUPCHANGED, MAKEGROUP, INVITEGROUP

	}
	public MessageType messageType;
	public StockkeeperReturnMessage(MessageType messageType) {
		super();
		this.messageType = messageType;
	}
	boolean success;
	public String message;
	

}

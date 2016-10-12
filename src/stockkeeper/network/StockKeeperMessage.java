package stockkeeper.network;

import java.io.Serializable;
import java.util.UUID;


public class StockKeeperMessage implements Serializable {

	public enum MessageType {
		CHESTCONTENTS,
		COUNT, INVITE, REGISTER, MAKEGROUP, INVITEGROUP, CHECKGROUP, GROUPCHANGED, FINDITEM

	}
	public MessageType messageType;
	public UUID playerUUID;
	public String serverIP;
	public String worldName;
	public String userName;

	public String password;
	public StockKeeperMessage(MessageType messageType_)
	{
		messageType = messageType_;
	}


}

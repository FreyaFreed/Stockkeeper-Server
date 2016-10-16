package stockkeeper.network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StockkeeperReturnMessage implements Serializable {
	public enum MessageType {
		CHESTCONTENTS,
		COUNT, INVITE, REGISTER, CHECKGROUP, FINDITEM, INVALID_PASSWORD, CONNECTION_FAILED, GROUPCHANGED, MAKEGROUP, INVITEGROUP

	}
	public MessageType messageType;
	public StockkeeperReturnMessage(MessageType messageType) {
		super();
		this.messageType = messageType;
		fields = new HashMap<String, Object>();
	}
	boolean success;
	public String message;
private Map<String, Object> fields;
	
	public Object getField(String fieldName)
	{
		return fields.get(fieldName);
	}	
	public void setField(String fieldName, Object field)
	{
		fields.put(fieldName, field);
	}
	

}

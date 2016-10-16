package stockkeeper.network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class StockKeeperMessage implements Serializable {
	
	public MessageType messageType;
	public UUID playerUUID;
	public String serverIP;
	public String worldName;
	public String userName;
	public String password;
	
	private Map<String, Object> fields;
	
	public Object getField(String fieldName)
	{
		return fields.get(fieldName);
	}	
	public void setField(String fieldName, Object field)
	{
		fields.put(fieldName, field);
	}
	public StockKeeperMessage(MessageType messageType_)
	{
		fields = new HashMap<String, Object>();
		messageType = messageType_;
	}


}

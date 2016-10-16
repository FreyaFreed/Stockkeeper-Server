package stockkeeper.network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StockkeeperReturnMessage implements Serializable {	
	public MessageType messageType;
	public StockkeeperReturnMessage(MessageType messageType) {
		super();
		this.messageType = messageType;
		fields = new HashMap<String, Object>();
	}
	public boolean success;
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

package stockkeeper.network;

import stockkeeper.data.Position;
import stockkeeper.network.StockKeeperMessage;

public class GroupChangedMessage extends StockKeeperMessage {

	public GroupChangedMessage(String newGroup, Position top, Position bottom) {
		super(MessageType.GROUPCHANGED);
		this.newGroup = newGroup;
		this.top = top;
		this.bottom = bottom;
		
	}
	public String newGroup;
	public Position top, bottom;
	public String getTopID()
	{
		if(top != null)
			return new String(this.serverIP + ":" + top.worldName + ":" + top.x + ":" + top.y + ":" + top.z);
		else
			return null;
	}
	public String getBottomID()
	{
		if(bottom != null)
			return new String(this.serverIP + ":" + bottom.worldName + ":" + bottom.x + ":" + bottom.y + ":" + bottom.z);
		else
			return null;
	}

}

package stockkeeper.network;

import stockkeeper.data.Position;

public class checkChestGroupMessage extends StockKeeperMessage {

	public checkChestGroupMessage(Position top, Position bottom) {
		super(MessageType.CHECKGROUP);
	}
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

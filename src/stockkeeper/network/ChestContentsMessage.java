package stockkeeper.network;

import java.io.Serializable;
import java.util.List;
import stockkeeper.data.Position;
import stockkeeper.data.Stack;



public class ChestContentsMessage extends StockKeeperMessage {
	public ChestContentsMessage(Position chest_, List<Stack> stacks_) {
		super(MessageType.CHESTCONTENTS);
		chest = chest_;
		stacks = stacks_;
		isDoubleChest = false;
		
	}
	public ChestContentsMessage(Position chest_,Position adjacentChest_, List<Stack> stacks_) {
		super(MessageType.CHESTCONTENTS);
		chest = chest_;
		chest = adjacentChest_;
		stacks = stacks_;
		isDoubleChest = true;
	}
	
	public Position chest, adjacentChest;
	public List<Stack> stacks;
	public boolean isDoubleChest;
	public String getID()
	{
		
		return new String(this.serverIP + ":" + chest.worldName + ":" + chest.x + ":" + chest.y + ":" + chest.z);
	}
	

}

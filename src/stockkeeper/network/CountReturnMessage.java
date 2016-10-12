package stockkeeper.network;

public class CountReturnMessage extends StockkeeperReturnMessage {
	
	public int amount;	
	public String itemName;
	public CountReturnMessage(int amount_, String itemName) {
		super(MessageType.COUNT);
		amount = amount_;
		this.itemName = itemName;
		
	}

}

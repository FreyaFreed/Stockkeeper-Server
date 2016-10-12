package stockkeeper.network;

public class CountMessage extends StockKeeperMessage {
	
	public CountMessage(String itemName_) {
		super(MessageType.COUNT);
		itemName = itemName_;
	}
	public String itemName;

}

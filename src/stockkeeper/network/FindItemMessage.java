package stockkeeper.network;

public class FindItemMessage extends StockKeeperMessage {

	public FindItemMessage(String itemName) {
		super(MessageType.FINDITEM);
		this.itemName = itemName;
	}
	public String itemName;
}

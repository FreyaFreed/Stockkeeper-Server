package stockkeeper.network;

public class MakeGroupMessage extends StockKeeperMessage {

	public MakeGroupMessage(String groupname) {
		super(MessageType.MAKEGROUP);
		this.groupname = groupname;
		
	}
	public String groupname;

}

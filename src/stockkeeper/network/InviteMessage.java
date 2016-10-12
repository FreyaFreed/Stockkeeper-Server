package stockkeeper.network;

public class InviteMessage extends StockKeeperMessage {

	public InviteMessage(int level_) {
		super(MessageType.INVITE);
		level = level_;
	}
	public int level;

}

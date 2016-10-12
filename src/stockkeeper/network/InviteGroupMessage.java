package stockkeeper.network;

public class InviteGroupMessage extends StockKeeperMessage {

	public InviteGroupMessage(String groupname, int grouplevel, String username) {
		super(MessageType.INVITEGROUP);
		this.groupname = groupname;
		this.grouplevel = grouplevel;
		this.username = username;
	}
	public String groupname;
	public int grouplevel;
	public String username;
}

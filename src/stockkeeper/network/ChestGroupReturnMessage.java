package stockkeeper.network;

public class ChestGroupReturnMessage extends StockkeeperReturnMessage {

	public String group;

	public ChestGroupReturnMessage(String group) {
		super(MessageType.CHECKGROUP);
		this.group = group;
	}

}

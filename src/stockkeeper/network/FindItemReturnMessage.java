package stockkeeper.network;

import java.sql.ResultSet;

import javax.sql.rowset.CachedRowSet;

public class FindItemReturnMessage extends StockkeeperReturnMessage {

	
	public CachedRowSet itemResults;

	public FindItemReturnMessage(CachedRowSet itemResults) {
		super(MessageType.FINDITEM);
		this.itemResults = itemResults;
	}
}

package stockkeeper.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

import org.sqlite.SQLiteConfig.JournalMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sqlite.SQLiteConnection;
import org.sqlite.SQLiteDataSource;

import stockkeeper.data.Stack;
import stockkeeper.network.ChestContentsMessage;
import stockkeeper.network.CountMessage;
import stockkeeper.network.GroupChangedMessage;
import stockkeeper.network.InviteGroupMessage;
import stockkeeper.network.MakeGroupMessage;
import stockkeeper.server.StockkeeperSrv;

import com.sun.rowset.CachedRowSetImpl;

public class StockkeeperSQL {
	
	private static final int GROUPOWNER_LEVEL = 4;
	Connection con;
	Logger LOG = Logger.getLogger(this.getClass().getName());
	public StockkeeperSQL() {
		con = getConnection();
	}
	
	public int countItem(CountMessage message)
	{
		try
		{
			//Connection con = getConnection();	
			String query = "SELECT SUM(stackSize) AS total FROM stack  INNER JOIN chest ON stack.chestid = chest.chestid WHERE itemName = ? AND ip = ?";
			PreparedStatement count = con.prepareStatement(query);
			count.setString(1, message.itemName);
			count.setString(2, message.serverIP);
			int result = count.executeQuery().getInt("total");
			System.out.println(result);
			return result;
			
		}
		catch(SQLException e)
		{
			return -1;
			
		}
	}
	
	public void updateChest(ChestContentsMessage chest)
	{
		
		try
		{
			int i = 0;
			for(Stack stack :chest.stacks)
			{
				
				String query = "REPLACE INTO stack(slot, chestid, itemName, stackSize) VALUES (?,?,?,?)";
				PreparedStatement insertStacks = con.prepareStatement(query);
				insertStacks.setInt(1, i);				
				insertStacks.setString(2, chest.getID());
				
				if(stack != null)
					insertStacks.setString(3, stack.name);
				else
					insertStacks.setString(3, null);
				if(stack != null)
					insertStacks.setInt(4, stack.size);
				else
					insertStacks.setString(4, null);				
				insertStacks.executeUpdate();
				insertStacks.close();
				i++;
			}
			String query = "REPLACE INTO chest(chestid, x, y, z, ip) VALUES (?,?,?,?,?)";
			PreparedStatement updateChest = con.prepareStatement(query);
			updateChest.setString(1, chest.getID());
			updateChest.setInt(2, chest.chest.x);
			updateChest.setInt(3, chest.chest.y);
			updateChest.setInt(4, chest.chest.z);
			updateChest.setString(5, chest.serverIP);
			updateChest.executeUpdate();
			updateChest.close();
			
			 
		}
		catch(SQLException e)
		{
			LOG.log(Level.WARNING, chest.userName ,e);
			
		}
	}
	Connection getConnection()
	{
		
		Connection con = null;
	    try {
	    	org.sqlite.SQLiteConfig config = new org.sqlite.SQLiteConfig();
	        config.setJournalMode(JournalMode.MEMORY);	        
	        con = DriverManager.getConnection("jdbc:sqlite:stockkeeper.db");
	        config.apply(con);
	      Class.forName("org.sqlite.JDBC");
	      LOG.log(Level.INFO, "Successfully connected to" + con.getMetaData().getDatabaseProductName().toString() );
	      
	      
	    } catch ( Exception e ) {
	    	LOG.log(Level.SEVERE, "" ,e);
	      
	    }
	    
	    return con;
	}
	public void createTables()
	{		
		try {			
			ClassLoader cl = StockkeeperSQL.class.getClassLoader();			
			InputStreamReader reader = new InputStreamReader(cl.getResourceAsStream("Databaseschema"));
			String statement = "";
			int i;
			while((i= reader.read())!=-1)
			{
				char c = (char)i;
				statement += c;
			}			
			for(String query : statement.split(";"))
			{
				//query = query.trim();				
				//query.replaceAll("[\n\r]", "");
				PreparedStatement createTables = con.prepareStatement(query +";");				
				createTables.executeUpdate();				
				createTables.close();			}
		
		} catch (IOException e) {
			LOG.log(Level.WARNING, "" ,e);
		} catch (SQLException e) {
			LOG.log(Level.WARNING, "" ,e);
		} 
	}

	public boolean hasInviteLevel(UUID playerUUID, int level) {
		int result = -1;
		try
		{
		String query = "SELECT userlevel FROM user WHERE userid = ?";
		PreparedStatement userlevel = con.prepareStatement(query);
		userlevel.setString(1, playerUUID.toString());
		result = userlevel.executeQuery().getInt("userlevel");
		}
		catch (SQLException e) {
			LOG.log(Level.WARNING, playerUUID.toString() ,e);
		}
		if(result >= 1 && result > level)		
			return true;
		else
			return false;
		
		
	}
	public int getUserLevel(UUID playerUUID)
	{
		int result = -1;
		try
		{
		String query = "SELECT userlevel FROM user WHERE userid = ?";
		PreparedStatement userlevel = con.prepareStatement(query);
		userlevel.setString(1, playerUUID.toString());
		result = userlevel.executeQuery().getInt("userlevel");
		}
		catch (SQLException e) {
			LOG.log(Level.WARNING, playerUUID.toString() ,e);
		}
		return result;
	}

	public void registerUser(UUID playerUUID, String password, Integer level) {
		boolean success = false;
		try
		{
		String query = "INSERT INTO user(userid, password, userlevel) VALUES (?,?,?)";
		PreparedStatement insertUser = con.prepareStatement(query);
		insertUser.setString(1, playerUUID.toString());
		insertUser.setString(2, hash(password));
		insertUser.setInt(3, level);
		int rowsUpdated =  insertUser.executeUpdate();
		insertUser.close();
		success = (rowsUpdated != 0);
		
		if(success)
			LOG.info("Succesfully registered user");
		
		}
		catch(SQLException e)
		{
			LOG.log(Level.WARNING, playerUUID.toString() ,e);
		}
		
		
	}
	
	public CachedRowSet findItem(String itemName)
	{
		try
		{
		String query = "SELECT chest.x,chest.y,chest.z, sum(stackSize) AS itemTotal FROM chest INNER JOIN stack ON chest.chestid = stack.chestid  WHERE itemName=? GROUP BY x,y,z";
		PreparedStatement findItem = con.prepareStatement(query);
		findItem.setString(1, itemName);
		CachedRowSetImpl results = new CachedRowSetImpl();	
		results.populate(findItem.executeQuery());			
		return results;
		}
		catch(SQLException e)
		{
			LOG.log(Level.WARNING, "" ,e);
			return null;
		}
		
	}

	private String hash(String password) {
		// TODO Auto-generated method stub
		return password;
	}

	public void makeGroup(MakeGroupMessage message) {
		
		try
		{
		con.setAutoCommit(false);
		String query = "INSERT INTO groups(name) VALUES (?);";
		PreparedStatement makeGroup = con.prepareStatement(query);
		makeGroup.setString(1, message.groupname);		
		int rowsUpdated =  makeGroup.executeUpdate();
		query = "INSERT INTO user_group(userid, groupname, grouplevel) VALUES (?,?,?)";
		PreparedStatement addOwner = con.prepareStatement(query);
		addOwner.setString(1, message.playerUUID.toString());
		addOwner.setString(2, message.groupname);
		addOwner.setInt(3, GROUPOWNER_LEVEL);
		addOwner.executeUpdate();		
		con.commit();
		con.setAutoCommit(true);
		
		
		
		}
		catch (SQLException e) {
			LOG.log(Level.WARNING, message.userName ,e);
			try{
			con.rollback();
			}
			catch (Exception ex) {
				LOG.log(Level.WARNING, message.userName ,e);
			}
		}
		
	}

	public int getGroupLevel(InviteGroupMessage message) {
		int result = -1;
		try
		{
		String query = "SELECT grouplevel FROM user_group WHERE userid = ? AND groupname = ?";
		PreparedStatement grouplevel = con.prepareStatement(query);
		grouplevel.setString(1, message.playerUUID.toString());
		grouplevel.setString(2, message.groupname);
		result = grouplevel.executeQuery().getInt("grouplevel");
		}
		catch (SQLException e) {
			LOG.log(Level.WARNING, message.username ,e);
		}
		return result;
		
	}

	public void addToGroup(InviteGroupMessage message) {
		try
		{
		String userid = getUUID(message.username);
		if(userExists(userid))
		{
			String query = "INSERT INTO user_group(userid, groupname, grouplevel) VALUES (?,?,?)";
			PreparedStatement addToGroup = con.prepareStatement(query);
			addToGroup.setString(1, userid);
			addToGroup.setString(2, message.groupname);
			addToGroup.setInt(3, message.grouplevel);
			addToGroup.executeUpdate();	
		}
		}
		catch(SQLException e)
		{
			LOG.log(Level.WARNING, message.username ,e);
		}
		
	}
	private boolean userExists(String userid) throws SQLException {
		
		String query = "SELECT count(userid) AS userexists FROM user WHERE userid = ?";
		PreparedStatement grouplevel = con.prepareStatement(query);
		grouplevel.setString(1, userid);		
		int exists = grouplevel.executeQuery().getInt("userexists");
		
		return exists != 0;
	}

	private String getUUID(String username) {

		JSONObject object =null;
		try {
			String url = "https://mcapi.ca/uuid/player/" + username;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			//add request header
			con.setRequestProperty("User-Agent", "Java");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			
			JSONArray ar = new JSONArray(response.toString());		
			object = (JSONObject)ar.get(0);
		} catch (MalformedURLException e) {
			LOG.log(Level.WARNING, username ,e);
		} catch (ProtocolException e) {
			LOG.log(Level.WARNING, username ,e);
		} catch (JSONException e) {
			LOG.log(Level.WARNING, username ,e);			
		} catch (IOException e) {
			LOG.log(Level.WARNING, username ,e);
		}
		
		return (String)object.get("uuid");
		
		
	}
	
	public boolean groupExists(String groupname) throws SQLException
	{
		String query = "SELECT count(name) AS groupexists FROM groups WHERE name = ?";
		PreparedStatement grouplevel = con.prepareStatement(query);
		grouplevel.setString(1, groupname);		
		int exists = grouplevel.executeQuery().getInt("groupexists");
		
		return exists != 0;
		
	}

	public String checkGroup(String topID, String bottomID) {
		String topGroup = null;
		String bottomGroup = null;
		try
		{
		String query = "SELECT groupid FROM chest_group WHERE chestid = ?";
		PreparedStatement checkGroup = con.prepareStatement(query);
		
		checkGroup.setString(1, topID);	
		ResultSet result =  checkGroup.executeQuery();
		topGroup = result.getString("groupid");
		
		PreparedStatement checkbottomGroup = con.prepareStatement(query);
		checkbottomGroup.setString(1, bottomID);		
		bottomGroup = checkbottomGroup.executeQuery().getString("groupid");
		}
		catch(SQLException e)
		{
			StockkeeperSrv.LOG.warning(e.toString());			
		}
		//is still the same chest as last time it was updated
		if(bottomGroup == topGroup || bottomGroup == null)
			return topGroup;
		//
		else
			return null;
		
		
	}

	public void changeChestGroup(GroupChangedMessage message) {
		try
		{
		
			if(groupExists(message.newGroup))
			{
				String query = "REPLACE INTO chest_group(groupid,chestid) VALUES(?,?) ";
				
				PreparedStatement changeGroup = con.prepareStatement(query);
				changeGroup.setString(1, message.newGroup);		
				changeGroup.setString(2, message.getTopID());
				changeGroup.executeUpdate();			
				
				changeGroup.setString(1, message.newGroup);		
				changeGroup.setString(2, message.getTopID());
				changeGroup.executeUpdate();
			}
		
		}
		catch(SQLException e)
		{
			LOG.log(Level.WARNING, message.userName ,e);
			
		}
		
	}

	public boolean verifyUser(UUID playerUUID, String password) {
		boolean verified = false;
		String query = "SELECT count(userid) AS verify, password FROM user WHERE userid = ? AND password = ?";
		
		try
		{
		PreparedStatement verifyUser = con.prepareStatement(query);
		
			verifyUser.setString(1, playerUUID.toString());
			verifyUser.setString(2, password);
			ResultSet result =  verifyUser.executeQuery();			
			if(result.getInt("verify") > 0)
				verified = true;
			else
				verified = false;
		}
		catch(SQLException e){
			LOG.log(Level.WARNING, "" ,e);
		}
		
		return verified;
	}

}

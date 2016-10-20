package stockkeeper.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Position implements Serializable {
	public Position(String worldName_, int x_,int  y_,int  z_)
	{
		x = x_;
		y = y_;
		z = z_;
		worldName = worldName_;

	}
	public Position(int x_,int  y_,int  z_)
	{
		x = x_;
		y = y_;
		z = z_;
		worldName = null;

	}
	public int x, y, z;
	public String worldName;
	
	public double distanceTo(Position p) {
	    return 
	    		Math.sqrt(
	    		Math.pow(x - p.x, 2) 
	    		+ 
	    		Math.pow(y - p.y, 2)
	    		+ 
	    		Math.pow(z - p.z, 2)
	    		);
	}
	
	public String getId(String ip)
	{
		return ip +":"+ this.worldName + ":" + this.x + ":" + this.y + ":" + this.z;		
	}
	
	
	public static final Map<String, String> worldIdtoName = new HashMap<String, String>()
	 {{
		
		put("a72e4777-ad62-4e3b-a4e0-8cf2d15147ea", "Rokko Steppe");
		put("b25abb31-fd1e-499d-a5b5-510f9d2ec501", "Volans");
		put("a7cbf239-6c11-4146-a715-ef0a9827b4c4", "Drakontas");
		put("44f4b133-a646-461a-a14a-5fd8c8dbc59c", "Tjikko");
		put("a358b10c-7041-40c5-ac5e-db5483a9dfc2", "Eilon");
		put("182702a7-ea3f-41de-a2d3-c046842d5e74", "Abydos");
		put("7120b7a6-dd21-468c-8cd7-83d96f735589", "Padzahr");
		put( "197e2c4f-2fd6-464a-8754-53b24d9f7898", "Isolde");
		put("de730958-fa83-4e73-ab7f-bfdab8e27960", "Naunet");
		put( "63a68417-f07f-4cb5-a9d8-e5e702565967", "Tigrillo");
		put( "7f03aa4d-833c-4b0c-9d3b-a65a5c6eada0", "Ulca Felya");
		put( "fc891b9e-4b20-4c8d-8f97-7436383e8105", "Sheol");
		
	}};
	private static final Map<String, String> worldNameToId = new HashMap<String, String>()
	{{
		put("a72e4777-ad62-4e3b-a4e0-8cf2d15147ea", "Rokko Steppe");
		put("b25abb31-fd1e-499d-a5b5-510f9d2ec501", "Volans");
		put("a7cbf239-6c11-4146-a715-ef0a9827b4c4", "Drakontas");
		put("44f4b133-a646-461a-a14a-5fd8c8dbc59c", "Tjikko");
		put("a358b10c-7041-40c5-ac5e-db5483a9dfc2", "Eilon");
		put("182702a7-ea3f-41de-a2d3-c046842d5e74", "Abydos");
		put("7120b7a6-dd21-468c-8cd7-83d96f735589", "Padzahr");
		put( "197e2c4f-2fd6-464a-8754-53b24d9f7898", "Isolde");
		put("de730958-fa83-4e73-ab7f-bfdab8e27960", "Naunet");
		put( "63a68417-f07f-4cb5-a9d8-e5e702565967", "Tigrillo");
		put( "7f03aa4d-833c-4b0c-9d3b-a65a5c6eada0", "Ulca Felya");
		put( "fc891b9e-4b20-4c8d-8f97-7436383e8105", "Sheol");

	}};	    	

}



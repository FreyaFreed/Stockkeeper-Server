package stockkeeper.data;

import java.io.Serializable;
import java.util.UUID;

import javax.crypto.SecretKey;

public class UserCredentials implements Serializable {
	public SecretKey key;
	public String password;
	public UUID user;
	public UserCredentials(SecretKey key, String password, UUID user) {
		super();
		this.key = key;
		this.password = password;
		this.user = user;
	}
	
	

}

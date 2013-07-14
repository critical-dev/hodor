package ca.etsmtl.ca.log735.messages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ca.etsmtl.log735.server.Server;

/**
 * With this message, the client asks the server to create an account.
 * process returns true if it was successfully able to validate the provided info.
 * The user must not already exist.
 * Passwords must have a length of at least 5 characters. The password and the confirmation must match.
 * @author chrys
 *
 */
public class RegisterRequest extends ServerMessage {
	
	private String username, password, passwordConf;
	
	public RegisterRequest(String username, String password, String passwordConf) {
		this.username = username.toLowerCase();
		this.password = password;
		this.passwordConf = passwordConf;
	}

	@Override
	public boolean process(Server server) {
		if(!password.equals(passwordConf) || password.length() < 5){
			System.out.println("RegisterRequest : passwords not matching or not 5 characters at least. Aborting.");
			return false;
		}
		else if(server.getUsersWithPasswords().containsKey(username)){
			System.out.println("RegisterRequest : user " + username + " already exists. Aborting.");
			return false;
		}
		else{
			//add account to registered users database
			File usersDatabase = server.getAuthList();
			try {
				FileWriter fw = new FileWriter(usersDatabase);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(username+":"+password+"\n");
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			server.getUsersWithPasswords().put(username.toLowerCase(), password);
			return true;
		}
	}
	
	public String getUsername(){
		return username;
	}

}

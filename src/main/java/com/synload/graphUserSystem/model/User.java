package com.synload.graphUserSystem.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;

public class User {
	public static Label label = DynamicLabel.label( "User" );
	public String uid;
	public String username;
	public byte[] password;
	public String email;
	public int created;
	public int level;
	public String salt;
	public List<String> flags = new ArrayList<String>();
	public User(Node userNode){
		this.username = (String)userNode.getProperty("username");
		this.password = (byte[]) userNode.getProperty("password");
		this.salt = (String)userNode.getProperty("salt");
		this.email = (String)userNode.getProperty("email");
		this.created = (int)userNode.getProperty("created");
		this.level = (int) userNode.getProperty("level");
		this.flags = Arrays.asList((String[])userNode.getProperty("flags"));
	}
	public User(String username){
		if(User.exists(username)){
			User u = User.find(username);
			this.username = u.username;
			this.password = u.password;
			this.email = u.email;
			this.salt = u.salt;
			this.level = u.level;
			this.uid = u.uid;
			this.created = u.created;
			this.flags = new ArrayList<String>(u.flags);
		}else{
			Log.info("Error user id does not exist", Session.class);
		}
	}
	public User(String username, String password, String email, String salt, List<String> flags, int created, int level){
		MessageDigest md;
		String uid = SynloadFramework.randomString(20);
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update((salt+password+salt).getBytes());
		    byte[] passwordBytes = md.digest();
			if(!User.exists(username)){
				this.username = username;
				this.email = email;
				this.created = created;
				this.uid = uid;
				this.salt = salt;
				this.level = level;
				this.flags = new ArrayList<String>(flags);
				try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
					Node sessionNode = SynloadFramework.graphDB.createNode(label);
					sessionNode.setProperty("username", this.username);
					sessionNode.setProperty("password", passwordBytes);
					sessionNode.setProperty("salt", this.salt);
					sessionNode.setProperty("uid", uid);
					sessionNode.setProperty("level", level);
					sessionNode.setProperty("created", created);
					sessionNode.setProperty("email", this.email);
					sessionNode.setProperty("flags", (String[])flags.toArray());
					tx.success();
				}
			}else{
				Log.info("Error user id collision", Session.class);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public boolean passwordMatch(String password){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update((salt+password+salt).getBytes());
		    byte[] passwordBytes = md.digest();
			if(passwordBytes.equals(this.password)){
				return true;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Node getNode(){
		Node userNode = null;
		try ( ResourceIterator<Node> users = SynloadFramework.graphDB.findNodes( label, "uid", uid ) ){
			if(users.hasNext()){
				userNode = users.next();
			}
	    }
		return userNode;
	}
	
	public static boolean exists(String username){
		boolean exist = false;
		try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
			Node userNode = null;
			try ( ResourceIterator<Node> users = SynloadFramework.graphDB.findNodes( label, "username", username ) ){
				if(users.hasNext()){
					exist = true;
				}
		    }
			tx.success();
		}
		return exist;
	}
	public static User find(String username){
		try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
			Node userNode = null;
			try ( ResourceIterator<Node> users = SynloadFramework.graphDB.findNodes( label, "username", username ) ){
				if(users.hasNext()){
					userNode = users.next();
				}
		    }
			User u = new User(userNode);
			tx.success();
			return u;
		}
	}
	public static Label getLabel() {
		return label;
	}
	public String getUid() {
		return uid;
	}
	public String getUsername() {
		return username;
	}
	public byte[] getPassword() {
		return password;
	}
	public String getEmail() {
		return email;
	}
	public String getSalt() {
		return salt;
	}
	public List<String> getFlags() {
		return flags;
	}
}

package com.synload.graphUserSystem.model;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.graphUserSystem.relationships.SessionRel;

public class Session {
	public static Label label = DynamicLabel.label( "Session" );
	public String ip;
	public String sid;
	public User u = null;
	public String uid;
	public Session(Node sessionNode){
		sid = (String)sessionNode.getProperty("sid");
		ip = (String)sessionNode.getProperty("ip");
		uid = (String)sessionNode.getProperty("uid");
		try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
			for(Relationship r: sessionNode.getRelationships(SessionRel.LOGGEDIN)){
				if(r.getNodes().length>0){
					this.u = new User(r.getNodes()[0]);
					break;
				}
			}
        	tx.success();
        }
	}
	public Session(String sid){
		if(Session.exists(sid)){
			Session s = Session.find(sid);
			this.sid = s.sid;
			this.ip = s.ip;
			try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
				for(Relationship r: s.getNode().getRelationships(SessionRel.LOGGEDIN)){
					if(r.getNodes().length>0){
						this.u = new User(r.getNodes()[0]);
						break;
					}
				}
	        	tx.success();
	        }
		}else{
			Log.info("Error session id collision", Session.class);
		}
	}
	public Session(String sid, String ip){
		if(Session.exists(sid)){
			Session s = Session.find(sid);
			if(s.getIp().equals(ip)){
				this.sid = s.sid;
				this.ip = s.ip;
				try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
					for(Relationship r: s.getNode().getRelationships(SessionRel.LOGGEDIN)){
						if(r.getNodes().length>0){
							this.u = new User(r.getNodes()[0]);
							break;
						}
					}
		        	tx.success();
		        }
			}
		}else{
			Log.info("Error session id collision", Session.class);
		}
	}
	public Session(User u, String ip){
		String sid = SynloadFramework.randomString(20);
		if(!Session.exists(sid)){
			this.sid = sid;
			this.ip = ip;
			this.uid = u.getUid();
			try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
				Node sessionNode = SynloadFramework.graphDB.createNode(label);
				sessionNode.setProperty("sid", sid);
				sessionNode.setProperty("uid", this.uid);
				sessionNode.setProperty("ip", this.ip);
				tx.success();
			}
		}else{
			Log.info("Error session id collision", Session.class);
		}
	}
	public Node getNode(){
		Node sessionNode = null;
		try ( ResourceIterator<Node> sessions = SynloadFramework.graphDB.findNodes( label, "sid", sid ) ){
			if(sessions.hasNext()){
				sessionNode = sessions.next();
			}
	    }
		return sessionNode;
	}
	public static boolean exists(String sid){
		boolean exist = false;
		try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
			Node sessionNode = null;
			try ( ResourceIterator<Node> sessions = SynloadFramework.graphDB.findNodes( label, "sid", sid ) ){
				if(sessions.hasNext()){
					exist = true;
				}
		    }
			tx.success();
		}
		return exist;
	}
	public static Session find(String sid){
		try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
			Node sessionNode = null;
			try ( ResourceIterator<Node> sessions = SynloadFramework.graphDB.findNodes( label, "sid", sid ) ){
				if(sessions.hasNext()){
					sessionNode = sessions.next();
				}
		    }
			Session s = new Session(sessionNode);
			tx.success();
			return s;
		}
	}
	public static Label getLabel() {
		return label;
	}
	public String getIp() {
		return ip;
	}
	public String getSid() {
		return sid;
	}
	public String getUid() {
		return uid;
	}
	public User getUser() {
		return u;
	}
}

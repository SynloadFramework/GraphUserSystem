package com.synload.graphUserSystem;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.neo4j.graphdb.Transaction;

import com.synload.framework.SynloadFramework;
import com.synload.framework.ws.WSHandler;
import com.synload.graphUserSystem.model.Session;
import com.synload.graphUserSystem.model.User;
import com.synload.graphUserSystem.relationships.SessionRel;

public class Authentication {
    public static User login(WSHandler session, String username, String password, String ip) {
        User u = null;
        try {
			if ((u = User.find(username)) != null) {
			    if (u.passwordMatch(password)) {
			        session.flags = u.getFlags();
			        Session s = new Session(u, ip);
			        try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
			        	s.getNode().createRelationshipTo(u.getNode(), SessionRel.LOGGEDIN);
			        	tx.success();
			        }
			        return u;
			    }
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
        return null;
    }
    public static User login(String username, String password, String ip) {
        User u = null;
        try {
			if ((u = User.find(username)) != null) {
			    if (u.passwordMatch(password)) {
			    	Session s = new Session(u, ip);
			    	try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
			        	s.getNode().createRelationshipTo(u.getNode(), SessionRel.LOGGEDIN);
			        	tx.success();
			        }
			        return u;
			    }
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    public static User create(String username, String password, String email, List<String> flags, int level) {
        boolean validEmail = false;
        try {
            Pattern regex = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher regexMatcher = regex.matcher(email);
            validEmail = regexMatcher.matches();
        } catch (PatternSyntaxException ex) {
        }
        if (!User.exists(username) && username.length() > 3
                && password.length() > 3 && validEmail) {
            int longTime = (int) (System.currentTimeMillis() / 1000L);
            User u = new User(username, password, email, SynloadFramework.randomString(10), flags, longTime, level);
            return u;
        } else {
            return null;
        }
    }

    public static User session(WSHandler session, String sid, String ip) {
    	Session s = new Session(sid);
        if (s.getUser() != null && s.getIp().equals(ip)) {
            session.flags = new ArrayList<String>(s.getUser().getFlags());
            return s.getUser();
        }
        return null;
    }
    public static User session(String sid, String ip) {
        Session s = new Session(sid);
        if (s.getUser() != null && s.getIp().equals(ip)) {
            return s.getUser();
        }
        return null;
    }
    public static User session(String sid) {
        Session s = new Session(sid);
        if (s.getUser() != null) {
            return s.getUser();
        }
        return null;
    }
}
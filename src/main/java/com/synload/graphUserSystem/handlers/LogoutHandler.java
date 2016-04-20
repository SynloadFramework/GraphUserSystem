package com.synload.graphUserSystem.handlers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Failed;
import com.synload.framework.elements.Success;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.graphUserSystem.model.Session;
import com.synload.graphUserSystem.model.User;

public class LogoutHandler {
	@Event(name = "Logout", description = "handle a user logout request", trigger = { "get", "logout" }, type = Type.WEBSOCKET)
    public void getLogout(RequestEvent event) throws JsonProcessingException,
            IOException {
        if (event.getSession().getSessionData().containsKey("graphUser")) {
        	try ( Transaction tx = SynloadFramework.graphDB.beginTx() ){
				Session s = new Session(
					event.getRequest().getData().get("sessionid"),
	                String.valueOf(event.getSession().session.getUpgradeRequest().getHeader("X-Real-IP"))
	            );
				for(Relationship r: s.getNode().getRelationships()){
					r.delete();
				}
				s.getNode().delete();
				tx.success();
        	}
            event.getSession().getSessionData().remove("graphUser");
            event.getSession().send(SynloadFramework.ow.writeValueAsString(new Success("logout")));
        } else {
            event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("logout")));
        }
    }
}

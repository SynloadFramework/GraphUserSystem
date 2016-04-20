package com.synload.graphUserSystem.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Failed;
import com.synload.framework.elements.RegisterBox;
import com.synload.framework.elements.Success;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.graphUserSystem.Authentication;
import com.synload.graphUserSystem.model.User;

public class RegisterHandler {
	@Event(name = "User Register", description = "handle user registrations", trigger = { "get", "register" }, type = Type.WEBSOCKET)
    public void getRegisterBox(RequestEvent event)
            throws JsonProcessingException, IOException {
        event.getSession().send(
                SynloadFramework.ow.writeValueAsString(new RegisterBox(event
                        .getRequest().getTemplateCache())));
    }
    @Event(name = "Register", description = "", trigger = { "action", "register" }, type = Type.WEBSOCKET)
    public void getRegister(RequestEvent event) throws JsonProcessingException,
            IOException {
        List<String> flags = new ArrayList<String>();
        flags.add("r");
        User authedUser = Authentication.create(
    		event.getRequest().getData().get("username").toLowerCase(),
    		event.getRequest().getData().get("password"),
    		event.getRequest().getData().get("email"),
            flags,
            0
        );
        if (authedUser!=null) {
            event.getSession().send(SynloadFramework.ow.writeValueAsString(new Success("register")));
        } else {
            event.getSession().send(SynloadFramework.ow.writeValueAsString(new Failed("register")));
        }
    }
}

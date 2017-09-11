package com.example.myapp.websockets;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Hello-world websockets endpoint
 *
 */
@ApplicationScoped
@ServerEndpoint("/wsocks")
public class WebsocketsEndpoint {

	@OnMessage
	public void processGreeting(String message, Session session) {
		System.out.println("Greeting received:" + message);
	}

}

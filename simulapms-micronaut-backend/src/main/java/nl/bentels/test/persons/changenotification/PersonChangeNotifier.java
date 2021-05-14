package nl.bentels.test.persons.changenotification;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.http.MediaType;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.netty.handler.codec.http.websocketx.WebSocketScheme;

@Singleton
@ServerWebSocket("/topics/person")
public class PersonChangeNotifier {

	private static final Logger                       LOGGER           = LoggerFactory.getLogger(PersonChangeNotifier.class);

    private static final String                       MESSAGE_TEMPLATE = """
            { "changeType" : "%s",
              "resource" : "%s"
              }
            """;

    private static final Collection<WebSocketSession> SESSIONS         = Collections.synchronizedCollection(new HashSet<>());

    public static enum ChangeType {
        ADDED, UPDATED, REMOVED
    }
    
    private final WebSocketBroadcaster broadcaster;

	public PersonChangeNotifier(WebSocketBroadcaster broadcaster) {
		this.broadcaster = broadcaster;
	}
    
	@OnOpen
    public void onOpen(WebSocketSession session) {
    	SESSIONS.add(session);
    }
	
	@OnClose
	public void onClose(WebSocketSession session) {
		SESSIONS.remove(session);
	}
	
	@OnMessage
	public void onMessage(String message, WebSocketSession session) {
		LOGGER.info(message);
	}
	
	public void enqueueNotification(final ChangeType changeType, final URI resource) {
        String message = prepareMessage(changeType, resource);
        broadcast(message);
    }
	
	 private void broadcast(final String message) {
	        Set<WebSocketSession> deadSessions = new HashSet<>();
	        SESSIONS.parallelStream().forEach(sess -> {
	            if (sess.isOpen()) {
					sess.sendSync(message, MediaType.APPLICATION_JSON_TYPE);
				} else {
				    deadSessions.add(sess);
				}
	        });
	        deadSessions.forEach(this::onClose);
	    }
	
	private String prepareMessage(final ChangeType changeType, final URI resource) {
        return String.format(MESSAGE_TEMPLATE, changeType.name(), resource.toString());
    }
}

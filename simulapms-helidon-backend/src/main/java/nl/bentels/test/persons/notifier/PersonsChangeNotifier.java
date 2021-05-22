package nl.bentels.test.persons.notifier;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ApplicationScoped
@ServerEndpoint("/topics/person")
public class PersonsChangeNotifier {
	
	private final static Logger LOGGER = Logger.getLogger(PersonsChangeNotifier.class.getCanonicalName());
	
	private static final String                       MESSAGE_TEMPLATE = """
            { "changeType" : "%s",
              "resource" : "%s"
              }
            """;

    private static final Collection<Session> SESSIONS         = Collections.synchronizedCollection(new HashSet<>());

    public static enum ChangeType {
        ADDED, UPDATED, REMOVED
    }
    
	@OnOpen
    public void onOpen(Session session, EndpointConfig config) {
    	SESSIONS.add(session);
    }
	
	@OnClose
	public void onClose(Session session, CloseReason reason) {
		SESSIONS.remove(session);
	}
	
	@OnMessage
	public void onMessage(Session session, String message) {
		LOGGER.info(message);
	}
	
	public void enqueueNotification(final ChangeType changeType, final URI resource) {
        String message = prepareMessage(changeType, resource);
        broadcast(message);
    }
	
	 private void broadcast(final String message) {
	        Set<Session> deadSessions = new HashSet<>();
	        SESSIONS.parallelStream().forEach(sess -> {
	            if (sess.isOpen()) {
					sess.getAsyncRemote().sendText(message);
				} else {
				    deadSessions.add(sess);
				}
	        });
	        deadSessions.forEach(sess -> onClose(sess, new CloseReason(CloseCodes.CLOSED_ABNORMALLY, "Dead session")));
	    }
	
	private String prepareMessage(final ChangeType changeType, final URI resource) {
        return String.format(MESSAGE_TEMPLATE, changeType.name(), resource.toString());
    }
	
}

package nl.bentels.loa.simulapms.frontend.persons;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator.OverflowStrategy;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@CrossOrigin("*")
public class PersonWebSocketTopic extends TextWebSocketHandler {

    private static final Logger                       LOGGER           = LoggerFactory.getLogger(PersonWebSocketTopic.class);

    private static final String                       MESSAGE_TEMPLATE = """
            { "changeType" : "%s",
              "resource" : "%s"
              }
            """;

    private static final Collection<WebSocketSession> SESSIONS         = Collections.synchronizedCollection(new HashSet<>());

    public static enum ChangeType {
        ADDED, UPDATED, REMOVED
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        SESSIONS.add(new ConcurrentWebSocketSessionDecorator(session, 5000, 128 * 1024, OverflowStrategy.DROP));
    }

    public static void enqueueNotification(final ChangeType changeType, final URI resource) {
        TextMessage message = prepareMessage(changeType, resource);
        broadcast(message);
    }

    private static void broadcast(final TextMessage message) {
        Set<WebSocketSession> deadSessions = new HashSet<>();
        SESSIONS.parallelStream().forEach(sess -> {
            try {
                if (sess.isOpen()) {
                    sess.sendMessage(message);
                } else {
                    deadSessions.add(sess);
                }
            } catch (IOException e) {
                LOGGER.error("Closing session due to IOException:", e);
                deadSessions.add(sess);
            }
        });
        deadSessions.forEach(PersonWebSocketTopic::closeSession);
    }

    private static TextMessage prepareMessage(final ChangeType changeType, final URI resource) {
        String messageBody = String.format(MESSAGE_TEMPLATE, changeType.name(), resource.toString());
        TextMessage message = new TextMessage(messageBody);
        return message;
    }

    private static void closeSession(final WebSocketSession sess) {
        if (sess.isOpen()) {
            try {
                sess.close();
            } catch (IOException e) {
                LOGGER.error("Closing session failed", e);
            }
        }
        SESSIONS.remove(sess);
    }

    @Override
    public String toString() {
        return "Person Web Socket Topic";
    }

}

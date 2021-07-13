package nl.bentels.loa.simulapms.person;

import java.net.URI;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

public class PersonChangeNotificationHandler implements WebSocketHandler, CorsConfigurationSource {

	private static final String                       MESSAGE_TEMPLATE = """
            { "changeType" : "%s",
              "resource" : "%s"
              }
            """;

    public static enum ChangeType {
        ADDED, UPDATED, REMOVED
    }
	
    public static String makeMessage(ChangeType changeType, URI resource) {
    	return MESSAGE_TEMPLATE.formatted(changeType.name(), resource.toString());
    }
    
    private final Sinks.Many<String> notificationSink;
    
	public PersonChangeNotificationHandler(Many<String> notificationSink) {
		this.notificationSink = notificationSink;
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		return session.send(notificationSink.asFlux()
				.flatMap(msg -> Mono.just(msg))
				.map(session::textMessage));
	}

	@Override
	public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
		CorsConfiguration cconf = new CorsConfiguration();
		cconf.addAllowedOrigin("*");
		return cconf;
	}

}

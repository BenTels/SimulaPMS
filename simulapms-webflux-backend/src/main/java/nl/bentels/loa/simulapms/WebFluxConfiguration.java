package nl.bentels.loa.simulapms;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.MongoConverterConfigurationAdapter;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import nl.bentels.loa.simulapms.person.PersonChangeNotificationHandler;
import nl.bentels.loa.simulapms.repository.LocalDateToStringConverter;
import nl.bentels.loa.simulapms.repository.StringToLocalDateConverter;
import reactor.core.publisher.Sinks;

@Configuration
@EnableWebFlux
@EnableReactiveMongoRepositories
public class WebFluxConfiguration extends AbstractReactiveMongoConfiguration implements WebFluxConfigurer {

	@Value("${spring.data.mongodb.uri}")
	public String mongoServerConnectionString; 
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/persons/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE")
				.allowedHeaders("Content-Type", "Location");
	}

	@Override
	public void configureConverters(MongoConverterConfigurationAdapter converterConfigurationAdapter) {
		converterConfigurationAdapter
				.registerConverters(Set.of(new LocalDateToStringConverter(), new StringToLocalDateConverter()));
	}

	@Override
	protected String getDatabaseName() {
		return "test";
	}
	
	@Override
    public MongoClient reactiveMongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoServerConnectionString);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();
        
        return MongoClients.create(mongoClientSettings);
    }
	
	@Bean
	public Sinks.Many<String> notificationSink() {
		return Sinks.many().multicast().onBackpressureBuffer();
	}
	
	@Bean
    public HandlerMapping handlerMapping(@Autowired Sinks.Many<String> notificationSink) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/topics/person", new PersonChangeNotificationHandler(notificationSink));
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }

}

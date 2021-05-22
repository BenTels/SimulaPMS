package nl.bentels.test.persons.repository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@ApplicationScoped
public class MongoDbClientFactory {

	private final String dbUri;
	
	@Inject
	public MongoDbClientFactory(@ConfigProperty(name = "db.url") final String dbUri) {
		this.dbUri = dbUri;
	}
	
	@Produces
	public MongoClient createMongoClient() {
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		
		MongoClientSettings settings = MongoClientSettings
				.builder()
				.codecRegistry(pojoCodecRegistry)
				.applyConnectionString(new ConnectionString(dbUri))
				.build();
		
		return MongoClients.create(settings);
	}
}

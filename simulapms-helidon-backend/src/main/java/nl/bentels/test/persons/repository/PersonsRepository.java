package nl.bentels.test.persons.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bson.BsonString;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import nl.bentels.test.persons.domain.Person;

@ApplicationScoped
public class PersonsRepository {
	
	private final MongoClient mongoClient;

	@Inject
	public PersonsRepository(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
	
	public List<Person> retrieveAllPersons() {
		List<Person> target = new ArrayList<>();
		getCollection().find().into(target);
		return target;
	}
	
	public Optional<Person> findById(String id) {
		return Optional.ofNullable(getCollection().find(new BasicDBObject("_id", new BsonString(id))).first());
	}
	
	public Optional<String> createNewPerson(Person person) {
		String key = UUID.randomUUID().toString();
		person.id = key;
		InsertOneResult insertOne = getCollection().insertOne(person);
		return Optional.ofNullable(insertOne.getInsertedId().asString().getValue().toString());
	}
	
	public void updatePerson(Person person) throws UnknownPersonException, PersonUpdateFailedException {
		UpdateResult result = getCollection()
		.replaceOne(new BasicDBObject("_id", new BsonString(person.id)), 
				person);
		if (result.getMatchedCount() != 1L) {
			throw new UnknownPersonException();
		}
		if (result.getModifiedCount() != 1L) {
			throw new PersonUpdateFailedException();
		}
	}
	
	public void removePerson(String id) {
		getCollection().deleteOne(new BasicDBObject("_id", new BsonString(id)));
	}
	
	private MongoCollection<Person> getCollection() {
		return mongoClient.getDatabase("test").getCollection("person", Person.class);
	}

}

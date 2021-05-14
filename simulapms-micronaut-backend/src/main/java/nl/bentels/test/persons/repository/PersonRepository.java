package nl.bentels.test.persons.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Singleton;

import org.bson.BsonString;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import nl.bentels.test.persons.changenotification.CausesClientNotification;
import nl.bentels.test.persons.pojodomain.Person;

@Singleton
public class PersonRepository {

	private MongoClient mongoClient;

	public PersonRepository(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

	public List<Person> findAll() {
		return getCollection().find().into(new ArrayList<>());
	}
	
	public Optional<Person> findById(String id) {
		return Optional.ofNullable(getCollection().find(new BasicDBObject("_id", new BsonString(id))).first());
	}
	
	public Optional<String> createNewPerson(Person person) {
		String key = UUID.randomUUID().toString();
		person.setId(key);
		InsertOneResult insertOne = getCollection().insertOne(person);
		return Optional.ofNullable(insertOne.getInsertedId().asString().getValue().toString());
	}
	
	public void updatePerson(Person person) throws UnknownPersonException, PersonUpdateFailedException {
		UpdateResult result = getCollection()
		.replaceOne(new BasicDBObject("_id", new BsonString(person.getId())), 
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
		return mongoClient
				.getDatabase("test")
				.getCollection("person", Person.class);
	}
	
}

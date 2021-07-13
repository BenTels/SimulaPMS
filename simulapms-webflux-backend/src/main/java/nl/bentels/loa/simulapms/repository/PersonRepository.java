package nl.bentels.loa.simulapms.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import nl.bentels.loa.simulapms.model.Person;

public interface PersonRepository extends ReactiveMongoRepository<Person, String> {

}

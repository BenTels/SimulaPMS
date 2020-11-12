package nl.bentels.loa.simulapms.model.person;

import java.util.List;

public interface PersonRepository {

    List<Person> findAll();

    Person findById(String id) throws NoSuchPersonException;

    void create(Person newPerson);

    void update(Person person);

    void remove(Person person);
}

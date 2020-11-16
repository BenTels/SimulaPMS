package nl.bentels.loa.simulapms.model.person;

import java.util.List;

public interface PersonRepository {

    List<Person> findAll();

    Person findById(String id) throws NoSuchPersonException;

    void create(Person newPerson);

    void update(Person person);

    void update(Person person, String field, Object newValue);

    void remove(Person person);
}

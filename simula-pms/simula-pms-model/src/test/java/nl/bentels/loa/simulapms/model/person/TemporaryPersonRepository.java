package nl.bentels.loa.simulapms.model.person;

import java.util.List;

public class TemporaryPersonRepository implements PersonRepository {

    @Override
    public Person findById(final String id) {
        return Person.builder().id(id).lastName(id).build();
    }

    @Override
    public List<Person> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void create(final Person newPerson) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void update(final Person person) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void remove(final Person person) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void update(final Person person, final String field, final Object newValue) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

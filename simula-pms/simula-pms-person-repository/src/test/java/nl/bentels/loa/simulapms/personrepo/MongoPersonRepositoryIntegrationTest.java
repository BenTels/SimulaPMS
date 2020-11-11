package nl.bentels.loa.simulapms.personrepo;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import nl.bentels.loa.simulapms.model.person.NoSuchPersonException;
import nl.bentels.loa.simulapms.model.person.Person;

class MongoPersonRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    @Autowired
    private MongoPersonRepository objectUnderTest;

    @Test
    void whenPersonsRetrieved_thenOkay() throws NoSuchPersonException {
        Person tels = objectUnderTest.findById("5f5f6c1c5fc0d0741b9c7732");
        assertTels(tels);
    }

    @Test
    void whenAllPersonsRetrieved_thenOkay() throws NoSuchPersonException {
        List<Person> findAll = objectUnderTest.findAll();
        assertFalse(findAll.isEmpty());
        findAll.forEach(System.out::println);
    }

}

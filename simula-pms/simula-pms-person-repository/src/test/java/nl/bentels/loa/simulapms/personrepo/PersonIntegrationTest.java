package nl.bentels.loa.simulapms.personrepo;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import nl.bentels.loa.simulapms.model.person.Person;

public class PersonIntegrationTest extends AbstractRepositoryIntegrationTest {

    @Test
    public void whenExistingPersonRetrieved_thenOkay() {
        Person tels = Person.findById("5f5f6c1c5fc0d0741b9c7732");
        assertTels(tels);
    }

    @Test
    public void whenAllPersonsRetrieved_thenOkay() {
        List<Person> findAllOfThem = Person.findAllOfThem();
        assertFalse(findAllOfThem.isEmpty());
        findAllOfThem.forEach(System.out::println);
    }

}

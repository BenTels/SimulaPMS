package nl.bentels.loa.simulapms.personrepo;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.bentels.loa.simulapms.model.person.Address;
import nl.bentels.loa.simulapms.model.person.AlreadyIdentifiedPersonException;
import nl.bentels.loa.simulapms.model.person.NoSuchPersonException;
import nl.bentels.loa.simulapms.model.person.Person;
import nl.bentels.loa.simulapms.model.person.PersonRepository;
import nl.bentels.loa.simulapms.model.person.PhoneNumber;
import nl.bentels.loa.simulapms.personrepo.model.PersonDocument;

@Component
public class MongoPersonRepository implements PersonRepository {

    @Autowired
    private MongoPersonDocumentRepository personDocumentRepo;
    @Autowired
    private MongoFieldUpdateRepository    fieldUpdateRepo;
    @Autowired
    private PersonRepositoryMapper        mapper;

    @Override
    public Person findById(final String id) throws NoSuchPersonException {
        PersonDocument personDoc = personDocumentRepo.findById(id).orElseThrow(() -> new NoSuchPersonException(id));
        return mapper.fromRepoPerson(personDoc);
    }

    @Override
    public List<Person> findAll() {
        List<PersonDocument> findAll = personDocumentRepo.findAll();
        return findAll.stream()
                .filter(pd -> StringUtils.isNotBlank(pd.getLastName()))
                .map(pd -> mapper.fromRepoPerson(pd))
                .collect(Collectors.toList());
    }

    @Override
    public void create(final Person newPerson) {
        if (!personDocumentRepo.existsById(newPerson.getId())) {
            personDocumentRepo.save(mapper.toRepoPerson(newPerson));
        } else {
            throw new AlreadyIdentifiedPersonException();
        }
    }

    @Override
    public void update(final Person person) {
        if (personDocumentRepo.existsById(person.getId())) {
            personDocumentRepo.save(mapper.toRepoPerson(person));
        } else {
            throw new NoSuchPersonException(person.getId());
        }
    }

    @Override
    public void remove(final Person person) {
        personDocumentRepo.delete(mapper.toRepoPerson(person));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(final Person person, final String field, final Object newValue) {
        switch (field) {
        case "lastName" -> fieldUpdateRepo.updateFieldValue(person, field, (String) newValue);
        case "dateOfBirth" -> fieldUpdateRepo.updateFieldValue(person, field, ((LocalDate) newValue).toString());
        case "firstNames", "middleNames", "emailAddresses" -> fieldUpdateRepo.updateFieldValue(person, field, (String[]) newValue);
        case "correspondenceAddress" -> fieldUpdateRepo.updateFieldValue(person, "mainCorrespondenceAddress", mapper.toRepoAddress((Address) newValue));
        case "billingAddress" -> fieldUpdateRepo.updateFieldValue(person, field, mapper.toRepoAddress((Address) newValue));
        case "phoneNumbers" -> fieldUpdateRepo.updatePhoneNumbersFieldValue(person, field,
                Arrays.stream((PhoneNumber[]) newValue).map(phn -> mapper.toRepoPhoneNumber(phn)).collect(Collectors.toList()));
        }
    }

}

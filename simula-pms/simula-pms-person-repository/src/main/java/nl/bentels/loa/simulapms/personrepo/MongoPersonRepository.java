package nl.bentels.loa.simulapms.personrepo;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nl.bentels.loa.simulapms.model.person.NoSuchPersonException;
import nl.bentels.loa.simulapms.model.person.Person;
import nl.bentels.loa.simulapms.model.person.PersonRepository;
import nl.bentels.loa.simulapms.personrepo.model.PersonDocument;

@Component
public class MongoPersonRepository implements PersonRepository {

    @Autowired
    private MongoPersonDocumentRepository personDocumentRepo;
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

}

package nl.bentels.loa.simulapms.personrepo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import nl.bentels.loa.simulapms.model.person.Person;
import nl.bentels.loa.simulapms.personrepo.model.Address;
import nl.bentels.loa.simulapms.personrepo.model.PersonDocument;
import nl.bentels.loa.simulapms.personrepo.model.PhoneNumber;

@Component
public class MongoFieldUpdateRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoFieldUpdateRepository.class);

    @Autowired
    private MongoTemplate       template;

    public void updateFieldValue(final Person p, final String fieldName, final String newValue) {
        long count = template.update(PersonDocument.class).matching(query(where("id").is(p.getId()))).apply(update(fieldName, newValue)).first().getModifiedCount();
        if (count != 1) {
            throw new IllegalStateException();
        }
    }

    public void updateFieldValue(final Person p, final String fieldName, final String[] newValue) {
        long count = template.update(PersonDocument.class).matching(query(where("id").is(p.getId()))).apply(update(fieldName, newValue)).first().getModifiedCount();
        if (count != 1) {
            throw new IllegalStateException();
        }
    }

    public void updatePhoneNumbersFieldValue(final Person p, final String fieldName, final List<PhoneNumber> newValue) {

        List<Map<String, ?>> collect = newValue.stream()
                .map(phn -> Map.of("phoneNumber", phn.getPhoneNumber(), "mobile", phn.isMobile()))
                .collect(Collectors.toList());
        long count = template.update(PersonDocument.class)
                .matching(query(where("id").is(p.getId())))
                .apply(new Update().addToSet("phoneNumbers").each(collect.toArray()))
                .first()
                .getModifiedCount();
        if (count != 1) {
            throw new IllegalStateException();
        }
    }

    public void updateFieldValue(final Person person, final String field, final Address repoAddress) {
        if ((repoAddress.getAddressLines() == null || repoAddress.getAddressLines().isEmpty()) && repoAddress.getCountryCode() == null) {
            throw new IllegalArgumentException("Incomplete address");
        }

        Map<String, Object> addressDocument = new HashMap<>();

        if (repoAddress.getAddressLines() != null && !repoAddress.getAddressLines().isEmpty()) {
            addressDocument.put("addressLines", repoAddress.getAddressLines());
        }
        if (repoAddress.getCountryCode() != null) {
            addressDocument.put("countryCode", repoAddress.getCountryCode().name());
        }

        long count = template.update(PersonDocument.class).matching(query(where("id").is(person.getId()))).apply(update(field, addressDocument)).first()
                .getModifiedCount();
        if (count != 1) {
            throw new IllegalStateException();
        }
    }
}

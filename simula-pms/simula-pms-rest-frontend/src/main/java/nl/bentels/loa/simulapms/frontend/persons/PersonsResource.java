package nl.bentels.loa.simulapms.frontend.persons;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neovisionaries.i18n.CountryCode;

import nl.bentels.loa.simulapms.frontend.persons.PersonWebSocketTopic.ChangeType;
import nl.bentels.loa.simulapms.model.person.Address;
import nl.bentels.loa.simulapms.model.person.AlreadyIdentifiedPersonException;
import nl.bentels.loa.simulapms.model.person.NoSuchPersonException;
import nl.bentels.loa.simulapms.model.person.Person;
import nl.bentels.loa.simulapms.model.person.PhoneNumber;

@RestController
@CrossOrigin(origins = "http://localhost.localdomain:3000", exposedHeaders = "Location")
public class PersonsResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonsResource.class);

    public static class PersonDTO {
        @JsonIgnore
        private Person              person;
        @JsonAnySetter
        private Map<String, Object> map = new HashMap<>();

        private static PersonDTO of(final Person p) {
            PersonDTO dto = new PersonDTO();
            dto.person = p;
            return dto;
        }

        @JsonAnyGetter
        public Map<String, Object> getProperties() {
            Map<String, Object> properties = new HashMap<>();
            properties.put("id", person.getId());
            properties.put("ageclass", person.getAgeClassNow().name());
            properties.put("lastname", person.getLastName());
            if (person.getFirstNames() != null) {
                properties.put("firstnames", person.getFirstNames());
            }
            if (person.getMiddleNames() != null) {
                properties.put("middlenames", person.getMiddleNames());
            }
            if (person.getEmailAddresses() != null) {
                properties.put("emailaddresses", person.getEmailAddresses());
            }
            if (person.getDateOfBirth() != null) {
                properties.put("dob", person.getDateOfBirth().toString());
            }
            if (person.getPhoneNumbers() != null) {
                properties.put("phonenumbers",
                        person.getPhoneNumbers().stream()
                                .map(pn -> Map.of("number", pn.getPhoneNumber(), "mobile", pn.isMobile()))
                                .collect(Collectors.toList()));
            }
            if (person.getCorrespondenceAddress() != null) {
                properties.put("mainCorrespondenceAddress", toJSONObject(person.getCorrespondenceAddress()));
            }
            if (person.getBillingAddress() != null) {
                properties.put("billingAddress", toJSONObject(person.getBillingAddress()));
            }

            return properties;
        }

        Person getPerson() {
            return person != null ? person : mapToPerson();
        }

        private Person mapToPerson() {
            return Person.builder()
                    .id((String) map.get("id"))
                    .lastName((String) map.get("lastname"))
                    .firstNames(emptyListObjectToNull("firstnames"))
                    .middleNames(emptyListObjectToNull("middlenames"))
                    .emailAddresses(emptyListObjectToNull("emailaddresses"))
                    .dateOfBirth(toLocalDate((String) map.get("dob")))
                    .phoneNumbers(phoneNumbersToList())
                    .correspondenceAddress(toAddress((Map<String, Object>) map.get("mainCorrespondenceAddress")))
                    .billingAddress(toAddress((Map<String, Object>) map.get("billingAddress")))
                    .build();

        }

        private List<PhoneNumber> phoneNumbersToList() {
            Object list0 = map.getOrDefault("phonenumbers", List.of());
            if (list0 != null) {
                List<PhoneNumber> list = ((List<Map<String, Object>>) list0).stream()
                        .filter(pnm -> StringUtils.isNotBlank((String) pnm.get("number")))
                        .map(pnm -> new PhoneNumber((String) pnm.get("number"), (Boolean) pnm.get("mobile")))
                        .collect(Collectors.toList());
                return list.isEmpty() ? null : list;
            }
            return null;
        }

        private List<String> emptyListObjectToNull(final String objectKey) {
            List<String> potentialList = (List<String>) map.get(objectKey);
            potentialList = emptyListToNull(potentialList);
            return potentialList;
        }

        private List<String> emptyListToNull(final List<String> potentialList) {
            List<String> resultList = potentialList;
            if (potentialList != null) {
                if (potentialList.isEmpty() || (potentialList.stream().filter(StringUtils::isNotBlank).count() == 0)) {
                    resultList = null;
                }
            }
            return resultList;
        }

        private Address toAddress(final Map<String, Object> addressMap) {
            if (addressMap == null) {
                return null;
            } else {
                CountryCode countryCode = CountryCode.valueOf((String) addressMap.get("country"));
                List<String> addressLines = emptyListToNull((List<String>) addressMap.get("lines"));
                return countryCode != null && addressLines != null ? Address.builder()
                        .countryCode(countryCode)
                        .addressLines(addressLines)
                        .build() : null;
            }
        }

        private LocalDate toLocalDate(final String dateString) {
            if (StringUtils.isNotBlank(dateString)) {
                return LocalDate.parse(dateString);
            }
            return null;
        }

        private Map<String, Object> toJSONObject(final Address address) {
            Map<String, Object> addressMap = Map.of("country", address.getCountryCode().name());
            if (address.getAddressLines() != null && !address.getAddressLines().isEmpty()) {
                addressMap = new HashMap<>(addressMap);
                addressMap.put("lines", address.getAddressLines());
            }
            return addressMap;
        }
    }

    @GetMapping(path = "/persons", produces = { "application/json" })
    public ResponseEntity<List<PersonDTO>> getPersons(@RequestParam(name = "searchTerm", defaultValue = "", required = false) final String filter) {
        LOGGER.debug("Going to retrieve persons with filter [{}]", filter);
        List<PersonDTO> filteredList = Person.findAllOfThem().stream()
                .filter(p -> StringUtils.isBlank(filter) || p.getLastName().toLowerCase().contains(filter.trim().toLowerCase()))
                .map(PersonDTO::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(filteredList);
    }

    @GetMapping(path = "/persons/{id}", produces = { "application/json" })
    public ResponseEntity<PersonDTO> getPerson(@PathVariable(name = "id", required = true) final String id) {
        LOGGER.debug("Going to retrieve person with id [{}]", id);
        Person person = Person.findById(id);
        if (person != null) {
            return ResponseEntity.ok(PersonDTO.of(person));
        }
        throw new NoSuchPersonException(id);
    }

    @PostMapping(path = "/persons", consumes = { "application/json" })
    public ResponseEntity<Void> createPerson(@RequestBody(required = true) final PersonDTO personDTO, final UriComponentsBuilder componentsBuilder) {
        Person person = personDTO.getPerson();

        if (StringUtils.isNotBlank(person.getId())) {
            throw new AlreadyIdentifiedPersonException();
        } else {
            Person newPerson = Person.fromTemplate(person);
            URI uri = componentsBuilder.pathSegment("persons", newPerson.getId()).build().toUri();
            PersonWebSocketTopic.enqueueNotification(ChangeType.ADDED, uri);
            return ResponseEntity.created(uri).build();
        }
    }

    @Transactional
    @PutMapping(path = "/persons/{id}", consumes = { "application/json" })
    @ResponseStatus(code = HttpStatus.OK)
    public void updatePerson(@RequestBody(required = true) final PersonDTO personDTO, @PathVariable(name = "id", required = true) final String id,
            final UriComponentsBuilder componentsBuilder) {
        Person person = personDTO.getPerson();
        Person existingPerson = Person.findById(person.getId());
        existingPerson.makeLike(person);
        URI uri = componentsBuilder.pathSegment("persons", id).build().toUri();
        PersonWebSocketTopic.enqueueNotification(ChangeType.UPDATED, uri);
    }

    @Transactional
    @DeleteMapping(path = "/persons/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void removePerson(@PathVariable(name = "id", required = true) final String id, final UriComponentsBuilder componentsBuilder) {
        Person.findById(id).delete();
        URI uri = componentsBuilder.pathSegment("persons", id).build().toUri();
        PersonWebSocketTopic.enqueueNotification(ChangeType.REMOVED, uri);
    }
}

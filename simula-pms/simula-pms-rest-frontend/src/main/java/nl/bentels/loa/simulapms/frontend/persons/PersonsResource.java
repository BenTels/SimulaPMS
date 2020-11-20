package nl.bentels.loa.simulapms.frontend.persons;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.neovisionaries.i18n.CountryCode;

import nl.bentels.loa.simulapms.frontend.persons.PersonWebSocketTopic.ChangeType;
import nl.bentels.loa.simulapms.frontend.persons.PersonsResource.PersonDTO.PersonAttributes;
import nl.bentels.loa.simulapms.model.person.Address;
import nl.bentels.loa.simulapms.model.person.AlreadyIdentifiedPersonException;
import nl.bentels.loa.simulapms.model.person.NoSuchPersonException;
import nl.bentels.loa.simulapms.model.person.Person;
import nl.bentels.loa.simulapms.model.person.PhoneNumber;

@RestController
@CrossOrigin(origins = "*", exposedHeaders = "Location")
public class PersonsResource {

    private static final Logger       LOGGER        = LoggerFactory.getLogger(PersonsResource.class);

    private static final ObjectReader OBJECT_READER = new ObjectMapper().reader();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static class PersonDTO {
        public static enum PersonAttributes {
            ID("id"),
            AGE_CLASS("ageclass"),
            LAST_NAME("lastname"),
            FIRST_NAMES("firstnames"),
            MIDDLE_NAMES("middlenames"),
            EMAIL_ADDRESSES("emailaddresses"),
            DATE_OF_BIRTH("dob"),
            PHONENUMBERS("phonenumbers"),
            PHONENUMBERS_NUMBER("number"),
            PHONENUMBERS_IS_MOBILE("mobile"),
            CORRESPONDENCE_ADDRESS("mainCorrespondenceAddress"),
            BILLING_ADDRESS("billingAddress"),
            ADDRESS_COUNTRY("country"),
            ADDRESS_LINES("lines"),
            UNKNOWN("____UNKNOWN____");

            private final String attributeName;

            private PersonAttributes(final String attributeName) {
                this.attributeName = attributeName;
            }

            public String getAttributeName() {
                return attributeName;
            }

            public static PersonAttributes fromAttributeName(final String s) {
                return Arrays.stream(PersonAttributes.values()).filter(pa -> pa.getAttributeName().equals(s)).findFirst().orElse(UNKNOWN);
            }

        }

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
            properties.put(PersonAttributes.ID.getAttributeName(), person.getId());
            properties.put(PersonAttributes.AGE_CLASS.getAttributeName(), person.getAgeClassNow().name());
            properties.put(PersonAttributes.LAST_NAME.getAttributeName(), person.getLastName());
            if (person.getFirstNames() != null) {
                properties.put(PersonAttributes.FIRST_NAMES.getAttributeName(), person.getFirstNames());
            }
            if (person.getMiddleNames() != null) {
                properties.put(PersonAttributes.MIDDLE_NAMES.getAttributeName(), person.getMiddleNames());
            }
            if (person.getEmailAddresses() != null) {
                properties.put(PersonAttributes.EMAIL_ADDRESSES.getAttributeName(), person.getEmailAddresses());
            }
            if (person.getDateOfBirth() != null) {
                properties.put(PersonAttributes.DATE_OF_BIRTH.getAttributeName(), person.getDateOfBirth().toString());
            }
            if (person.getPhoneNumbers() != null) {
                properties.put(PersonAttributes.PHONENUMBERS.getAttributeName(),
                        person.getPhoneNumbers().stream()
                                .map(pn -> Map.of(PersonAttributes.PHONENUMBERS_NUMBER.getAttributeName(), pn.getPhoneNumber(),
                                        PersonAttributes.PHONENUMBERS_IS_MOBILE.getAttributeName(), pn.isMobile()))
                                .collect(Collectors.toList()));
            }
            if (person.getCorrespondenceAddress() != null) {
                properties.put(PersonAttributes.CORRESPONDENCE_ADDRESS.getAttributeName(), toJSONObject(person.getCorrespondenceAddress()));
            }
            if (person.getBillingAddress() != null) {
                properties.put(PersonAttributes.BILLING_ADDRESS.getAttributeName(), toJSONObject(person.getBillingAddress()));
            }

            return properties;
        }

        Person getPerson() {
            return person != null ? person : mapToPerson();
        }

        private Person mapToPerson() {
            return Person.builder()
                    .id((String) map.get(PersonAttributes.ID.getAttributeName()))
                    .lastName((String) map.get(PersonAttributes.LAST_NAME.getAttributeName()))
                    .firstNames(emptyListObjectToNull(PersonAttributes.FIRST_NAMES.getAttributeName()))
                    .middleNames(emptyListObjectToNull(PersonAttributes.MIDDLE_NAMES.getAttributeName()))
                    .emailAddresses(emptyListObjectToNull(PersonAttributes.EMAIL_ADDRESSES.getAttributeName()))
                    .dateOfBirth(toLocalDate((String) map.get(PersonAttributes.DATE_OF_BIRTH.getAttributeName())))
                    .phoneNumbers(phoneNumbersToList())
                    .correspondenceAddress(toAddress((Map<String, Object>) map.get(PersonAttributes.CORRESPONDENCE_ADDRESS.getAttributeName())))
                    .billingAddress(toAddress((Map<String, Object>) map.get(PersonAttributes.BILLING_ADDRESS.getAttributeName())))
                    .build();

        }

        private List<PhoneNumber> phoneNumbersToList() {
            Object list0 = map.getOrDefault(PersonAttributes.PHONENUMBERS.getAttributeName(), List.of());
            if (list0 != null) {
                List<PhoneNumber> list = ((List<Map<String, Object>>) list0).stream()
                        .filter(pnm -> StringUtils.isNotBlank((String) pnm.get(PersonAttributes.PHONENUMBERS_NUMBER.getAttributeName())))
                        .map(pnm -> new PhoneNumber((String) pnm.get(PersonAttributes.PHONENUMBERS_NUMBER.getAttributeName()),
                                (Boolean) pnm.get(PersonAttributes.PHONENUMBERS_IS_MOBILE.getAttributeName())))
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
                CountryCode countryCode = CountryCode.valueOf((String) addressMap.get(PersonAttributes.ADDRESS_COUNTRY.getAttributeName()));
                List<String> addressLines = emptyListToNull((List<String>) addressMap.get(PersonAttributes.ADDRESS_LINES.getAttributeName()));
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
            Map<String, Object> addressMap = Map.of(PersonAttributes.ADDRESS_COUNTRY.getAttributeName(), address.getCountryCode().name());
            if (address.getAddressLines() != null && !address.getAddressLines().isEmpty()) {
                addressMap = new HashMap<>(addressMap);
                addressMap.put(PersonAttributes.ADDRESS_LINES.getAttributeName(), address.getAddressLines());
            }
            return addressMap;
        }
    }

    public static class PhoneNumberDTO {
        private String  number;
        private boolean mobile;

        public void setNumber(final String number) {
            this.number = number;
        }

        public void setMobile(final boolean mobile) {
            this.mobile = mobile;
        }

        public String getNumber() {
            return number;
        }

        public boolean isMobile() {
            return mobile;
        }

    }

    public static class AddressDTO {
        private List<String> lines;
        private CountryCode  country;

        public void setLines(final List<String> lines) {
            this.lines = lines;
        }

        public void setCountry(final CountryCode country) {
            this.country = country;
        }

        public List<String> getLines() {
            return lines;
        }

        public CountryCode getCountry() {
            return country;
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
    @PutMapping(path = "/persons/{id}/{field}", consumes = { "application/json", "text/plain" })
    @ResponseStatus(code = HttpStatus.OK)
    public void updatePersonField(@RequestBody(required = true) final String newVal, @PathVariable(name = "id", required = true) final String id,
            @PathVariable(name = "field", required = true) final String attributeName,
            final UriComponentsBuilder componentsBuilder) {
        Person existingPerson = Person.findById(id);
        switch (PersonAttributes.fromAttributeName(attributeName)) {
        case LAST_NAME -> existingPerson.withCorrectedLastName(newVal);
        case DATE_OF_BIRTH -> existingPerson.withCorrectedDateOfBirth(LocalDate.parse(newVal));
        case FIRST_NAMES -> existingPerson.withCorrectedFirstNames(jsonToArrayOfString(newVal));
        case MIDDLE_NAMES -> existingPerson.withCorrectedMiddleNames(jsonToArrayOfString(newVal));
        case EMAIL_ADDRESSES -> existingPerson.withCorrectedEmailAddresses(jsonToArrayOfString(newVal));
        case PHONENUMBERS -> existingPerson.withCorrectedPhoneNumbers(jsonToArrayOfPhoneNumber(newVal));
        case CORRESPONDENCE_ADDRESS -> existingPerson.withCorrectedCorrespondenceAddress(jsonToAddress(newVal));
        case BILLING_ADDRESS -> existingPerson.withCorrectedBillingAddress(jsonToAddress(newVal));
        default -> throw new IllegalArgumentException("Invalid person attribute name");
        }
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

    private String[] jsonToArrayOfString(final String newVal) {
        try {
            return OBJECT_MAPPER.readValue(newVal, new TypeReference<String[]>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private PhoneNumber[] jsonToArrayOfPhoneNumber(final String newVal) {
        try {
            List<PhoneNumberDTO> list = OBJECT_MAPPER.readValue(newVal, new TypeReference<List<PhoneNumberDTO>>() {
            });
            PhoneNumber[] array = list.stream()
                    .map(pdto -> new PhoneNumber(pdto.getNumber(), pdto.isMobile()))
                    .collect(Collectors.toList())
                    .toArray(new PhoneNumber[] {});
            return array;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Address jsonToAddress(final String newVal) {
        try {
            AddressDTO adto = OBJECT_MAPPER.readValue(newVal, new TypeReference<AddressDTO>() {
            });
            return Address.builder().countryCode(adto.getCountry()).addressLines(adto.getLines()).build();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

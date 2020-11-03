package nl.bentels.loa.simulapms.temprestfrontend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import nl.bentels.loa.simulapms.model.person.Address;
import nl.bentels.loa.simulapms.model.person.Person;
import nl.bentels.loa.simulapms.model.person.PhoneNumber;

@RestController
@CrossOrigin(origins = "http://localhost.localdomain:3000")
public class PersonsResource {

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Person with given id does not exists")
    public class NoSuchPersonException extends RuntimeException {

    }

    @ResponseStatus(code = HttpStatus.CONFLICT, reason = "Person with given id already exists")
    public class AlreadyIdentifiedPersonException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

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
                    .firstNames((List<String>) map.get("firstnames"))
                    .middleNames((List<String>) map.get("middlenames"))
                    .emailAddresses((List<String>) map.get("emailaddresses"))
                    .dateOfBirth(toLocalDate((String) map.get("dob")))
                    .phoneNumbers(((List<Map<String, Object>>) map.getOrDefault("phonenumbers", List.of())).stream()
                            .map(pnm -> new PhoneNumber((String) pnm.get("number"), (Boolean) pnm.get("mobile"))).collect(Collectors.toList()))
                    .correspondenceAddress(toAddress((Map<String, Object>) map.get("mainCorrespondenceAddress")))
                    .billingAddress(toAddress((Map<String, Object>) map.get("billingAddress")))
                    .build();

        }

        private Address toAddress(final Map<String, Object> addressMap) {
            return addressMap != null ? Address.builder()
                    .countryCode(CountryCode.valueOf((String) addressMap.get("country")))
                    .addressLines((List<String>) addressMap.get("lines"))
                    .build()
                    : null;
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

    private static final List<PersonDTO> PERSONS_LIST = new ArrayList<>(List.of(
            PersonDTO.of(Person.builder()
                    .id("ID-JLIO-000")
                    .emailAddresses(List.of("julio@gmail.com"))
                    .firstNames(List.of("Julió"))
                    .lastName("Espinóza")
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.DE)
                            .addressLines(List.of("Privada Calle 109 - Piso 4 - 34", "Iztacalco, Col. Agricola Pantitlan",
                                    "75520 Cancun, Q. ROO"))
                            .build())
                    .phoneNumbers(
                            List.of(new PhoneNumber("+52-155-5391-363", true), new PhoneNumber("+52-155-5391-372", false)))
                    .build()),
            PersonDTO.of(Person.builder()
                    .id("02999")
                    .emailAddresses(List.of("barack.obama@potus.gov"))
                    .firstNames(List.of("Barack"))
                    .middleNames(List.of("Hussein"))
                    .lastName("Obama")
                    .dateOfBirth(LocalDate.of(1961, 8, 04))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.US)
                            .addressLines(List.of("2500 W. Golf Road", "Hoffman Estates",
                                    "IL 60169-1114"))
                            .build())
                    .phoneNumbers(
                            List.of(new PhoneNumber("+1 212 555 3400", true)))
                    .build()),
            PersonDTO.of(Person.builder()
                    .id("03457")
                    .emailAddresses(List.of("bzt@bentels.nl", "bzt@bentels.dyndns.org"))
                    .firstNames(List.of("Benjamin", "Extrafakename"))
                    .middleNames(List.of("Ze'ev", "Extrafakename"))
                    .lastName("Tels")
                    .dateOfBirth(LocalDate.parse("1978-03-12"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.NL)
                            .addressLines(List.of("Twickel 13", "5655 JK", "Eindhoven"))
                            .build())
                    .phoneNumbers(
                            List.of(new PhoneNumber("+31614435308", true), new PhoneNumber("+31 40 252 13 94", false)))
                    .build()),
            PersonDTO.of(Person.builder()
                    .id("08888")
                    .emailAddresses(List.of("francois.philippe.marron@tomtel.fr", "fmarron@gmail.com"))
                    .firstNames(List.of("François"))
                    .middleNames(List.of("Philippe"))
                    .lastName("Marron")
                    .dateOfBirth(LocalDate.parse("1985-06-05"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR)
                            .addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE"))
                            .build())
                    .phoneNumbers(
                            List.of(new PhoneNumber("+33-6-78679-0087", true)))
                    .build()),
            PersonDTO.of(Person.builder()
                    .id("08889")
                    .emailAddresses(List.of("marie.marron@tomtel.fr", "memarron@gmail.com"))
                    .firstNames(List.of("Marie", "Elle"))
                    .lastName("Marron-Chevreux")
                    .dateOfBirth(LocalDate.parse("1988-04-26"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR)
                            .addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE"))
                            .build())
                    .phoneNumbers(List.of(new PhoneNumber("+33-6-78506-9089", true))).build()),
            PersonDTO.of(Person.builder().id("08890").emailAddresses(List.of("thomas.marron@tomtel.fr")).firstNames(List.of("Thomas")).lastName("Marron")
                    .dateOfBirth(LocalDate.parse("2014-01-06"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR).addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+33-6-66506-9089", true))).build()),

            PersonDTO.of(Person.builder().id("08891").emailAddresses(List.of("jeanelle.marron@tomtel.fr")).firstNames(List.of("Jeanelle")).lastName("Marron")
                    .dateOfBirth(LocalDate.parse("2017-11-18"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR).addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE")).build()).build()),
            PersonDTO.of(Person.builder().id("08892").firstNames(List.of("Manou")).lastName("Marron").dateOfBirth(LocalDate.parse("2020-07-18"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR).addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE")).build()).build()),
            PersonDTO.of(Person.builder().id("19198").emailAddresses(List.of("betsy@bentels.nl")).firstNames(List.of("Betsy")).lastName("Tels")
                    .dateOfBirth(LocalDate.parse("1942-05-17"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.NL).addressLines(List.of("Twickel 13", "5655 JK", "Eindhoven")).build())
                    .billingAddress(Address.builder().countryCode(CountryCode.NL).addressLines(List.of("Postbus 45667", "5778 EA", "Eindhoven")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+31 6 211 720 53", true))).build()),
            PersonDTO.of(Person.builder().id("20007").emailAddresses(List.of("w.j.johansson@gmail.com")).firstNames(List.of("Wilhelm", "Jakob"))
                    .middleNames(List.of("Bernhardt", "Maria")).lastName("Johansson").dateOfBirth(LocalDate.parse("1992-05-09"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.DE).addressLines(List.of("Karlsstrasse 23", "Karlsruhe")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+34 61 211 720 53", true))).build()),
            PersonDTO.of(Person.builder().id("20008").firstNames(List.of("Jimmy")).lastName("Johansson").dateOfBirth(LocalDate.parse("2005-09-21"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.DE).addressLines(List.of("Karlsstrasse 23", "Karlsruhe")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+34 61 211 720 53", true))).build()),
            PersonDTO.of(Person.builder().id("20019").firstNames(List.of("Pippi")).lastName("Langkous").dateOfBirth(LocalDate.parse("2019-08-01"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.SE).build()).build())));

    @GetMapping(path = "/persons", produces = { "application/json" })
    public ResponseEntity<List<PersonDTO>> getPersons(@RequestParam(name = "searchTerm", defaultValue = "", required = false) final String filter) {
        LOGGER.debug("Going to retrieve persons with filter [{}]", filter);
        List<PersonDTO> filteredList = PERSONS_LIST.stream()
                .filter(p -> StringUtils.isBlank(filter) || p.getPerson().getLastName().toLowerCase().contains(filter.trim().toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filteredList);
    }

    @PostMapping(path = "/persons", consumes = { "application/json" })
    public ResponseEntity<Void> createPerson(@RequestBody(required = true) final PersonDTO personDTO, final UriComponentsBuilder componentsBuilder) {
        Person person = personDTO.getPerson();
        if (StringUtils.isNotBlank(person.getId())) {
            throw new AlreadyIdentifiedPersonException();
        } else {
            Person newPerson = Person.fromTemplate(person);
            PERSONS_LIST.add(PersonDTO.of(newPerson));
            return ResponseEntity.created(componentsBuilder.pathSegment("persons", newPerson.getId()).build().toUri()).build();
        }
    }

    @PutMapping(path = "/persons/{id}", consumes = { "application/json" })
    @ResponseStatus(code = HttpStatus.OK)
    public void updatePerson(@RequestBody(required = true) final PersonDTO personDTO, @PathVariable(name = "id", required = true) final String id) {
        Person person = personDTO.getPerson();
        AtomicInteger replacementCount = new AtomicInteger();
        List<PersonDTO> updatedDTOs = PERSONS_LIST.stream()
                .map(pers -> {
                    if (pers.getPerson().getId().equals(person.getId())) {
                        replacementCount.incrementAndGet();
                        return PersonDTO.of(person);
                    }
                    return pers;
                }).collect(Collectors.toList());
        if (replacementCount.get() == 1) {
            synchronized (PERSONS_LIST) {
                PERSONS_LIST.clear();
                PERSONS_LIST.addAll(updatedDTOs);
            }
        } else {
            throw new NoSuchPersonException();
        }
    }

    @DeleteMapping(path = "/persons/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void removePerson(@PathVariable(name = "id", required = true) final String id) {
        PERSONS_LIST.removeIf(persDTO -> persDTO.getPerson().getId().equals(id));
    }
}

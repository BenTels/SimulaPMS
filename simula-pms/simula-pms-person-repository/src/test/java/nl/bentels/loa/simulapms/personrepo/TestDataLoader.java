package nl.bentels.loa.simulapms.personrepo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.neovisionaries.i18n.CountryCode;

import nl.bentels.loa.simulapms.model.person.Address;
import nl.bentels.loa.simulapms.model.person.Person;
import nl.bentels.loa.simulapms.model.person.PhoneNumber;

public class TestDataLoader extends AbstractRepositoryIntegrationTest {

    public static final List<Person> PERSONS_LIST = new ArrayList<>(List.of(
            Person.builder()
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
                    .build(),
            Person.builder()
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
                    .build(),
            Person.builder()
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
                    .build(),
            Person.builder()
                    .id("08888")
                    .emailAddresses(List.of("francois.philippe.marron@tomtel.fr", "fmarron@gmail.com"))
                    .firstNames(List.of("François"))
                    .middleNames(List.of("Philippe"))
                    .lastName("Marron")
                    .dateOfBirth(LocalDate.parse("1985-06-05"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR).addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+33-6-78679-0087", true))).build(),
            Person.builder()
                    .id("08889")
                    .emailAddresses(List.of("marie.marron@tomtel.fr", "memarron@gmail.com"))
                    .firstNames(List.of("Marie", "Elle"))
                    .lastName("Marron-Chevreux")
                    .dateOfBirth(LocalDate.parse("1988-04-26"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR).addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+33-6-78506-9089", true)))
                    .build(),
            Person.builder()
                    .id("08890")
                    .emailAddresses(List.of("thomas.marron@tomtel.fr")).firstNames(List.of("Thomas")).lastName("Marron").dateOfBirth(LocalDate.parse("2014-01-06"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR).addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+33-6-66506-9089", true))).build(),
            Person.builder().id("08891").emailAddresses(List.of("jeanelle.marron@tomtel.fr")).firstNames(List.of("Jeanelle")).lastName("Marron")
                    .dateOfBirth(LocalDate.parse("2017-11-18"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR).addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE")).build()).build(),
            Person.builder().id("08892").firstNames(List.of("Manou")).lastName("Marron").dateOfBirth(LocalDate.parse("2020-07-18"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.FR).addressLines(List.of("32 Rue de Fleurs", "33500 LIBOURNE")).build()).build(),
            Person.builder()
                    .id("19198")
                    .emailAddresses(List.of("betsy@bentels.nl"))
                    .firstNames(List.of("Betsy")).lastName("Tels")
                    .dateOfBirth(LocalDate.parse("1942-05-17"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.NL).addressLines(List.of("Twickel 13", "5655 JK", "Eindhoven")).build())
                    .billingAddress(Address.builder().countryCode(CountryCode.NL).addressLines(List.of("Postbus 45667", "5778 EA", "Eindhoven")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+31 6 211 720 53", true))).build(),
            Person.builder().id("20007").emailAddresses(List.of("w.j.johansson@gmail.com")).firstNames(List.of("Wilhelm", "Jakob")).middleNames(List.of("Bernhardt", "Maria"))
                    .lastName("Johansson").dateOfBirth(LocalDate.parse("1992-05-09"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.DE).addressLines(List.of("Karlsstrasse 23", "Karlsruhe")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+34 61 211 720 53", true))).build(),
            Person.builder().id("20008").firstNames(List.of("Jimmy")).lastName("Johansson").dateOfBirth(LocalDate.parse("2005-09-21"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.DE).addressLines(List.of("Karlsstrasse 23", "Karlsruhe")).build())
                    .phoneNumbers(List.of(new PhoneNumber("+34 61 211 720 53", true))).build(),
            Person.builder().id("20019").firstNames(List.of("Pippi")).lastName("Langkous").dateOfBirth(LocalDate.parse("2019-08-01"))
                    .correspondenceAddress(Address.builder().countryCode(CountryCode.SE).build()).build()));

    @Test
    public void loadTestData() {
//        Person tels = Person.findById("5f5f6c1c5fc0d0741b9c7732");
//        assertTels(tels);

        for (Person person : PERSONS_LIST) {
            Person.fromTemplate(person);
        }
    }
}

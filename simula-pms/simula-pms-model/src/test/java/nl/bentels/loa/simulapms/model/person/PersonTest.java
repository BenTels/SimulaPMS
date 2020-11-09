package nl.bentels.loa.simulapms.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.neovisionaries.i18n.CountryCode;

import nl.bentels.loa.simulapms.model.person.Person.AgeClass;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PersonTestConfig.class)
class PersonTest {

    @ParameterizedTest
    @DisplayName("When person's first names corrected, then all other fields remain unchanged")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenFirstNamesCorrected_thenOkay(final Person person) throws NoSuchMethodException, SecurityException {
        Person mutated = person.withCorrectedFirstNames("John", "James");
        assertTrue(sameExceptInProperty(person, mutated, person.getClass().getMethod("getFirstNames"),
                List.of("John", "James")),
                () -> "Mutating first names does not work correctly");
    }

    @ParameterizedTest
    @DisplayName("When person's middle names corrected, then all other fields remain unchanged")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenMiddleNamesCorrected_thenOkay(final Person person) throws NoSuchMethodException, SecurityException {
        Person mutated = person.withCorrectedMiddleNames("John", "James");
        assertTrue(sameExceptInProperty(person, mutated, person.getClass().getMethod("getMiddleNames"),
                List.of("John", "James")),
                () -> "Mutating middle names does not work correctly");
    }

    @ParameterizedTest
    @DisplayName("When person's last name corrected, then all other fields remain unchanged")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenLastNameCorrected_thenOkay(final Person person) throws NoSuchMethodException, SecurityException {
        Person mutated = person.withCorrectedLastName("James");
        assertTrue(sameExceptInProperty(person, mutated, person.getClass().getMethod("getLastName"),
                "James"),
                () -> "Mutating last name does not work correctly");
    }

    @ParameterizedTest
    @DisplayName("When person's correspondence address corrected, then all other fields remain unchanged")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenMainCorrespondenceAddressCorrected_thenOkay(final Person person)
            throws NoSuchMethodException, SecurityException {
        Address testAddress = Address
                .builder().addressLines(List.of("Gryffindor House Commons Room",
                        "Hogwarts School of Witchcraft and Wizardry", "Little Winging"))
                .countryCode(CountryCode.GB).build();
        Person mutated = person.withNewCorrespondenceAddress(testAddress);
        assertTrue(
                sameExceptInProperty(person, mutated, person.getClass().getMethod("getCorrespondenceAddress"),
                        testAddress),
                () -> "Mutating correspondence address does not work correctly");
    }

    @ParameterizedTest
    @DisplayName("When person's billing address corrected, then all other fields remain unchanged")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenBillingAddressCorrected_thenOkay(final Person person) throws NoSuchMethodException, SecurityException {
        Address testAddress = Address
                .builder().addressLines(List.of("Gryffindor House Commons Room",
                        "Hogwarts School of Witchcraft and Wizardry", "Little Winging"))
                .countryCode(CountryCode.GB).build();
        Person mutated = person.withNewBillingAddress(testAddress);
        assertTrue(sameExceptInProperty(person, mutated, person.getClass().getMethod("getBillingAddress"),
                testAddress),
                () -> "Mutating billing addresses does not work correctly");
    }

    @ParameterizedTest
    @DisplayName("When person's email addresses corrected, then all other fields remain unchanged")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenEmailAddressesCorrected_thenOkay(final Person person) throws NoSuchMethodException, SecurityException {
        Person mutated = person.withNewEmailAddresses("new@newlynew.com");
        assertTrue(sameExceptInProperty(person, mutated, person.getClass().getMethod("getEmailAddresses"),
                List.of("new@newlynew.com")),
                () -> "Mutating email addresses does not work correctly");
    }

    @ParameterizedTest
    @DisplayName("When person's phone numbers corrected, then all other fields remain unchanged")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenPhoneNumbersCorrected_thenOkay(final Person person) throws NoSuchMethodException, SecurityException {
        PhoneNumber phoneNumber = new PhoneNumber("+31012345678", false);
        Person mutated = person.withNewPhoneNumbers(phoneNumber);
        assertTrue(sameExceptInProperty(person, mutated, person.getClass().getMethod("getPhoneNumbers"),
                List.of(phoneNumber)),
                () -> "Mutating phone numbers does not work correctly");
    }

    @ParameterizedTest
    @DisplayName("When person's last name removed, then exception is thrown")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenLastNameDropped_thenException(final Person person) throws NoSuchMethodException, SecurityException {
        assertThrows(ConstraintViolationException.class, () -> person.withCorrectedLastName(null));
        assertThrows(ConstraintViolationException.class, () -> person.withCorrectedLastName(""));
        assertThrows(ConstraintViolationException.class, () -> person.withCorrectedLastName(" "));
    }

    @ParameterizedTest
    @DisplayName("When person's date of birth removed, then exception is thrown")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenDOBDropped_thenException(final Person person) throws NoSuchMethodException, SecurityException {
        assertThrows(ConstraintViolationException.class, () -> person.withCorrectedDateOfBirth(null));
    }

    @ParameterizedTest
    @DisplayName("When address set without country, then exception is thrown")
    @ArgumentsSource(PersonTestArgumentsSource.class)
    void whenNoCountryOnAddress_thenException(final Person person) throws NoSuchMethodException, SecurityException {
        assertThrows(ConstraintViolationException.class, () -> person.withNewBillingAddress(Address.builder().addressLines(List.of("test 0", "test 1")).build()));
    }

    @Test
    @DisplayName("When person created without last name, then exception is thrown")
    void whenLastNameDropped_thenException() throws NoSuchMethodException, SecurityException {
        assertThrows(ConstraintViolationException.class, () -> Person.builder().id("BANG").build());
        assertThrows(ConstraintViolationException.class, () -> Person.builder().id("BANG").lastName("").build());
        assertThrows(ConstraintViolationException.class, () -> Person.builder().id("BANG").lastName("   ").build());
    }

    @Test
    @DisplayName("When person created, then age class is correct")
    void whenpersonCreated_thenAgeClassCorrect() throws NoSuchMethodException, SecurityException {
        LocalDate infantAge = LocalDate.now().minusYears(1);
        LocalDate childAge = LocalDate.now().minusYears(7);
        LocalDate adultAge = LocalDate.now().minusYears(46);
        LocalDate seniorAge = LocalDate.now().minusYears(75);

        assertEquals(AgeClass.ADULT, Person.builder().id("NO_AGE").lastName("bliep").build().getAgeClassNow());
        assertEquals(AgeClass.INFANT, Person.builder().id("NO_AGE").lastName("bliep").dateOfBirth(infantAge).build().getAgeClassNow());
        assertEquals(AgeClass.CHILD, Person.builder().id("NO_AGE").lastName("bliep").dateOfBirth(childAge).build().getAgeClassNow());
        assertEquals(AgeClass.ADULT, Person.builder().id("NO_AGE").lastName("bliep").dateOfBirth(adultAge).build().getAgeClassNow());
        assertEquals(AgeClass.SENIOR, Person.builder().id("NO_AGE").lastName("bliep").dateOfBirth(seniorAge).build().getAgeClassNow());
    }

    private boolean sameExceptInProperty(final Person expected, final Person actual,
            final Method expectedDifferenceMethod,
            final Object expectedDifferenceValue) {
        boolean differenceIsAsExpected = false;

        boolean isSameInNonDifferenceProperties = Arrays.stream(Person.class.getMethods())
                .filter(method -> method.getName().startsWith("get") && method.getParameterCount() == 0)
                .filter(method -> !method.equals(expectedDifferenceMethod))
                .map(method -> {
                    try {
                        Object expectedPropertyValue = method.invoke(expected);
                        Object actualPropertyValue = method.invoke(actual);
                        return Objects.equals(expectedPropertyValue,
                                actualPropertyValue);
                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {
                        throw new UnsupportedOperationException(
                                "Could not retrieve value for property "
                                        + method.getName());
                    }
                })
                .reduce(true, Boolean::logicalAnd);

        try {
            Object expectedPropertyValue = expectedDifferenceMethod.invoke(expected);
            Object actualPropertyValue = expectedDifferenceMethod.invoke(actual);

            differenceIsAsExpected = !Objects.equals(expectedPropertyValue, actualPropertyValue)
                    && Objects.equals(expectedDifferenceValue, actualPropertyValue);

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(
                    "Could not retrieve value for property " + expectedDifferenceMethod.getName());
        }

        return isSameInNonDifferenceProperties && differenceIsAsExpected;
    }

}

@Configuration
@EnableLoadTimeWeaving
class PersonTestConfig {

}

class PersonTestArgumentsSource implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
        return Stream.of(benTels(), werner(), julio()).map(Arguments::of);
    }

    private Person benTels() {
        return Person.builder()
                .id("ID-BZT-000")
                .emailAddresses(List.of("bzt@bentels.nl"))
                .firstNames(List.of("Benjamin"))
                .lastName("Tels")
                .correspondenceAddress(Address.builder().countryCode(CountryCode.NL)
                        .addressLines(List.of("Twickel 13", "5655JK", "Eindhoven")).build())
                .middleNames(List.of("Ze'ev"))
                .phoneNumbers(List.of(new PhoneNumber("+31614435308", true)))
                .build();
    }

    private Person werner() {
        return Person.builder()
                .id("ID-WRNR-000")
                .emailAddresses(List.of("werner@gmail.com"))
                .firstNames(List.of("Werner", "Johann"))
                .lastName("Röntgen")
                .correspondenceAddress(Address.builder().countryCode(CountryCode.DE)
                        .addressLines(List.of("Rotstraße 25", "67894", "Maxdorf")).build())
                .billingAddress(Address.builder().countryCode(CountryCode.DE)
                        .addressLines(List.of("Rotstraße 23b", "67894", "Maxdorf")).build())
                .middleNames(List.of("Wilhelm", "Karl"))
                .phoneNumbers(List.of(new PhoneNumber("+49 30 901820", true)))
                .build();
    }

    private Person julio() {
        return Person.builder()
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
                .build();
    }

}

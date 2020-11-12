package nl.bentels.loa.simulapms.model.person;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Value;

@Value
@Builder()
public class Person {

    public static enum AgeClass {
        INFANT(2),
        CHILD(14),
        ADULT(64),
        SENIOR(Integer.MAX_VALUE);

        private final int maxAge;

        private AgeClass(final int maxAge) {
            this.maxAge = maxAge;
        }

        static AgeClass findAgeClassGivenDateOfBirth(final LocalDate dateOfBirth) {
            LocalDate currentDate = LocalDate.now();
            return findAgeClassOnSpecificDate(dateOfBirth, currentDate);
        }

        static AgeClass findAgeClassOnSpecificDate(final LocalDate dateOfBirth, final LocalDate comparisonDate) {
            long age = ChronoUnit.YEARS.between(dateOfBirth, comparisonDate);
            return Arrays.stream(values())
                    .filter(ac -> 0 <= age && age <= ac.maxAge)
                    .findFirst()
                    .orElse(ADULT);
        }
    }

    private String            id;
    private List<String>      firstNames;
    private List<String>      middleNames;
    @NotBlank
    private String            lastName;
    private Address           correspondenceAddress;
    private Address           billingAddress;
    private List<String>      emailAddresses;
    private List<PhoneNumber> phoneNumbers;
    private LocalDate         dateOfBirth;

    public static Person findById(final String id) {
        return null;
    }

    public static List<Person> findAllOfThem() {
        return null;
    }

    public static Person fromTemplate(@NotNull final Person template) {
        return fromTemplateWithId(template, UUID.randomUUID().toString());
    }

    public static Person fromTemplateWithId(@NotNull final Person template, @NotNull final String id) {
        return builderFromTemplateWithId(template, id)
                .build();
    }

    private static PersonBuilder builderFromTemplateWithId(final Person template, final String id) {
        return Person.builder()
                .id(id)
                .firstNames(template.getFirstNames())
                .billingAddress(template.getBillingAddress())
                .emailAddresses(template.getEmailAddresses())
                .lastName(template.getLastName())
                .correspondenceAddress(template.getCorrespondenceAddress())
                .middleNames(template.getMiddleNames())
                .phoneNumbers(template.getPhoneNumbers())
                .dateOfBirth(template.getDateOfBirth());
    }

    public AgeClass getAgeClassNow() {
        return getAgeClassAtDateOfArrival(LocalDate.now());
    }

    private AgeClass getAgeClassAtDateOfArrival(final LocalDate arrivalDate) {
        return getDateOfBirth() != null ? AgeClass.findAgeClassOnSpecificDate(getDateOfBirth(), arrivalDate) : AgeClass.ADULT;
    }

    public Person makeLike(final Person updateTemplate) {
        return fromTemplateWithId(updateTemplate, getId());
    }

    public Person delete() {
        return Person.builder().id(getId()).lastName("DELETED").build();
    }

    public Person withCorrectedFirstNames(final String... names) {
        return withMutatedField("firstNames", Arrays.asList(names));
    }

    public Person withCorrectedMiddleNames(final String... names) {
        return withMutatedField("middleNames", Arrays.asList(names));
    }

    public Person withCorrectedLastName(@NotEmpty final String name) {
        return withMutatedField("lastName", name);
    }

    public Person withCorrectedCorrespondenceAddress(@NotNull final Address newAddress) {
        return withMutatedField("correspondenceAddress", newAddress);
    }

    public Person withCorrectedBillingAddress(final Address newAddress) {
        return withMutatedField("billingAddress", newAddress);
    }

    public Person withCorrectedEmailAddresses(final String... emailAddresses) {
        return withMutatedField("emailAddresses", Arrays.asList(emailAddresses));
    }

    public Person withCorrectedPhoneNumbers(final PhoneNumber... phoneNumbers) {
        return withMutatedField("phoneNumbers", Arrays.asList(phoneNumbers));
    }

    public Person withCorrectedDateOfBirth(final @NotNull LocalDate dateOfBirth) {
        return withMutatedField("dateOfBirth", dateOfBirth);
    }

    @SuppressWarnings("unchecked")
    private Person withMutatedField(final String fieldName, final Object newValue) {
        PersonBuilder builder = builderFromTemplateWithId(this, getId());
        switch (fieldName) {
        case "firstNames" -> builder.firstNames((List<String>) newValue);
        case "middleNames" -> builder.middleNames((List<String>) newValue);
        case "lastName" -> builder.lastName((String) newValue);
        case "correspondenceAddress" -> builder.correspondenceAddress((Address) newValue);
        case "billingAddress" -> builder.billingAddress((Address) newValue);
        case "emailAddresses" -> builder.emailAddresses((List<String>) newValue);
        case "phoneNumbers" -> builder.phoneNumbers((List<PhoneNumber>) newValue);
        case "dateOfBirth" -> builder.dateOfBirth((LocalDate) newValue);
        }
        return builder.build();
    }
}

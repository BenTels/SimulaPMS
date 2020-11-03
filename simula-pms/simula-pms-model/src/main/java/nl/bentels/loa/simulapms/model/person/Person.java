package nl.bentels.loa.simulapms.model.person;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    @NotEmpty
    private String            lastName;
    private Address           correspondenceAddress;
    private Address           billingAddress;
    private List<String>      emailAddresses;
    private List<PhoneNumber> phoneNumbers;
    private LocalDate         dateOfBirth;

    public AgeClass getAgeClassNow() {
        return getAgeClassAtDateOfArrival(LocalDate.now());
    }

    private AgeClass getAgeClassAtDateOfArrival(@NotNull final LocalDate arrivalDate) {
        return getDateOfBirth() != null ? AgeClass.findAgeClassOnSpecificDate(getDateOfBirth(), arrivalDate) : AgeClass.ADULT;
    }

    public static Person fromTemplate(@NotNull final Person template) {
        return fromTemplateWithId(template, UUID.randomUUID().toString());
    }

    public static Person fromTemplateWithId(@NotNull final Person template, @NotNull final String id) {
        return Person.builder()
                .id(id)
                .firstNames(template.getFirstNames())
                .billingAddress(template.getBillingAddress())
                .emailAddresses(template.getEmailAddresses())
                .lastName(template.getLastName())
                .correspondenceAddress(template.getCorrespondenceAddress())
                .middleNames(template.getMiddleNames())
                .phoneNumbers(template.getPhoneNumbers())
                .dateOfBirth(template.getDateOfBirth())
                .build();

    }

    public Person withCorrectedFirstNames(final String... names) {
        return Person.builder()
                .id(getId())
                .firstNames(Arrays.asList(names))
                .billingAddress(getBillingAddress())
                .emailAddresses(getEmailAddresses())
                .lastName(getLastName())
                .correspondenceAddress(getCorrespondenceAddress())
                .middleNames(getMiddleNames())
                .phoneNumbers(getPhoneNumbers())
                .dateOfBirth(getDateOfBirth())
                .build();
    }

    public Person withCorrectedMiddleNames(final String... names) {
        return Person.builder()
                .id(getId())
                .middleNames(Arrays.asList(names))
                .billingAddress(getBillingAddress())
                .emailAddresses(getEmailAddresses())
                .firstNames(getFirstNames())
                .lastName(getLastName())
                .correspondenceAddress(getCorrespondenceAddress())
                .phoneNumbers(getPhoneNumbers())
                .dateOfBirth(getDateOfBirth())
                .build();
    }

    public Person withCorrectedLastName(@NotEmpty final String name) {
        return Person.builder()
                .id(getId())
                .lastName(name)
                .billingAddress(getBillingAddress())
                .emailAddresses(getEmailAddresses())
                .firstNames(getFirstNames())
                .middleNames(getMiddleNames())
                .correspondenceAddress(getCorrespondenceAddress())
                .phoneNumbers(getPhoneNumbers())
                .dateOfBirth(getDateOfBirth())
                .build();
    }

    public Person withNewCorrespondenceAddress(@NotNull final Address newAddress) {
        return Person.builder()
                .id(getId())
                .correspondenceAddress(newAddress)
                .billingAddress(getBillingAddress())
                .emailAddresses(getEmailAddresses())
                .firstNames(getFirstNames())
                .lastName(getLastName())
                .middleNames(getMiddleNames())
                .phoneNumbers(getPhoneNumbers())
                .dateOfBirth(getDateOfBirth())
                .build();
    }

    public Person withNewBillingAddress(final Address newAddress) {
        return Person.builder()
                .id(getId())
                .billingAddress(newAddress)
                .correspondenceAddress(getCorrespondenceAddress())
                .emailAddresses(getEmailAddresses())
                .firstNames(getFirstNames())
                .lastName(getLastName())
                .middleNames(getMiddleNames())
                .phoneNumbers(getPhoneNumbers())
                .dateOfBirth(getDateOfBirth())
                .build();
    }

    public Person withNewEmailAddresses(final String... emailAddresses) {
        return Person.builder()
                .id(getId())
                .emailAddresses(Arrays.asList(emailAddresses))
                .billingAddress(getBillingAddress())
                .correspondenceAddress(getCorrespondenceAddress())
                .firstNames(getFirstNames())
                .lastName(getLastName())
                .middleNames(getMiddleNames())
                .phoneNumbers(getPhoneNumbers())
                .dateOfBirth(getDateOfBirth())
                .build();
    }

    public Person withNewPhoneNumbers(final PhoneNumber... phoneNumbers) {
        return Person.builder()
                .id(getId())
                .phoneNumbers(Arrays.asList(phoneNumbers))
                .billingAddress(getBillingAddress())
                .correspondenceAddress(getCorrespondenceAddress())
                .emailAddresses(getEmailAddresses())
                .firstNames(getFirstNames())
                .lastName(getLastName())
                .middleNames(getMiddleNames())
                .dateOfBirth(getDateOfBirth())
                .build();
    }

    public Person withCorrectedDateOfBirth(final @NotNull LocalDate dateOfBirth) {
        return Person.builder()
                .id(getId())
                .dateOfBirth(dateOfBirth)
                .billingAddress(getBillingAddress())
                .correspondenceAddress(getCorrespondenceAddress())
                .emailAddresses(getEmailAddresses())
                .firstNames(getFirstNames())
                .lastName(getLastName())
                .middleNames(getMiddleNames())
                .phoneNumbers(getPhoneNumbers())
                .build();
    }
}

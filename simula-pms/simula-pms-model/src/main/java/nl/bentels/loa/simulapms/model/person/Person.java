package nl.bentels.loa.simulapms.model.person;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Value;

@Value
@Builder()
public class Person {

    private String            id;
    private List<String>      firstNames;
    private List<String>      middleNames;
    @NotEmpty
    private String            lastName;
    private Address           correspondenceAddress;
    private Address           billingAddress;
    private List<String>      emailAddresses;
    private List<PhoneNumber> phoneNumbers;

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
                .build();
    }
}

package nl.bentels.loa.simulapms.model.person;

import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder()
public class Person {

    private String                  id;
    private final List<String>      firstNames;
    private final List<String>      middleNames;
    private final String            lastName;
    private final Address           correspondenceAddress;
    private final Address           billingAddress;
    private final List<String>      emailAddresses;
    private final List<PhoneNumber> phoneNumbers;

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

    public Person withCorrectedLastName(final String name) {
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

    public Person withNewCorrespondenceAddress(final Address newAddress) {
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

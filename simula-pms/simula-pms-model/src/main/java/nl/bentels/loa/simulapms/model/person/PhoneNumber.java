package nl.bentels.loa.simulapms.model.person;

import lombok.Value;

@Value
public class PhoneNumber {
    private final String  phoneNumber;
    private final boolean mobile;

    public PhoneNumber(final String phoneNumber, final boolean mobile) {
        this.phoneNumber = phoneNumber;
        this.mobile      = mobile;
    }

}

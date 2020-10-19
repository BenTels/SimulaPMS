package nl.bentels.loa.simulapms.model.person;

import javax.validation.constraints.Pattern;

import lombok.Value;

@Value
public class PhoneNumber {
    @Pattern(regexp = "\\+?[0-9 \\-]{6,}")
    private final String  phoneNumber;
    private final boolean mobile;

    public PhoneNumber(final String phoneNumber, final boolean mobile) {
        this.phoneNumber = phoneNumber;
        this.mobile = mobile;
    }

}

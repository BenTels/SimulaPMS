package nl.bentels.test.persons.domain;

import javax.validation.constraints.Pattern;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record PhoneNumber(@Pattern(regexp = "\\+?[0-9 \\-]{6,}") String number, boolean mobile) {

}

package nl.bentels.test.persons.domain;

import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record Address(List<String> lines, CountryCode country) {

}

package nl.bentels.test.persons.domain;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import io.micronaut.core.annotation.Introspected;
import nl.bentels.test.LocalDateSerializer;

@Introspected
public record Person(String id, List<String> firstnames, List<String> middlenames, @NotBlank String lastname,
		Address mainCorrespondenceAddress, Address billingAddress, List<String> emailaddresses,
		List<PhoneNumber> phonenumbers,
		@JsonSerialize(using = LocalDateSerializer.class)
		@JsonDeserialize(using = LocalDateDeserializer.class)
		LocalDate dob) {

	@JsonProperty("ageclass")
	public AgeClass getAgeClassNow() {
		return getAgeClassAtDateOfArrival(LocalDate.now());
	}

	@Transient
	private AgeClass getAgeClassAtDateOfArrival(final LocalDate arrivalDate) {
		return dob() != null ? AgeClass.findAgeClassOnSpecificDate(dob(), arrivalDate) : AgeClass.ADULT;
	}

}

package nl.bentels.loa.simulapms.model;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.bentels.loa.simulapms.LocalDateSerializer;

@Data
@Builder()
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "person")
public class Person {

	@Id
	private String id;
	@Field("firstNames")
	@Builder.Default
	private List<String> firstnames = List.of();
	@Field("middleNames")
	@Builder.Default
	private List<String> middlenames = List.of();
	@Field("lastName")
	private String lastname;
	private Address mainCorrespondenceAddress;
	private Address billingAddress;
	@Field("emailAddresses")
	@Builder.Default
	private List<String> emailaddresses = List.of();
	@Field("phoneNumbers")
	private List<PhoneNumber> phonenumbers;
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@Field(name="dateOfBirth", targetType = FieldType.STRING)
	private LocalDate dob;

	public Person withId(String id) {
		if (StringUtils.isNotBlank(getId())) {
			return this;
		} else {
			return Person.builder().id(id).billingAddress(getBillingAddress()).dob(getDob())
					.emailaddresses(getEmailaddresses()).firstnames(getFirstnames()).lastname(getLastname())
					.mainCorrespondenceAddress(getMainCorrespondenceAddress()).middlenames(getMiddlenames()).phonenumbers(getPhonenumbers()).build();
		}
	}

	@JsonProperty("ageclass")
	@BsonIgnore
	public AgeClass getAgeClassNow() {
		return getAgeClassAtDateOfArrival(LocalDate.now());
	}

	@Transient
	private AgeClass getAgeClassAtDateOfArrival(final LocalDate arrivalDate) {
		return getDob() != null ? AgeClass.findAgeClassOnSpecificDate(getDob(), arrivalDate) : AgeClass.ADULT;
	}

}

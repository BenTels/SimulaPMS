package nl.bentels.test.persons.domain;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.validation.constraints.NotBlank;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Person {
	
	public String id; 
	@BsonProperty("firstNames")
	public List<String> firstnames; 
	@BsonProperty("middleNames")
	public List<String> middlenames; 
	@BsonProperty("lastName")
	@NotBlank public String lastname;
	public Address mainCorrespondenceAddress; 
	public Address billingAddress; 
	@BsonProperty("emailAddresses")
	public List<String> emailaddresses;
	@BsonProperty("phoneNumbers")
	public List<PhoneNumber> phonenumbers;
	@BsonIgnore
	public LocalDate dob;

	@JsonbProperty("ageclass")
	public AgeClass getAgeClassNow() {
		return getAgeClassAtDateOfArrival(LocalDate.now());
	}

	@Transient
	@JsonbTransient
	private AgeClass getAgeClassAtDateOfArrival(final LocalDate arrivalDate) {
		return dob != null ? AgeClass.findAgeClassOnSpecificDate(dob, arrivalDate) : AgeClass.ADULT;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", firstnames=" + firstnames + ", middlenames=" + middlenames + ", lastname="
				+ lastname + ", mainCorrespondenceAddress=" + mainCorrespondenceAddress + ", billingAddress="
				+ billingAddress + ", emailaddresses=" + emailaddresses + ", phonenumbers=" + phonenumbers + ", dob="
				+ dob + ", getAgeClassNow()=" + getAgeClassNow() + "]";
	}

	
}

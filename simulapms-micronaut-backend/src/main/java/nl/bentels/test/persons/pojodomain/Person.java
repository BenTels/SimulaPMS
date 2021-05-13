package nl.bentels.test.persons.pojodomain;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import io.micronaut.core.annotation.Introspected;
import nl.bentels.test.LocalDateSerializer;

@Introspected
public class Person {
	
	private String id; 
	@BsonProperty("firstNames")
	private List<String> firstnames; 
	@BsonProperty("middleNames")
	private List<String> middlenames; 
	@BsonProperty("lastName")
	@NotBlank private String lastname;
	private Address mainCorrespondenceAddress; 
	private Address billingAddress; 
	@BsonProperty("emailAddresses")
	private List<String> emailaddresses;
	@BsonProperty("phoneNumbers")
	private List<PhoneNumber> phonenumbers;
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@BsonIgnore
	private LocalDate dob;

	@JsonIgnore
	@BsonProperty("dateOfBirth")
	public String getDateOfBirth() {
		if (dob != null) { 
			return dob.toString();
		} else {
			return null;
		}
	}
	
	public void setDateOfBirth(String dateOfBirth) {
		dob = LocalDate.parse(dateOfBirth);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getFirstnames() {
		return firstnames;
	}

	public void setFirstnames(List<String> firstnames) {
		this.firstnames = firstnames;
	}

	public List<String> getMiddlenames() {
		return middlenames;
	}

	public void setMiddlenames(List<String> middlenames) {
		this.middlenames = middlenames;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Address getMainCorrespondenceAddress() {
		return mainCorrespondenceAddress;
	}

	public void setMainCorrespondenceAddress(Address mainCorrespondenceAddress) {
		this.mainCorrespondenceAddress = mainCorrespondenceAddress;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public List<String> getEmailaddresses() {
		return emailaddresses;
	}

	public void setEmailaddresses(List<String> emailaddresses) {
		this.emailaddresses = emailaddresses;
	}

	public List<PhoneNumber> getPhonenumbers() {
		return phonenumbers;
	}

	public void setPhonenumbers(List<PhoneNumber> phonenumbers) {
		this.phonenumbers = phonenumbers;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
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

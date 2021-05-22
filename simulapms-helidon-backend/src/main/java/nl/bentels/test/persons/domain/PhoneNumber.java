package nl.bentels.test.persons.domain;

import javax.validation.constraints.Pattern;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class PhoneNumber {
	
	@BsonProperty("phoneNumber")
	@Pattern(regexp = "\\+?[0-9 \\-]{6,}") 
	public String number;
	public boolean mobile = true;
	
	public static PhoneNumber of(String number, boolean mobile) {
		PhoneNumber p = new PhoneNumber();
		p.number = number;
		p.mobile = mobile; 
		return p;
	}
	
	@Override
	public String toString() {
		return "PhoneNumber [number=" + number + ", mobile=" + mobile + "]";
	}
}

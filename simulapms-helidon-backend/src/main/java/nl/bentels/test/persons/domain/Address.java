package nl.bentels.test.persons.domain;

import java.util.Arrays;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;

import com.neovisionaries.i18n.CountryCode;

public class Address {
	
	@BsonProperty("addressLines")
	public List<String> lines; 
	@BsonProperty("countryCode")
	public CountryCode country;
	
	public static Address of(String[] lines, CountryCode code) {
		Address address = new Address();
		address.lines = Arrays.asList(lines != null ? lines : new String[] {});
		address.country = code;
		return address;
	}
	
	@Override
	public String toString() {
		return "Address [lines=" + lines + ", country=" + country + "]";
	}
	
}

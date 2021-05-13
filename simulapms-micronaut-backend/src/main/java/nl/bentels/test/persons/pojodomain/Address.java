package nl.bentels.test.persons.pojodomain;

import java.util.List;

import org.bson.codecs.pojo.annotations.BsonProperty;

import com.neovisionaries.i18n.CountryCode;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Address {
	
	@BsonProperty("addressLines")
	private List<String> lines; 
	@BsonProperty("countryCode")
	private CountryCode country;
	public List<String> getLines() {
		return lines;
	}
	public void setLines(List<String> lines) {
		this.lines = lines;
	}
	public CountryCode getCountry() {
		return country;
	}
	public void setCountry(CountryCode country) {
		this.country = country;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((lines == null) ? 0 : lines.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (country != other.country)
			return false;
		if (lines == null) {
			if (other.lines != null)
				return false;
		} else if (!lines.equals(other.lines))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Address [lines=" + lines + ", country=" + country + "]";
	}
	
	
}

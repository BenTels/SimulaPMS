package nl.bentels.test.persons.pojodomain;

import javax.validation.constraints.Pattern;

import org.bson.codecs.pojo.annotations.BsonProperty;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class PhoneNumber {
	
	@BsonProperty("phoneNumber")
	@Pattern(regexp = "\\+?[0-9 \\-]{6,}") 
	private String number;
	private boolean mobile;
	
	public void setNumber(String number) {
		this.number = number;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	public String getNumber() {
		return number;
	}

	public boolean isMobile() {
		return mobile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (mobile ? 1231 : 1237);
		result = prime * result + ((number == null) ? 0 : number.hashCode());
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
		PhoneNumber other = (PhoneNumber) obj;
		if (mobile != other.mobile)
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PhoneNumber [number=" + number + ", mobile=" + mobile + "]";
	}
	
	

}

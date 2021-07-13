package nl.bentels.loa.simulapms.model;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneNumber {
	
	@Field("phoneNumber")
	private String number;
	private boolean mobile;

}

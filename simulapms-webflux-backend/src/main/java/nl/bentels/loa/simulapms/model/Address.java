package nl.bentels.loa.simulapms.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import com.neovisionaries.i18n.CountryCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

	@Field("addressLines")
	private List<String> lines; 
	@Field("countryCode")
	private CountryCode country;
}

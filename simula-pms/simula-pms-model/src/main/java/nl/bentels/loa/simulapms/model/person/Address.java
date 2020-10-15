package nl.bentels.loa.simulapms.model.person;

import java.util.ArrayList;
import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {

	private final List<String> addressLines;
	private final CountryCode countryCode;
}

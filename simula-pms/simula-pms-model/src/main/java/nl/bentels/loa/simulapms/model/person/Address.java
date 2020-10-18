package nl.bentels.loa.simulapms.model.person;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.neovisionaries.i18n.CountryCode;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Address {

    private final List<String> addressLines;
    @NotNull
    private final CountryCode  countryCode;
}

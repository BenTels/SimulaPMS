package nl.bentels.loa.simulapms.personrepo.model;

import java.util.List;

import com.neovisionaries.i18n.CountryCode;

import lombok.Data;

@Data
public class Address {

    private final List<String> addressLines;
    private final CountryCode  countryCode;
}

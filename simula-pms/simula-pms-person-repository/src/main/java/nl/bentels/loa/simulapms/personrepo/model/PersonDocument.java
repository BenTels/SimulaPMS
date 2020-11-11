package nl.bentels.loa.simulapms.personrepo.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "person")
public class PersonDocument {

    private String            id;
    private List<String>      firstNames;
    private List<String>      middleNames;
    private String            lastName;
    private Address           mainCorrespondenceAddress;
    private Address           billingAddress;
    private List<String>      emailAddresses;
    private List<PhoneNumber> phoneNumbers;
}

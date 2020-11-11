package nl.bentels.loa.simulapms.personrepo;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import nl.bentels.loa.simulapms.model.person.Person;
import nl.bentels.loa.simulapms.personrepo.model.Address;
import nl.bentels.loa.simulapms.personrepo.model.PersonDocument;
import nl.bentels.loa.simulapms.personrepo.model.PhoneNumber;

@Mapper(componentModel = "spring")
public interface PersonRepositoryMapper {

    PhoneNumber toRepoPhoneNumber(nl.bentels.loa.simulapms.model.person.PhoneNumber phonenumber);

    @InheritInverseConfiguration
    nl.bentels.loa.simulapms.model.person.PhoneNumber fromRepoPhoneNumber(PhoneNumber phoneNumber);

    Address toRepoAddress(nl.bentels.loa.simulapms.model.person.Address address);

    @InheritInverseConfiguration
    nl.bentels.loa.simulapms.model.person.Address fromRepoAddress(Address address);

    PersonDocument toRepoPerson(Person person);

    @InheritInverseConfiguration
    Person fromRepoPerson(PersonDocument personDocument);
}

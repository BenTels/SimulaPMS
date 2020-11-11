package nl.bentels.loa.simulapms.personrepo;

import org.springframework.data.mongodb.repository.MongoRepository;

import nl.bentels.loa.simulapms.personrepo.model.PersonDocument;

public interface MongoPersonDocumentRepository extends MongoRepository<PersonDocument, String> {

}

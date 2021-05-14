package nl.bentels.test.persons.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.i18n.CountryCode;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.web.router.RouteBuilder.UriNamingStrategy;
import nl.bentels.test.persons.changenotification.CausesClientNotification;
import nl.bentels.test.persons.pojodomain.Address;
import nl.bentels.test.persons.pojodomain.Person;
import nl.bentels.test.persons.pojodomain.PhoneNumber;
import nl.bentels.test.persons.repository.PersonRepository;
import nl.bentels.test.persons.repository.PersonUpdateFailedException;
import nl.bentels.test.persons.repository.UnknownPersonException;

@Controller("/persons")
public class PersonsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PersonsController.class);

	private UriNamingStrategy uriNamingStrategy;
	private PersonRepository personRepository;

	public PersonsController(UriNamingStrategy uriNamingStrategy, PersonRepository personRepository) {
		this.uriNamingStrategy = uriNamingStrategy;
		this.personRepository = personRepository;
	}

	@Get("/")
	public List<Person> getAllPersons(@QueryValue(value="searchTerm", defaultValue = "") final String searchTerm) {
		return personRepository.findAll().stream().filter(p -> StringUtils.isBlank(searchTerm) || p.getLastname().toLowerCase().contains(searchTerm.trim().toLowerCase())).toList();
	}

	@Get("/{id}")
	public Optional<Person> findById(@PathVariable(name = "id") String id) {
		return personRepository.findById(id);
	}

	@Post("/")
	@CausesClientNotification
	public HttpResponse<String> processPerson(Person person, HttpRequest<?> request) {
		if (StringUtils.isNotBlank(person.getId())) {
			return HttpResponse.badRequest("A new person may not specify an ID");
		}

		try {
			Optional<String> key = personRepository.createNewPerson(person);
			return HttpResponse.accepted(URI.create(uriNamingStrategy.resolveUri(PersonsController.class) + "/" + key.get())).body("");
		} catch (Exception e) {
			LOGGER.error("Failed to insert", e);
			return HttpResponse.serverError(e.getMessage());
		}
	}

	@Put("/{id}")
	@CausesClientNotification
	public HttpResponse<String> updatePerson(Person p, String id, HttpRequest<?> request) {
		HttpResponse<String> result = HttpResponse.ok();
		if (id.equals(p.getId())) {
			try {
				personRepository.updatePerson(p);
			} catch (UnknownPersonException e) {
				result = HttpResponse.notFound();
			} catch (PersonUpdateFailedException e) {
				result = HttpResponse.serverError();
			}
		} else {
			result = HttpResponse.badRequest("Wrong person identified in update request");
		}
		return result;
	}

	@Delete("/{id}")
	@CausesClientNotification
	public void removePerson(String id, HttpRequest<?> request) {
		personRepository.removePerson(id);
	}

}

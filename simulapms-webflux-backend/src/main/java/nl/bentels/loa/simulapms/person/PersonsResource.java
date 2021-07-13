package nl.bentels.loa.simulapms.person;

import java.net.URI;
import java.util.UUID;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import nl.bentels.loa.simulapms.model.Person;
import nl.bentels.loa.simulapms.person.PersonChangeNotificationHandler.ChangeType;
import nl.bentels.loa.simulapms.repository.PersonRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@RestController
public class PersonsResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(PersonsResource.class);

	private final PersonRepository repository;
	private final Sinks.Many<String> notificationSink;

	public PersonsResource(PersonRepository repository, Many<String> notificationSink) {
		this.repository = repository;
		this.notificationSink = notificationSink;
	}

	@GetMapping(path = "/persons", produces = { "application/json" })
	public Flux<Person> getPersons(
			@RequestParam(name = "searchTerm", defaultValue = "", required = false) final String filter) {

		Predicate<Person> predicate = StringUtils.isNotBlank(filter) ? (p) -> p.getLastname().startsWith(filter)
				: (p) -> true;

		LOGGER.debug("Going to retrieve persons with filter [{}]", filter);
		return repository.findAll().filter(predicate);
	}

	@GetMapping(path = "/persons/{id}", produces = { "application/json" })
	public Mono<Person> getPerson(@PathVariable(name = "id", required = true) final String id) {
		LOGGER.debug("Going to retrieve person with id [{}]", id);
		return repository.findById(id);
	}

	@Transactional
	@PostMapping(path = "/persons", consumes = { "application/json" })
	public Mono<ResponseEntity<Person>> createPerson(@RequestBody(required = true) final Person person,
			final UriComponentsBuilder componentsBuilder) {
		if (StringUtils.isNotBlank(person.getId())) {
			throw new AlreadyIdentifiedPersonException();
		} else {
			String uuid = UUID.randomUUID().toString();
			Person p0 = person.withId(uuid);

			return repository.save(p0)
					.doOnNext((savedPerson) -> notificationSink.tryEmitNext(PersonChangeNotificationHandler.makeMessage(ChangeType.ADDED, componentsBuilder.pathSegment("persons", savedPerson.getId()).build().toUri())))
					.flatMap((savedPerson) -> Mono.just(ResponseEntity
							.created(componentsBuilder.pathSegment("persons", savedPerson.getId()).build().toUri())
							.contentType(MediaType.APPLICATION_JSON).body(savedPerson)));
		}
	}

    @Transactional
    @PutMapping(path = "/persons/{id}", consumes = { "application/json" })
    @ResponseStatus(code = HttpStatus.OK)
    public void updatePerson(@RequestBody(required = true) final Person person, @PathVariable(name = "id", required = true) final String id,
            final UriComponentsBuilder componentsBuilder) {
    	if (!id.equals(person.getId())) {
    		throw new NoSuchPersonException(id);
    	}
    	
        repository.save(person)
        .doOnNext((savedPerson) -> notificationSink.tryEmitNext(PersonChangeNotificationHandler.makeMessage(ChangeType.UPDATED, componentsBuilder.pathSegment("persons", savedPerson.getId()).build().toUri())))
        .subscribe();
    }

	@Transactional
    @DeleteMapping(path = "/persons/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void removePerson(@PathVariable(name = "id", required = true) final String id, final UriComponentsBuilder componentsBuilder) {
		Person p0 = Person.builder().id(id).lastname("placeholder").build();
    	repository.delete(p0)
    	.doOnSuccess((savedPerson) -> notificationSink.tryEmitNext(PersonChangeNotificationHandler.makeMessage(ChangeType.REMOVED, componentsBuilder.pathSegment("persons", id).build().toUri())))
    	.subscribe();
    }

}

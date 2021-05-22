package nl.bentels.test.persons.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import io.helidon.microprofile.cors.CrossOrigin;
import nl.bentels.test.persons.domain.Person;
import nl.bentels.test.persons.notifier.PersonsChangeNotifier;
import nl.bentels.test.persons.notifier.PersonsChangeNotifier.ChangeType;
import nl.bentels.test.persons.repository.PersonUpdateFailedException;
import nl.bentels.test.persons.repository.PersonsRepository;
import nl.bentels.test.persons.repository.UnknownPersonException;

@Path("/persons")
public class PersonsResource {
	
	private static final Logger LOGGER = Logger.getLogger(PersonsResource.class.getCanonicalName());
	
	private final PersonsRepository personsRepository;
	private final PersonsChangeNotifier changeNotifier;
	
	@Inject
	public PersonsResource(PersonsRepository personsRepository, PersonsChangeNotifier changeNotifier) {
		this.personsRepository = personsRepository;
		this.changeNotifier = changeNotifier;
	}

	@OPTIONS
	@CrossOrigin() 
	public void options() {}

	@OPTIONS
	@CrossOrigin() 
	@Path("/{id}")
	public void optionsById() {}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> getAllPersons(@QueryParam("searchTerm") @DefaultValue("") String searchTerm) {
		return personsRepository.retrieveAllPersons().stream().filter(p -> p.lastname.contains(searchTerm)).collect(Collectors.toList());
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSpecificPerson(@PathParam("id") String id) {
		Optional<Person> person = personsRepository.findById(id);
		if (person.isPresent()) {
			return Response.ok(person.get()).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewPerson(Person person, @Context UriInfo uriInfo) {
		if (person.id != null && !person.id.isBlank()) {
			return Response.status(Status.BAD_REQUEST).entity("A new person may not specify an ID").type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		
		try {
			Optional<String> key = personsRepository.createNewPerson(person);
			URI path = uriInfo.getAbsolutePathBuilder().path(key.get()).build();
			changeNotifier.enqueueNotification(ChangeType.ADDED, path);
			return Response.accepted(path).build();
		} catch (Exception e) {
			LOGGER.severe(String.format("Failed to insert\n%s", e.toString()));
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePerson(Person p, @PathParam("id") String id, @Context UriInfo uriInfo) {
		Response result = Response.ok().build();
		if (id.equals(p.id)) {
			try {
				personsRepository.updatePerson(p);
				changeNotifier.enqueueNotification(ChangeType.UPDATED, uriInfo.getAbsolutePath());
			} catch (UnknownPersonException e) {
				result = Response.status(Status.NOT_FOUND).build();
			} catch (PersonUpdateFailedException e) {
				result = Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			result = Response.status(Status.BAD_REQUEST).entity("Wrong person identified in update request").type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		return result;
	}

	@DELETE
	@Path("/{id}")
	public void removePerson(@PathParam("id") String id, @Context UriInfo uriInfo) {
		personsRepository.removePerson(id);
		changeNotifier.enqueueNotification(ChangeType.REMOVED, uriInfo.getAbsolutePath());
	}
}

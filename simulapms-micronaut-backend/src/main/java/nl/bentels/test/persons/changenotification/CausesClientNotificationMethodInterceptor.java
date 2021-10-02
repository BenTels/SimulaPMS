package nl.bentels.test.persons.changenotification;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;

import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.web.router.RouteBuilder.UriNamingStrategy;
import jakarta.inject.Singleton;
import nl.bentels.test.persons.changenotification.PersonChangeNotifier.ChangeType;
import nl.bentels.test.persons.controller.PersonsController;
import nl.bentels.test.persons.pojodomain.Person;

@Singleton
@InterceptorBean(CausesClientNotification.class)
public class CausesClientNotificationMethodInterceptor implements MethodInterceptor<Object, Object> {

	private final String personBaseURI;
	private final HttpHostResolver hostResolver;
	private final PersonChangeNotifier changeNotifier;

	public CausesClientNotificationMethodInterceptor(HttpHostResolver hostResolver, UriNamingStrategy uriNamingStrategy,
			PersonChangeNotifier changeNotifier) {
		this.hostResolver = hostResolver;
		this.personBaseURI = uriNamingStrategy.resolveUri(PersonsController.class);
		this.changeNotifier = changeNotifier;
	}

	@Override
	public Object intercept(MethodInvocationContext<Object, Object> context) {
		try {
			Object[] parameterValues = context.getParameterValues();
			Optional<Person> personParam = Arrays.stream(parameterValues)
					.filter(param -> Person.class.isInstance(param)).map(param -> (Person) param).findFirst();
			Optional<String> idParam = Arrays.stream(parameterValues)
					.filter(param -> String.class.isInstance(param)).map(param -> (String) param).findFirst();
			@SuppressWarnings("unchecked")
			Optional<HttpRequest<Object>> requestParam = Arrays.stream(parameterValues)
					.filter(param -> HttpRequest.class.isInstance(param)).map(param -> (HttpRequest<Object>) param).findFirst();

			Object retVal = context.proceed();

			String id = "";
			ChangeType ct = ChangeType.UPDATED;
			if (personParam.isPresent()) {
				Person p = personParam.get();
				id = p.getId();
				if (context.hasAnnotation(Post.class)) {
					ct = ChangeType.ADDED;
				}
			} else {
				id = idParam.get();
				ct = ChangeType.REMOVED;
			}
			String host = requestParam.isPresent() ? hostResolver.resolve(requestParam.get()) : "";
			URI uri = URI.create(new StringJoiner("").add(host).add(this.personBaseURI).add("/").add(id).toString());
			changeNotifier.enqueueNotification(ct, uri);
			
			return retVal;
		} catch (Throwable t) {
			throw t;
		}
	}

}

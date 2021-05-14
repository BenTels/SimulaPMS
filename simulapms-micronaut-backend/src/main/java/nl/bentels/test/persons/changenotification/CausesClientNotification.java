package nl.bentels.test.persons.changenotification;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.micronaut.aop.Around;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Around
public @interface CausesClientNotification {

}

package nl.bentels.loa.simulapms.model.person;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class PersonRepositoryAccessProvidingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonRepositoryAccessProvidingAspect.class);

    @Autowired
    private PersonRepository    repository;

    @Pointcut("execution(static * nl.bentels.loa.simulapms.model.person.Person.fromTemplate*(..)) && !within(nl.bentels.loa.simulapms.model.person.Person)")
    public void personCreation() {
    }

    @Around("personCreation()")
    public Person persistOnPersonCreation(final ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        try {
            Person newPerson = (Person) pjp.proceed(args);
            repository.create(newPerson);
            return newPerson;
        } catch (AlreadyIdentifiedPersonException aipe) {
            throw aipe;
        } catch (Throwable e) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    @Pointcut("execution(static * nl.bentels.loa.simulapms.model.person.Person.findById(java.lang.String)) && args(id)")
    public void personRetrievalPointcut(final String id) {
    }

    @Around("personRetrievalPointcut(id)")
    public Person doActualRetrieval(final String id) {
        try {
            return repository.findById(id);
        } catch (NoSuchPersonException e) {
            return null;
        }
    }

    @Pointcut("execution(static * nl.bentels.loa.simulapms.model.person.Person.findAllOfThem())")
    public void allPersonsRetrievalPointcut() {
    }

    @Around("allPersonsRetrievalPointcut()")
    public List<Person> doActualRetrieval() {
        return repository.findAll();
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model.person.Person.makeLike(..))")
    public void updatePersonPointcut() {
    }

    @Around("updatePersonPointcut()")
    public Person doActualUpdate(final ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        try {
            Person updatedPerson = (Person) pjp.proceed(args);
            repository.update(updatedPerson);
            return updatedPerson;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model.person.Person.delete())")
    public void removePersonPointcut() {
    }

    @Around("removePersonPointcut()")
    public Person doActualRemoval(final ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        try {
            Person removedPerson = (Person) pjp.proceed(args);
            LOGGER.debug("Will remove person {}", removedPerson.getId());
            repository.remove(removedPerson);
            return removedPerson;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}

package nl.bentels.loa.simulapms.model.person;

import java.util.List;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class PersonRepositoryAccessProvidingAspect {

    @Autowired
    private PersonRepository repository;

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

}

package nl.bentels.loa.simulapms.model.person;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PersonRepositoryAccessProvidingAspect {

    @Pointcut("execution(static * nl.bentels.loa.simulapms.model.person.Person.findById(java.lang.String)) && args(id)")
    public void personRetrievalPointcut(final String id) {
    }

    @Around("personRetrievalPointcut(id)")
    public Person doActualRetrieval(final String id) {
        System.out.println("In advice: id = " + id);
        return Person.builder().id(id).lastName(id).build();
    }

}

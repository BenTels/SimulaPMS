package nl.bentels.loa.simulapms.model.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class OnConstructionValidationAspect {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Pointcut("call(nl.bentels.loa.simulapms.model..new(..))")
    public void constructorWithParametersWithValidationAnnotations() {
    }

    @AfterReturning(pointcut = "constructorWithParametersWithValidationAnnotations()", returning = "retval")
    public void doTheValidation(final Object retval, final JoinPoint joinPoint) {
        Set<ConstraintViolation<Object>> violations = validator.validate(retval);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(
                    String.format("Validation constraint failed on construction of class %s: %s", joinPoint.getStaticPart().getSignature().getDeclaringType().getName(),
                            violations),
                    violations);
        }
    }
}

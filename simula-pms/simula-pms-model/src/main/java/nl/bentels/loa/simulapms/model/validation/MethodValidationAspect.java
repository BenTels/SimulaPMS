package nl.bentels.loa.simulapms.model.validation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MethodValidationAspect {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Null)(*), .. ))")
    public void methodsWithNullConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.NotNull)(*), .. ))")
    public void methodsWithNotNullConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.AssertTrue)(*), .. ))")
    public void methodsWithAssertTrueConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.AssertFalse)(*), .. ))")
    public void methodsWithAssertFalseConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Min)(*), .. ))")
    public void methodsWithMinConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Max)(*), .. ))")
    public void methodsWithMaxConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.DecimalMin)(*), .. ))")
    public void methodsWithDecimalMinConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.DecimalMax)(*), .. ))")
    public void methodsWithDecimalMaxConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Negative)(*), .. ))")
    public void methodsWithNegativeConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.NegativeOrZero)(*), .. ))")
    public void methodsWithNegativeOrZeroConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Positive)(*), .. ))")
    public void methodsWithPositiveConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.PositiveOrZero)(*), .. ))")
    public void methodsWithPositiveOrZeroConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Size)(*), .. ))")
    public void methodsWithSizeConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Digits)(*), .. ))")
    public void methodsWithDigitsConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Past)(*), .. ))")
    public void methodsWithPastConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.PastOrPresent)(*), .. ))")
    public void methodsWithPastOrPresentConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Future)(*), .. ))")
    public void methodsWithFutureConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.FutureOrPresent)(*), .. ))")
    public void methodsWithFutureOrPresentConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Pattern)(*), .. ))")
    public void methodsWithPatternConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.NotEmpty)(*), .. ))")
    public void methodsWithNotEmptyConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.NotBlank)(*), .. ))")
    public void methodsWithNotBlankConstraints() {
    }

    @Pointcut("execution(* nl.bentels.loa.simulapms.model..*(.., @(javax.validation.constraints.Email)(*), .. ))")
    public void methodsWithEmailConstraints() {
    }

    @Pointcut("methodsWithNullConstraints() || "
            + "methodsWithNotNullConstraints() || "
            + "methodsWithAssertTrueConstraints() || "
            + "methodsWithAssertFalseConstraints() || "
            + "methodsWithMinConstraints() || "
            + "methodsWithMaxConstraints() || "
            + "methodsWithDecimalMinConstraints() || "
            + "methodsWithDecimalMaxConstraints() || "
            + "methodsWithNegativeConstraints() || "
            + "methodsWithNegativeOrZeroConstraints() || "
            + "methodsWithPositiveConstraints() || "
            + "methodsWithPositiveOrZeroConstraints() ||"
            + "methodsWithSizeConstraints() ||"
            + "methodsWithDigitsConstraints() ||"
            + "methodsWithPastConstraints() ||"
            + "methodsWithPastOrPresentConstraints() ||"
            + "methodsWithFutureConstraints() ||"
            + "methodsWithFutureOrPresentConstraints() ||"
            + "methodsWithPatternConstraints() ||"
            + "methodsWithNotEmptyConstraints() ||"
            + "methodsWithNotBlankConstraints() ||"
            + "methodsWithEmailConstraints()")
    public void methodsWithParametersWithValidationAnnotations() {
    }

    @Before("methodsWithParametersWithValidationAnnotations()")
    public void doTheValidation(final JoinPoint thisJoinPoint) {
        try {
            identifyAdvisedMethodAndPerformValidation(thisJoinPoint);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Could not find correct method");
        }
    }

    private void identifyAdvisedMethodAndPerformValidation(final JoinPoint thisJoinPoint) throws NoSuchMethodException {
        Method method = findAdvisedMethodObject(thisJoinPoint);
        Set<ConstraintViolation<Object>> violations = startValidation(thisJoinPoint, method);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(
                    String.format("Constraints violated on method or constructor %s of type %s", method.getName(), getInvocationTargetObject(thisJoinPoint).getClass()),
                    violations);
        }
    }

    private Set<ConstraintViolation<Object>> startValidation(final JoinPoint thisJoinPoint, final Method method) {
        return validator.forExecutables().validateParameters(
                getInvocationTargetObject(thisJoinPoint),
                method,
                thisJoinPoint.getArgs());
    }

    private Object getInvocationTargetObject(final JoinPoint thisJoinPoint) {
        Object target = thisJoinPoint.getTarget();
        if (target == null && Modifier.isStatic(thisJoinPoint.getSignature().getModifiers())) {
            target = thisJoinPoint.getSignature().getDeclaringType();
        }
        return target;
    }

    private Method findAdvisedMethodObject(final JoinPoint thisJoinPoint) throws NoSuchMethodException {
        Class<?> advisedClass = thisJoinPoint.getSignature().getDeclaringType();
        Class<?>[] advisedMethodParameterTypes = identifyAdvisedMethodParameterTypes(thisJoinPoint);
        String advisedMethodName = thisJoinPoint.getSignature().getName();

        return tryToFindAdvisedMethodObject(advisedClass, advisedMethodParameterTypes, advisedMethodName);
    }

    private Method tryToFindAdvisedMethodObject(final Class<?> advisedClass, final Class<?>[] advisedMethodParameterTypes, final String advisedMethodName)
            throws NoSuchMethodException {
        return Arrays.stream(advisedClass.getMethods())
                .filter(exec -> advisedMethodName.equals(exec.getName()))
                .filter(exec -> matchesParameters(exec, advisedMethodParameterTypes))
                .findAny()
                .orElseThrow(NoSuchMethodException::new);
    }

    private Class<?>[] identifyAdvisedMethodParameterTypes(final JoinPoint thisJoinPoint) {
        return Arrays.stream(thisJoinPoint.getArgs())
                .map(o -> o != null ? o.getClass() : null)
                .toArray(count -> new Class<?>[count]);
    }

    private boolean matchesParameters(final Method meth, final Class<?>[] parameterTypes) {
        boolean matches = meth.getParameterCount() == parameterTypes.length;
        if (matches) {
            for (int ptr = 0; ptr < parameterTypes.length; ptr++) {
                Class<?> declaredType = meth.getParameters()[ptr].getType();
                Class<?> actualType = parameterTypes[ptr];
                matches = actualType == null || declaredType.isAssignableFrom(actualType);
            }
        }
        return matches;
    }

}

package nl.bentels.loa.simulapms.temprestfrontend;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorHandingControllerAdvice extends ResponseEntityExceptionHandler {
    private static final String ERROR_TEMPLATE = """
            {"status" : %d, "error": "%s", "message": "%s"}
            """;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleModelConstraintViolation(final ConstraintViolationException cve, final WebRequest req) {
        String message = String.format("%s",
                cve.getConstraintViolations().stream().map(cv -> cv.getPropertyPath().toString() + " " + cv.getMessage()).collect(Collectors.joining()));
        String body = String.format(ERROR_TEMPLATE, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
        return handleExceptionInternal(cve, body,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
    }
}

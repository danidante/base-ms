package com.mp.basems.infra.exception;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mp.basems.infra.validator.EnumNamePattern;
import com.mp.basems.infra.validator.NullOrNotBlank;

@RestControllerAdvice
public class MPResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private ConstraintViolationException constraintViolationException;
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@ExceptionHandler(value = MPException.class)
    public final ResponseEntity<ExceptionResponse> handleMPException(MPException mpException) {
        return new ResponseEntity(this.createExceptionResponse(mpException), mpException.getHttpStatus());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(
            ConstraintViolationException cVException) {

    	this.constraintViolationException = cVException;

    	
        final String traceLogId = UUID.randomUUID().toString();

        MPException validationException = this.validateMissingParams(traceLogId);
        if(validationException == null) {
        	validationException = this.validateInvalidParams(traceLogId);
        }
        
        return new ResponseEntity(this.createExceptionResponse(validationException), HttpStatus.BAD_REQUEST);
    }

    private MPException validateInvalidParams(String traceLogId) {
		String fieldsWithError = extractFieldsWithErrors(Min.class);

		fieldsWithError = this.concatValidation(fieldsWithError, extractFieldsWithErrors(Max.class));

		fieldsWithError = this.concatValidation(fieldsWithError, extractFieldsWithErrors(NullOrNotBlank.class));

		fieldsWithError = this.concatValidation(fieldsWithError, extractFieldsWithErrors(EnumNamePattern.class));

		fieldsWithError = this.concatValidation(fieldsWithError, extractFieldsWithErrors(Size.class));
		
		fieldsWithError = this.concatValidation(fieldsWithError, extractFieldsWithErrors(Pattern.class));
		
		fieldsWithError = this.concatValidation(fieldsWithError, extractFieldsWithErrors(Email.class));
		
		fieldsWithError = this.concatValidation(fieldsWithError, extractFieldsWithErrors(DecimalMin.class));
		
		fieldsWithError = this.concatValidation(fieldsWithError, extractFieldsWithErrors(DecimalMax.class));
		
		fieldsWithError = this.concatValidation(fieldsWithError, extractFieldsWithErrors(PositiveOrZero.class));

		return fieldsWithError.isEmpty() 
        		? null
        		: new InvalidParamException(fieldsWithError, traceLogId);
    }
    
    private MPException validateMissingParams(String traceLogId) {
    	String fieldsWithError = this.concatValidation(extractFieldsWithErrors(NotBlank.class), extractFieldsWithErrors(NotNull.class));
        
        return fieldsWithError.isEmpty() 
        		? null
        		: new MissingParamException(fieldsWithError, traceLogId);
    }
    
    private ExceptionResponse createExceptionResponse(MPException mpException) {

    	return mpException != null 
    			? ExceptionResponse.builder().code(mpException.getExceptionCode())
    					.type(mpException.getExceptionType()).traceLogId(mpException.getExceptionTraceLogId())
    					.description(mpException.getExceptionDescription()).build()
                : new ExceptionResponse();
    }

    private String extractFieldsWithErrors(Class<?> validationClass) {

        return this.constraintViolationException.getConstraintViolations()
        		.stream()
                .filter(cv -> cv.getConstraintDescriptor().getAnnotation().annotationType().equals(validationClass))
                .map(cv -> StreamSupport.stream(cv.getPropertyPath().spliterator(), false)
						.reduce((a, b) -> b).get().getName()
						.concat(" - ")
						.concat(cv.getMessage()))
                .collect(Collectors.joining(", "));
    }

    private String concatValidation(String fieldsWithError, String validation) {
    	return validation.isEmpty() 
				? fieldsWithError
				: this.concatFieldsError(fieldsWithError, validation);
    }
    
    private String concatFieldsError(String fieldsWithError, String validation) {
    	return fieldsWithError.isEmpty() 
    			? fieldsWithError.concat(validation)
    			: fieldsWithError.concat(", ").concat(validation);
    }
    
}

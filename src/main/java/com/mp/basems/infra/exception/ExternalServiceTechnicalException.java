package com.mp.basems.infra.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class ExternalServiceTechnicalException extends MPException {

    private static final long serialVersionUID = 6177387947132560967L;
    
    public static final String EXCEPTION_BASE_DESCRIPTION = "External service error - Technical: ";
    
    public ExternalServiceTechnicalException(String errorMessage) {
    	super(EXCEPTION_BASE_DESCRIPTION.concat(errorMessage), MPExceptionCodes.EXTERNAL_SERVICE_UNAVAILABLE, 
    			EXCEPTION_BASE_DESCRIPTION.concat(errorMessage), UUID.randomUUID().toString(), HttpStatus.resolve(500));
    }

    public ExternalServiceTechnicalException(HttpStatus httpStatus, String errorMessage) {
    	super(EXCEPTION_BASE_DESCRIPTION.concat(errorMessage), MPExceptionCodes.EXTERNAL_SERVICE_UNAVAILABLE, 
    			EXCEPTION_BASE_DESCRIPTION.concat(errorMessage), UUID.randomUUID().toString(), httpStatus);
    }

}

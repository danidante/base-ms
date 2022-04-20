package com.mp.basems.infra.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;

public class ExternalServiceRequestException extends MPException {

    private static final long serialVersionUID = -3629044614075166746L;

    public static final String EXCEPTION_BASE_DESCRIPTION = "External service error - Request: ";
    
    public ExternalServiceRequestException(String errorMessage) {
    	super(EXCEPTION_BASE_DESCRIPTION.concat(errorMessage), MPExceptionCodes.EXTERNAL_SERVICE_UNAVAILABLE, 
    			EXCEPTION_BASE_DESCRIPTION.concat(errorMessage), UUID.randomUUID().toString(), HttpStatus.resolve(500));
    }

    public ExternalServiceRequestException(HttpStatus httpStatus, String errorMessage) {
    	super(EXCEPTION_BASE_DESCRIPTION.concat(errorMessage), MPExceptionCodes.EXTERNAL_SERVICE_UNAVAILABLE, 
    			EXCEPTION_BASE_DESCRIPTION.concat(errorMessage), UUID.randomUUID().toString(), httpStatus);
    }

}

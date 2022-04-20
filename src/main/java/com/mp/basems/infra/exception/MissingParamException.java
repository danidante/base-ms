package com.mp.basems.infra.exception;

import org.springframework.http.HttpStatus;

public class MissingParamException extends MPException {

	private static final long serialVersionUID = 1L;
    
	public static final String EXCEPTION_BASE_DESCRIPTION = "The following parameters are missing: ";

	public MissingParamException(String descriptionToAdd, String traceLogId) {
		super(EXCEPTION_BASE_DESCRIPTION.concat(descriptionToAdd), MPExceptionCodes.MISSING_PARAM, 
				EXCEPTION_BASE_DESCRIPTION.concat(descriptionToAdd), traceLogId, HttpStatus.BAD_REQUEST);
	}

}

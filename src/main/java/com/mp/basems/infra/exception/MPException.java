package com.mp.basems.infra.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class MPException extends RuntimeException {

	private static final long serialVersionUID = -3900016879874296087L;
	private static final String UNEXPECTED_INTERNAL_ERROR = "Unexpected Internal Error. ";
	
	protected int exceptionCode;
	protected String exceptionType;
	protected String exceptionDescription;
	protected String exceptionTraceLogId;
	protected HttpStatus httpStatus;
	
	/**
	 * Release an Internal Server Error Exception (HttpStatus 500).
	 * 
	 * @param message
	 * @param exceptionTraceLogId
	 */
	public MPException(String message, String exceptionTraceLogId) {
		super(UNEXPECTED_INTERNAL_ERROR + exceptionTraceLogId);
		this.exceptionCode = MPExceptionCodes.INTERNAL_SERVER_ERROR.getCode();
		this.exceptionType = MPExceptionCodes.INTERNAL_SERVER_ERROR.getType();
		this.exceptionDescription = UNEXPECTED_INTERNAL_ERROR + message;
		this.exceptionTraceLogId = exceptionTraceLogId;
		this.httpStatus= HttpStatus.INTERNAL_SERVER_ERROR;
	}
	
	public MPException(String message, MPExceptionCodes mPExceptionCodes, String exceptionDescription,
			String exceptionTraceLogId,HttpStatus httpStatus) {
		super(message);
		this.exceptionCode = mPExceptionCodes.getCode();
		this.exceptionType = mPExceptionCodes.getType();
		this.exceptionDescription = exceptionDescription;
		this.exceptionTraceLogId = exceptionTraceLogId;
		this.httpStatus= httpStatus;
	}

	public MPException(String message, int exceptionCode, String exceptionType, String exceptionDescription,
					   String exceptionTraceLogId,HttpStatus httpStatus) {
		super(message);
		this.exceptionCode = exceptionCode;
		this.exceptionType = exceptionType;
		this.exceptionDescription = exceptionDescription;
		this.exceptionTraceLogId = exceptionTraceLogId;
		this.httpStatus= httpStatus;
	}
}

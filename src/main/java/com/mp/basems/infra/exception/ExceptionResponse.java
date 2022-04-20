package com.mp.basems.infra.exception;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@Data
public class ExceptionResponse {

    private Integer code;
    private String type;
    private String description;
    private String traceLogId;
    
    public static ExceptionResponse parse(String source) {
    	try {
    		return new ObjectMapper().readValue(source, ExceptionResponse.class);
    	}
    	catch (IOException e) {
    		throw new MPException("Error on ExceptionResponse", MPExceptionCodes.INTERNAL_SERVER_ERROR, "Error on ExceptionResponse", 
					UUID.randomUUID().toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
}
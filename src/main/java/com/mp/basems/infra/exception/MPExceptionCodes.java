package com.mp.basems.infra.exception;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public enum MPExceptionCodes {

	MISSING_PARAM(1, "MISSING_PARAM"), 
	INVALID_PARAM(2, "INVALID_PARAM"),
	ENTITY_NOT_FOUND(3, "ENTITY_NOT_FOUND"),
	TRANSACTION_NOT_FOUND(4, "TRANSACTION_NOT_FOUND"),
	EXTERNAL_SERVICE_UNAVAILABLE(5, "EXTERNAL_SERVICE_UNAVAILABLE"),
	INTERNAL_SERVER_ERROR(6, "INTERNAL_SERVER_ERROR");
	
	private Integer code;
	private String type;
	private static List<MPExceptionCodes> list;
	
	static {
		list = Arrays.asList(MPExceptionCodes.values());
	}
	
	private MPExceptionCodes(Integer code, String type) {
		this.code = code;
		this.type = type;
	}

	public static MPExceptionCodes getByCode(int code) {
		return list.stream().filter(ec -> code == ec.getCode()).findFirst()
				.orElseThrow(() -> new MPException("Invalid Exception Code", UUID.randomUUID().toString()));
	}
	
	public static MPExceptionCodes getByType(String type) {
		return list.stream().filter(ec -> type.equals(ec.getType())).findFirst()
				.orElseThrow(() -> new MPException("Invalid Exception Type", UUID.randomUUID().toString()));
	}
}

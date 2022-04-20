package com.mp.basems.infra.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.mp.basems.infra.exception.ExceptionResponse;
import com.mp.basems.infra.exception.ExternalServiceRequestException;
import com.mp.basems.infra.exception.ExternalServiceTechnicalException;
import com.mp.basems.infra.exception.MPException;
import com.mp.basems.infra.exception.MPExceptionCodes;
import com.mp.basems.infra.exception.NoInstanceAvailableException;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

@Log4j2
public class ExternalService {

	private DiscoveryService discoveryService;
    private WebClient webClient;
    private TokenService tokenService;
    
    public ExternalService(DiscoveryService discoveryService, WebClient webClient, TokenService tokenService) {
        this.discoveryService = discoveryService;
        this.webClient = webClient;
        this.tokenService = tokenService;
    }

    protected Mono<?> post(String externalService, String endpointUrl, Class<?> responseClass, Object requestObject) {
        return discoveryService.serviceAddressFor(externalService)
                .next()
                .flatMap(address -> Mono.just(this.webClient.mutate()
                        .baseUrl(address + endpointUrl)
                        .build()
                        .post()
                        .body(BodyInserters.fromObject(requestObject))))
                .flatMap(requestHeadersUriSpec -> Flux.combineLatest(
                        Flux.just(requestHeadersUriSpec),
                        Flux.from(this.tokenService.token()),
                        (reqSpec, token) -> {
                            reqSpec.header("Authorization", "Bearer" + token.getAccessToken());
                            return reqSpec;
                        }).next())
                .map(RequestHeadersSpec::retrieve)
                .retryWhen(Retry
                        .onlyIf(context -> context.exception() instanceof NoInstanceAvailableException)
                        .fixedBackoff(Duration.ofSeconds(5))
                        .retryMax(4))
                .map(respSpec -> this.evaluateResponseSpec(respSpec, endpointUrl))
                .flatMap(eq -> eq.bodyToMono(responseClass));
    }
    
    protected Mono<?> get(String externalService, String endpointUrl, Class<?> responseClass) {
        return discoveryService.serviceAddressFor(externalService)
                .next()
                .flatMap(address -> Mono.just(this.webClient.mutate()
                        .baseUrl(address + endpointUrl)
                        .build()
                        .get()))
                .flatMap(requestHeadersUriSpec -> Flux.combineLatest(
                        Flux.just(requestHeadersUriSpec),
                        Flux.from(tokenService.token()),
                        (reqSpec, token) -> {
                            reqSpec.header("Authorization", "Bearer" + token.getAccessToken());
                            return reqSpec;
                        }).next())
                .map(RequestHeadersSpec::retrieve)
                .retryWhen(Retry
                        .onlyIf(context -> context.exception() instanceof NoInstanceAvailableException)
                        .fixedBackoff(Duration.ofSeconds(5))
                        .retryMax(4))
                .map(respSpec -> this.evaluateResponseSpec(respSpec, endpointUrl))
                .flatMap(eq -> eq.bodyToMono(responseClass));
    }
    
    protected Mono<?> put(String externalService, String endpointUrl, Class<?> responseClass, Object requestObject) {
        return discoveryService.serviceAddressFor(externalService)
                .next()
                .flatMap(address -> Mono.just(this.webClient.mutate()
                        .baseUrl(address + endpointUrl)
                        .build()
                        .put()
                        .body(BodyInserters.fromObject(requestObject))))
                .flatMap(requestHeadersUriSpec -> Flux.combineLatest(
                        Flux.just(requestHeadersUriSpec),
                        Flux.from(this.tokenService.token()),
                        (reqSpec, token) -> {
                            reqSpec.header("Authorization", "Bearer" + token.getAccessToken());
                            return reqSpec;
                        }).next())
                .map(RequestHeadersSpec::retrieve)
                .retryWhen(Retry
                        .onlyIf(context -> context.exception() instanceof NoInstanceAvailableException)
                        .fixedBackoff(Duration.ofSeconds(5))
                        .retryMax(4))
                .map(respSpec -> this.evaluateResponseSpec(respSpec, endpointUrl))
                .flatMap(eq -> eq.bodyToMono(responseClass));
    }
    
    private ResponseSpec evaluateResponseSpec(ResponseSpec respSpec, String endpointUrl) {
    	return respSpec.onStatus(HttpStatus::is4xxClientError, this.raiseUpException(ExternalServiceRequestException.class, endpointUrl, getClass().getName()))
                .onStatus(HttpStatus::is5xxServerError, this.raiseUpException(ExternalServiceTechnicalException.class, endpointUrl, getClass().getName()));
    }
    
    private Function<ClientResponse, Mono<? extends Throwable>> raiseUpException(Class<?> clazz, String endpointUrl, String serviceName) {
        return clientResponse -> clientResponse.bodyToMono(String.class)
                .map(bodyError -> this.callExceptionConstructor(serviceName, clazz.getName(), endpointUrl, clientResponse.statusCode(), bodyError));
    }

    private MPException callExceptionConstructor(String serviceName, String className, String endpointUrl, HttpStatus status, String message) {
    	String exceptionDetail = "[Error on " + serviceName + " - " + endpointUrl + "]: -> " + message;
    	ExceptionResponse exceptionResponse = ExceptionResponse.parse(message);
    	
    	Constructor<?> exceptionClassConstructor;
    	try {
        	exceptionClassConstructor = Class.forName(className).getConstructor(HttpStatus.class, String.class);
        	
        	return status.is4xxClientError()
        		? new MPException(exceptionResponse.getDescription(), MPExceptionCodes.getByCode(exceptionResponse.getCode()),
        				exceptionResponse.getDescription(), exceptionResponse.getTraceLogId(), status)
        		: (MPException) exceptionClassConstructor.newInstance(status, exceptionDetail);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        	return new MPException(message, MPExceptionCodes.EXTERNAL_SERVICE_UNAVAILABLE, message, exceptionResponse.getTraceLogId(), status);
        } finally {
        	log.warn(new StringBuilder("traceLogId ").append(exceptionResponse.getTraceLogId()).append(" - ").append(exceptionDetail));
		}
    }
    
}

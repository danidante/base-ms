package com.mp.basems.infra.resource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.annotation.JsonView;
import com.mp.basems.infra.dto.CreateRequestObject;
import com.mp.basems.infra.dto.ResponseObject;
import com.mp.basems.infra.dto.UpdateRequestObject;
import com.mp.basems.infra.event.PublisherEvent;
import com.mp.basems.infra.jackson.Views;
import com.mp.basems.infra.model.MPEntity;
import com.mp.basems.infra.service.CommandUCService;

import reactor.core.publisher.Mono;

/**
 * 
 * @author danie
 *
 * @param <Q> - create request
 * @param <UR> - update request
 * @param <S> - response
 * @param <M> - model
 */
public abstract class CommandUCResource<Q extends CreateRequestObject, UR extends UpdateRequestObject, S extends ResponseObject<M>, M extends MPEntity<Q,UR,?>, CE extends PublisherEvent<S>, UE extends PublisherEvent<S>> {

    private Constructor<S> responseConstructor;
    protected CommandUCService<M, Q, UR, S, CE, UE> commandService;

    public CommandUCResource(CommandUCService<M, Q, UR, S, CE, UE> commandService, Class<S> responseClass, Class<M> modelClass) throws NoSuchMethodException, SecurityException {
        this.commandService = commandService;
        this.responseConstructor = responseClass.getConstructor(modelClass);
    }

    @JsonView(Views.Public.class)
    @PostMapping
    public Mono<ResponseEntity<S>> create(@RequestBody @Valid Q request, BindingResult bindingResult) {
        return this.commandService.create(request)
                .map(object -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(this.setResponse(object)));
    }

    @JsonView(Views.Public.class)
    @PutMapping("/{id}")
    public Mono<ResponseEntity<S>> update(@PathVariable String id, @RequestBody @Valid UR request, BindingResult bindingResult) {
        return Mono.just(this.setIdToRequest(id, request))
                .flatMap(updateRequest -> this.commandService.update(request)
                        .map(object -> ResponseEntity.ok(this.setResponse(object))));
    }

    protected S setResponse(M model) {
        try {
           return (S) this.responseConstructor.newInstance(model);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected UR setIdToRequest(String id, UR request) {
        return (UR) request.setId(id);
    }
}

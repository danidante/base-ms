package com.mp.basems.infra.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.reactivestreams.Publisher;

import com.mp.basems.infra.dto.ResponseObject;
import com.mp.basems.infra.event.Event;
import com.mp.basems.infra.event.PublisherEvent;
import com.mp.basems.infra.model.MPEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * @author danie
 *
 * @param <M>
 *            - model
 * @param <R>
 *            - request
 * @param <S>
 *            - response
 * @param <E>
 *            - event
 */
public abstract class Command<M extends MPEntity<?,?,?>, R, S extends ResponseObject<M>, E extends PublisherEvent<S>> implements ICommand<M, R> {

    protected E event;
    private Constructor<S> responseConstructor;
    
    public Command(E event, Class<S> responseClass, Class<M> modelClass) throws NoSuchMethodException, SecurityException {
        this.event = event;
        this.responseConstructor = responseClass.getConstructor(modelClass);
    }

    public Mono<M> execute(R request) {
        return ((Mono<M>) this.commandLogicExecution(request))
                .flatMap(result -> this.event.publish(this.setEvent(result))
                        .next()
                        .flatMap(response -> Mono.just(result)));
    }

    public Flux<M> executeFlux(R request) {
        return ((Flux<M>) this.commandLogicExecution(request))
                .flatMap(result -> this.event.publish(this.setEvent(result))
                        .flatMap(response -> Mono.just(result)));
    }

    protected Event<S> setEvent(M model) {
        try {
            return new Event<S>((S) this.responseConstructor.newInstance(model), "ENTITY", model.getId(), model.getMarketplaceId());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract Publisher<M> commandLogicExecution(R request);

}

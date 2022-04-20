package com.mp.basems.infra.command;

import org.reactivestreams.Publisher;

import com.mp.basems.infra.event.Event;
import com.mp.basems.infra.event.PublisherEvent;

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
public abstract class GenericCommand<M, R, S, E extends PublisherEvent<S>> implements ICommand<M, R> {

    protected E event;
    
    public GenericCommand(E event) {
        this.event = event;
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

    protected abstract Event<S> setEvent(M model); 
    protected abstract Publisher<M> commandLogicExecution(R request);

}

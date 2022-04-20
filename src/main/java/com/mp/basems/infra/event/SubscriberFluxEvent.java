package com.mp.basems.infra.event;

import reactor.core.publisher.Flux;

public abstract class SubscriberFluxEvent<M, S> {

    private PublisherEvent<S> eventPublisher;

    public SubscriberFluxEvent(PublisherEvent<S> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected void receiveMessage(S eventResponse) {
        this.executeSideEffectEvent(eventResponse)
                .doOnError(error -> this.eventPublisher.publish(this.setErrorEvent(eventResponse)).log("Error on subscriber event " + error).subscribe())
                .subscribe();
    }

    protected Event<S> setErrorEvent(S response) {
        return new Event<S>((S) response, "ENTITY", "", "");
    }
    
    protected abstract Flux<M> executeSideEffectEvent(S eventResponse);

}

package com.mp.basems.infra.event;

import reactor.core.publisher.Mono;

public abstract class SubscriberMonoEvent<M, S> {

    private PublisherEvent<S> eventPublisher;

    public SubscriberMonoEvent(PublisherEvent<S> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected void receiveMessage(S eventResponse) {
        this.executeSideEffectEvent(eventResponse)
                .doOnError(error -> this.eventPublisher.publish(this.setErrorEvent(eventResponse)).log("Subscribe exception " + error).subscribe())
                .subscribe();
    }

    protected Event<S> setErrorEvent(S response) {
        return new Event<S>((S) response, "ENTITY", "", "");
    }

    protected abstract Mono<M> executeSideEffectEvent(S eventResponse);

}

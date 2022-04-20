package com.mp.basems.infra.event;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.lang.NonNull;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public abstract class PublisherEvent<S> {

    private EventStorePublisher eventStore;
    private MessageChannel channel;
    private String type;

    public PublisherEvent(EventStorePublisher eventStore, MessageChannel channel, String type) {
        this.eventStore = eventStore;
        this.channel = channel;
        this.type = type;
    }

    public Flux<S> publish(@NonNull Event<S> event) {
        return Flux.defer(() -> Flux.just(this.channel.send(MessageBuilder.withPayload(event.getObject()).build()))
                .flatMap(payload -> this.eventStore.publish(new EventStore(event, this.type))
                        .flatMap(eventStore -> {
                            return Mono.just(event.getObject());
                        })));
    }
}

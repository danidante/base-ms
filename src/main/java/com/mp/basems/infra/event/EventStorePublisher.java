package com.mp.basems.infra.event;


import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import com.mp.basems.infra.channel.EventSourceChannel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@EnableBinding(EventSourceChannel.class)
public class EventStorePublisher {

    private EventSourceChannel sourceChannel;

    public EventStorePublisher(EventSourceChannel sourceChannel) {       
        this.sourceChannel = sourceChannel;
    }
    
    public Flux<EventStore> publish(EventStore object) {
        return Flux.defer(() -> Flux.just(this.getMessageChannel().send(MessageBuilder.withPayload(object).build()))
                .flatMap(payload -> Mono.just(object)));        
    }    

    protected MessageChannel getMessageChannel() {
        return this.sourceChannel.eventStoreCreated();
    }
}

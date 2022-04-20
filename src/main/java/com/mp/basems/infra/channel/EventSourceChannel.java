package com.mp.basems.infra.channel;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EventSourceChannel {

    String EVENT_STORE_CREATED = "event-store-created";

    @Output(EventSourceChannel.EVENT_STORE_CREATED)
    MessageChannel eventStoreCreated();

}

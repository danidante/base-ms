package com.mp.basems.infra.service;

import com.mp.basems.infra.command.CreateCommand;
import com.mp.basems.infra.dto.CreateRequestObject;
import com.mp.basems.infra.dto.ResponseObject;
import com.mp.basems.infra.event.PublisherEvent;
import com.mp.basems.infra.model.MPEntity;

import reactor.core.publisher.Mono;

public class CommandCService<M extends MPEntity<Q,?,?>, Q extends CreateRequestObject, S extends ResponseObject<M>, E extends PublisherEvent<S>> {

    private CreateCommand<M, Q, S, E> createCommand;

    public CommandCService(CreateCommand<M, Q, S, E> createCommand) {
        this.createCommand = createCommand;
    }

    public Mono<M> create(Q request) {
        return this.createCommand.execute(request);
    }
}

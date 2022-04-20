package com.mp.basems.infra.command;

import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.mp.basems.infra.dto.ResponseObject;
import com.mp.basems.infra.event.PublisherEvent;
import com.mp.basems.infra.model.MPEntity;

import reactor.core.publisher.Mono;

public class DeleteCommand<M extends MPEntity<?,?,?>, S extends ResponseObject<M>, E extends PublisherEvent<S>> extends Command<M, String, S, PublisherEvent<S>> {

    protected ReactiveCrudRepository<M, String> repository;

    public DeleteCommand(PublisherEvent<S> event, Class<S> responseClass, Class<M> modelClass, ReactiveCrudRepository<M, String> repository) throws NoSuchMethodException, SecurityException {
        super(event, responseClass, modelClass);
        this.repository = repository;
    }

    @Override
    protected Publisher<M> commandLogicExecution(String id) {
        return this.repository.findById(id)
                .switchIfEmpty(Mono.empty())
                .flatMap(model -> this.repository.deleteById((id))
                        .map(deleted -> model));
    }

}

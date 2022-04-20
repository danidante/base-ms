package com.mp.basems.infra.command;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.mp.basems.infra.dto.ResponseObject;
import com.mp.basems.infra.dto.UpdateRequestObject;
import com.mp.basems.infra.event.PublisherEvent;
import com.mp.basems.infra.model.MPEntity;

import reactor.core.publisher.Mono;

public class UpdateCommand<M extends MPEntity<?, UR, ?>, UR extends UpdateRequestObject, S extends ResponseObject<M>, E extends PublisherEvent<S>> extends Command<M, UR, S, PublisherEvent<S>> {

    protected ReactiveCrudRepository<M, String> repository;

    public UpdateCommand(PublisherEvent<S> event, Class<S> responseClass, Class<M> modelClass, ReactiveCrudRepository<M, String> repository) throws NoSuchMethodException, SecurityException {
        super(event, responseClass, modelClass);
        this.repository = repository;
    }

    @Override
    protected Mono<M> commandLogicExecution(UR request) {
        return this.repository.findById(request.getId())
                .flatMap(model -> (Mono<M>) Mono.just(model.updateModel(request))
                        .flatMap(finalModel -> this.beforeSave(model, request)
                                .flatMap(updatedModel -> this.repository.save((M) updatedModel))));
    }

    protected Mono<M> beforeSave(M model, UR request) {
        return Mono.just(model);
    }

}

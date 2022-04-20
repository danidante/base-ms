package com.mp.basems.infra.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.mp.basems.infra.dto.CreateRequestObject;
import com.mp.basems.infra.dto.ResponseObject;
import com.mp.basems.infra.event.PublisherEvent;
import com.mp.basems.infra.model.MPEntity;
import com.mp.basems.infra.oauth.OAuth2TokenInfoExtractor;

import reactor.core.publisher.Mono;

public class CreateCommand<M extends MPEntity<R, ?, ?>, R extends CreateRequestObject, S extends ResponseObject<M>, E extends PublisherEvent<S>> extends Command<M, R, S, PublisherEvent<S>> {

    private OAuth2TokenInfoExtractor tokenInfoExtractor;
    private Constructor<M> modelConstructor;
    protected ReactiveCrudRepository<M, String> repository;

    public CreateCommand(PublisherEvent<S> event, Class<R> createRequestClass, Class<S> responseClass, Class<M> modelClass, ReactiveCrudRepository<M, String> repository,
            OAuth2TokenInfoExtractor tokenInfoExtractor) throws NoSuchMethodException, SecurityException {
        super(event, responseClass, modelClass);
        this.modelConstructor = modelClass.getConstructor(String.class, createRequestClass);
        this.repository = repository;
        this.tokenInfoExtractor = tokenInfoExtractor;
    }

    @Override
    protected Publisher<M> commandLogicExecution(R request) {
        return (Publisher<M>) this.tokenInfoExtractor.getUserTokenData()
                .flatMap(data -> {
                    try {
                        return Mono.just(this.modelConstructor.newInstance(data.getMarketplaceId(), request))
                                .flatMap(model -> this.beforeSave(model, request)
                                        .flatMap(finalModel -> this.repository.save((M) finalModel)));
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    protected Mono<M> beforeSave(M model, R request) {
        return Mono.just(model);
    }

}

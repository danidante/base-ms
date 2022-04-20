package com.mp.basems.infra.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.mp.basems.infra.model.MPEntity;
import com.mp.basems.infra.oauth.OAuth2TokenInfoExtractor;
import com.mp.basems.infra.resource.QueryObject;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class QueryService<Q extends QueryObject<?>, M extends MPEntity<?,?,Q>, R extends ReactiveCrudRepository<M, String>> {

    protected R repository;
    protected Class<M> typeParameterClass;
    protected ReactiveMongoTemplate template;
    private Constructor<M> modelConstructor;
    protected OAuth2TokenInfoExtractor tokenInfoExtractor;

    public QueryService(ReactiveMongoTemplate template, R repository, Class<M> modelClass, Class<Q> queryClass, OAuth2TokenInfoExtractor tokenInfoExtractor) throws NoSuchMethodException, SecurityException {
        this.template = template;
        this.repository = repository;
        this.modelConstructor = modelClass.getConstructor(String.class, queryClass);
        this.typeParameterClass = modelClass;
        this.tokenInfoExtractor = tokenInfoExtractor;
    }

    public Mono<Long> countByQuery(Query query) {
        return this.template.count(query, this.typeParameterClass);
    }

    public Flux<M> getFilteredModel(Query query) {
        return template.find(query, this.typeParameterClass);
    }

    public Mono<M> getById(String id) {
        return this.repository.findById(id);
    }

    public Mono<Query> buildQueryParameters(final QueryObject<Q> queryObject, PageRequest pageRequest) {
        return this.tokenInfoExtractor.getUserTokenData()
                .flatMap(userTokenData -> Mono.just(new Query()
                        .addCriteria(Criteria.byExample(Example.of(this.setExampleObject(userTokenData.getMarketplaceId(), queryObject))))
                        .with(pageRequest)));
    }

    @SuppressWarnings("unchecked")
    protected MPEntity<?,?,Q> setExampleObject(String marketplaceId, QueryObject<Q> queryObject) {
        try {
            return (MPEntity<?,?,Q>) this.modelConstructor.newInstance(marketplaceId, queryObject).setModelFromQuery((Q) queryObject);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}

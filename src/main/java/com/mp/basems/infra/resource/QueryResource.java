package com.mp.basems.infra.resource;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mp.basems.infra.dto.ResponseObject;
import com.mp.basems.infra.jackson.Views;
import com.mp.basems.infra.model.MPEntity;
import com.mp.basems.infra.search.Pages;
import com.mp.basems.infra.search.PaginationService;
import com.mp.basems.infra.search.ResponseList;
import com.mp.basems.infra.service.QueryService;

import reactor.core.publisher.Mono;

/**
 * Generic class that implements get all (with query params and pagination) + get by id methods.
 * 
 * @author danie
 * @param <Q>
 *            - query model
 * @param <M>
 *            - model
 * @param <S>
 *            - response
 * @param <R>
 *            - repository
 */
public abstract class QueryResource<Q extends QueryObject<?>, M extends MPEntity<?,?,Q>, S extends ResponseObject<M>, R extends ReactiveCrudRepository<M, String>> {

    private PaginationService<S> paginationService;
    private QueryService<Q, M, R> queryService;
    private QueryObject<Q> queryObject;
    private Constructor<S> responseConstructor;

    public QueryResource(QueryService<Q, M, R> queryService, PaginationService<S> paginationService, QueryObject<Q> queryObject, Class<S> responseClass, Class<M> modelClass) throws NoSuchMethodException, SecurityException {
        this.queryService = queryService;
        this.paginationService = paginationService;
        this.queryObject = queryObject;
        this.responseConstructor = responseClass.getConstructor(modelClass);
    }

    @SuppressWarnings("unchecked")
    @JsonView(Views.Public.class)
    @GetMapping
    public Mono<ResponseEntity<ResponseList<S>>> get(
            @RequestParam(value = "startDate", required = false, defaultValue = "2018-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false, defaultValue = "today") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int size,
            @RequestParam Map<String, Object> map) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {

        int pageNumber = page <= 0 ? 0 : page - 1;

        PageRequest pageReq = PageRequest.of(pageNumber, size);

        this.queryObject = (QueryObject<Q>) this.queryObject.setObject(map);

        return Mono.just(new ResponseList<S>())
                .flatMap(responseList -> this.queryService.buildQueryParameters(this.queryObject, pageReq)
                        .flatMap(builtQuery -> Mono.just(this.buildDateRange(builtQuery, startDate, endDate))
                                .flatMap(query -> this.queryService.countByQuery(query)
                                        .flatMap(count -> this.queryService.getFilteredModel(query)
                                                .flatMap(model -> Mono.just(this.setResponse(model)))
                                                .collectList()
                                                .flatMap(items -> this.paginationService.buildPagination((List<S>) items, count, pageReq)
                                                        .flatMap(pageable -> Mono.just(responseList.setPaginationValues(new Pages(pageable), (List<S>) items))))
                                                .map(itemsObject -> itemsObject.getItems().size() == 0
                                                        ? ResponseEntity.notFound().build()
                                                        : ResponseEntity.status(itemsObject.getPages().getPageNumber() == itemsObject.getPages().getTotalPage()
                                                                ? HttpStatus.OK
                                                                : HttpStatus.PARTIAL_CONTENT)
                                                                .headers(this.paginationService.buildHeaders(pageReq, itemsObject))
                                                                .body(itemsObject))))));
    }

    @JsonView(Views.Public.class)
    @GetMapping("/{id}")
    public Mono<ResponseEntity<S>> get(@PathVariable String id) {
        return this.queryService.getById(id)
                .map(item -> this.setResponse(item))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<S>(HttpStatus.NOT_FOUND));
    }

    private Query buildDateRange(Query query, LocalDate startDate, LocalDate endDate) {
        return query.addCriteria(Criteria.where("creationDate")
                .gte(startDate.atStartOfDay())
                .lte(endDate.atTime(LocalTime.MAX)));
    }

    protected S setResponse(M model) {
        try {
            return (S) this.responseConstructor.newInstance(model);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.mp.basems.infra.command;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICommand<M, R> {

    Mono<M> execute(R request);
    Flux<M> executeFlux(R request);
}

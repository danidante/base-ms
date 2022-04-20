package com.mp.basems.infra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;

import com.mp.basems.infra.exception.NoInstanceAvailableException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DiscoveryService {

    @Autowired
    private LoadBalancerClient loadBalanceClient;
    @Autowired
    private DiscoveryClient discoveryClient;

    public Flux<String> serviceAddressFor(String service) {
        return Flux.defer(() -> Flux.just(this.discoveryClient.getInstances(service))
                .flatMap(srv -> Mono.just(this.loadBalanceClient.choose(service)))
                .onErrorMap(error -> new NoInstanceAvailableException("No instance of " + service + " available"))
                .flatMap(serviceInstance -> Mono.just(serviceInstance.getUri().toString())));
    }

}

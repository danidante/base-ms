package com.mp.basems.infra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.mp.basems.infra.oauth.GraviteeTokenResponse;

import reactor.core.publisher.Mono;

@Service
public class TokenService {

    @Autowired
    private WebClient webClient;
    @Value("${gravitee.url.access_token}")
    private String addressAccessToken;
    @Autowired
    private MultiValueMap<String, String> params;

    public Mono<GraviteeTokenResponse> token() {
        return this.webClient
                .mutate()
                .baseUrl(this.addressAccessToken)
                .build()
                .post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromFormData(this.params))
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> Mono.error(new RuntimeException("Error on server")))
                .bodyToMono(GraviteeTokenResponse.class);
    }

}

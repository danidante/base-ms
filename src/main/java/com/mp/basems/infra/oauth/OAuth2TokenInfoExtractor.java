package com.mp.basems.infra.oauth;

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class OAuth2TokenInfoExtractor {
    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @SuppressWarnings({ "unchecked" })
    public Mono<UserTokenData> getUserTokenData() {
        if(oauth2ClientContext.getAccessToken() == null) {
            return Mono.just(new UserTokenData());
        }
        
        String accessToken = oauth2ClientContext.getAccessToken().getValue();
        Jwt jwt = JwtHelper.decode(accessToken);
        String claims = jwt.getClaims();
        HashMap<String, String> claimsMap = new HashMap<>();
        try {
            claimsMap = new ObjectMapper().readValue(claims, HashMap.class);
        } catch (Exception e) {
            log.error("Cannot retrieve client_id", e.getMessage());
            
        }
        
        return Mono.just(new UserTokenData(claimsMap, accessToken));
    }
}
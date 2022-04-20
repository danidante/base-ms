package com.mp.basems.infra.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Configuration
public class CredentialsParamsProducer {

    @Value("${oauth.client_id}")
    private String clientId;
    @Value("${oauth.client_secret}")
    private String clientSecret;
    @Value("${oauth.user_name}")
    private String userName;
    @Value("${oauth.password}")
    private String password;
    
    @Bean("usersParams")
    public MultiValueMap<String, String> params() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", this.clientId);
        params.add("client_secret", this.clientSecret);
        params.add("username", this.userName);
        params.add("password", this.password);
        params.add("scope", "openid");
        return params;
    }
}

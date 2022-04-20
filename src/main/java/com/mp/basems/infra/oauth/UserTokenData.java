package com.mp.basems.infra.oauth;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;

@Data
@Log4j2
public class UserTokenData {

    private String userId = "";
    private String role = "";
    private String accessToken = "";
    private String beneficiaryId = "";
    private String marketplaceId = "";

    // default user token data (empty)
    public UserTokenData() {}

    public UserTokenData(HashMap<String, String> claimsMap, String accessToken) {
        this.userId = claimsMap.get("userID") == null ? "" : claimsMap.get("userID");
        this.role = claimsMap.get("roles") == null ? "" : claimsMap.get("roles");
        this.beneficiaryId = claimsMap.get("beneficiaryID") == null ? "" : claimsMap.get("beneficiaryID");
        this.accessToken = accessToken;
        if(claimsMap.get("marketplaceID") == null) {
            log.warn("claimsMap.get(\"marketplaceID\") is null");
            this.marketplaceId = "";
        } else {
            this.marketplaceId = claimsMap.get("marketplaceID");
        }
    }

}

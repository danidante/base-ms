package com.mp.basems.infra.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateRequestObject {
    
    protected String id;
    
    public UpdateRequestObject setId(String id) {
        this.id = id;
        return this;
    }

}

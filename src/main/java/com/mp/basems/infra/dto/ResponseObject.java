package com.mp.basems.infra.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.mp.basems.infra.jackson.Views;
import com.mp.basems.infra.model.MPEntity;

@JsonView(Views.Public.class)
public abstract class ResponseObject<M extends MPEntity<?,?,?>> {

    public ResponseObject(M model) {
        this.setResponseObject(model);
    }
    
    // method for now, useless !
    public ResponseObject<M> setResponseObject(M model) {
        this.setResponseDetail(model);
        return this;
    }
    
    public abstract void setResponseDetail(M model);
}

package com.mp.basems.infra.event;

import lombok.Getter;

@Getter
public class Event<S> {

    private S object;
    private String entity;
    private String entityId;
    private String tenant;
    
    public Event(S object, String entity, String entityId, String tenant) {
        this.object = object;
        this.entity = entity;
        this.entityId = entityId;
    }
}

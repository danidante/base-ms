package com.mp.basems.infra.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Data
public class EventStore {

    private String dateTime;
    private String entity;
    private String type;
    private Object data;
    private String entityId;
    private String tenant;

    public EventStore(@SuppressWarnings("rawtypes") Event event, String type) {
        this.dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.entity = event.getEntity();
        this.type = type;
        this.data = addData(event.getObject());
        this.entityId = event.getEntityId();
        this.tenant = event.getTenant();
    }

    private String addData(Object data) {
        String returnedData = "";
        try {
            returnedData = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            // manage this exception.
            throw new RuntimeException("ERROR ON PARSE OBJECT TO JSON");
        }
        return returnedData;
    }


}

package com.mp.basems.infra.resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QueryObject<T> {
    
    private Class<T> typeParameterClass;
    
    public QueryObject(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public T setObject(Map<String, Object> map) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
        //find a better way ! :(
        map.remove("startDate");
        map.remove("endDate");
        map.remove("pageNumber");
        map.remove("pageSize");
        
        ObjectMapper mapper = new ObjectMapper();
//        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        try {
            return mapper.readValue(mapper.writeValueAsString(map), this.typeParameterClass);
        } catch (IOException e) {
            return mapper.readValue(mapper.writeValueAsString(new HashMap<String, Object>()), this.typeParameterClass);
        }
    }
    
}

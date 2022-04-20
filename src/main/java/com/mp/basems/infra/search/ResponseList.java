package com.mp.basems.infra.search;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.mp.basems.infra.jackson.Views;

import lombok.Data;

@JsonView(Views.Public.class)
@Data
public class ResponseList<T> {
    
    private Pages pages;
    private List<T> items;

    public ResponseList() {}

    public ResponseList(Pages pages, List<T> items) {
        this.pages = pages;
        this.items = items;
    }

    public List<T> getItems() {
        return this.items;
    }
    
    public ResponseList<T> setPaginationValues(Pages pages, List<T> items) {
        this.items = items;
        this.pages = pages;
        return this;
    }

}

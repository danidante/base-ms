package com.mp.basems.infra.search;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonView;
import com.mp.basems.infra.jackson.Views;

import lombok.Data;

@JsonView(Views.Public.class)
@Data
public class Pages {

    protected Pages() {
    }

    public Pages(long totalElements) {
        this.totalItem = totalElements;
    }

    public Pages(Page<?> pageInformation) {
        this.totalItem = pageInformation.getTotalElements();
        this.totalPage = pageInformation.getTotalPages();
        this.pageSize = pageInformation.getSize();
        this.pageNumber = pageInformation.getNumber() + 1;
    }

    private long totalItem;
    private long totalPage;
    private long pageSize;
    private long pageNumber;
}
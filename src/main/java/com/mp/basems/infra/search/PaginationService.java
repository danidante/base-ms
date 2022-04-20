package com.mp.basems.infra.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class PaginationService<T> {

    @Value("${server.url}")
    private String url;

    public HttpHeaders buildHeaders(Pageable page, ResponseList<T> responseListObject) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Link", createLinkHeader(page, responseListObject));
        return responseHeaders;
    }

    public Mono<Page<T>> buildPagination(List<T> items, Long count, Pageable page) {
        return Mono.just(PageableExecutionUtils.getPage(items, page, () -> count));
    }

    private String createLinkHeader(Pageable page, ResponseList<T> responseListObject) {
        final StringBuilder linkHeader = new StringBuilder();
        linkHeader.append(buildLinkHeader(url + "?skip=" + (page.getPageNumber() <= 1 ? 0 : page.getPageNumber() - 1)
                + "&limit=" + page.getPageSize(), "prev"));
        linkHeader.append(", ");
        linkHeader.append(buildLinkHeader(url + "?skip="
                + (responseListObject.getItems().size() == page.getPageSize()
                        ? page.getPageSize() + page.getPageNumber()
                        : page.getPageNumber())
                + "&limit=" + page.getPageSize(), "next"));
        return linkHeader.toString();
    }

    private String buildLinkHeader(final String uri, final String rel) {
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}

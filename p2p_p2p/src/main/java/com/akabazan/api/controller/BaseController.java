package com.akabazan.api.controller;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.akabazan.common.dto.BaseResponse;

public abstract class BaseController extends BaseApiController {

    protected <T, R> BaseResponse<List<R>> buildPagedResponse(Page<T> page, Function<T, R> mapper) {
        List<R> data = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        BaseResponse<List<R>> response = BaseResponse.success("OK", data);

        BaseResponse.PaginationMeta meta = new BaseResponse.PaginationMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious());

        String selfLink = buildPageLink(page.getNumber(), page.getSize());
        String nextLink = page.hasNext() ? buildPageLink(page.getNumber() + 1, page.getSize()) : null;
        String prevLink = page.hasPrevious() ? buildPageLink(page.getNumber() - 1, page.getSize()) : null;

        response.setMeta(meta);
        response.setLinks(new BaseResponse.PaginationLinks(selfLink, nextLink, prevLink));
        return response;
    }

    private String buildPageLink(int page, int size) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .toUriString();
    }
}

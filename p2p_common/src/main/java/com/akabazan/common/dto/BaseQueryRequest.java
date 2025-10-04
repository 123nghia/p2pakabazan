package com.akabazan.common.dto;

public abstract class BaseQueryRequest {


    private Integer page = 0;
    private Integer size = 10;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public int getPageOrDefault() {
        return page != null && page >= 0 ? page : 0;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public int getSizeOrDefault() {
        return size != null && size > 0 ? size : 10;
    }
}

package com.petshop.dto;

import java.util.List;

public class PageResponse<T> {
    private List<T> data;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;

    public PageResponse() {
    }

    public PageResponse(List<T> data, Long total, Integer page, Integer pageSize, Integer totalPages) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static class Builder<T> {
        private List<T> data;
        private Long total;
        private Integer page;
        private Integer pageSize;
        private Integer totalPages;

        public Builder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public Builder<T> total(Long total) {
            this.total = total;
            return this;
        }

        public Builder<T> page(Integer page) {
            this.page = page;
            return this;
        }

        public Builder<T> pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<T> totalPages(Integer totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PageResponse<T> build() {
            return new PageResponse<>(data, total, page, pageSize, totalPages);
        }
    }
}

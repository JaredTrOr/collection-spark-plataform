package org.jared.trujillo.dto;

import java.util.List;

public class Page<T> {
    private final List<T> data;
    private final int page;
    private final int limit;
    private final long totalItems;
    private final int totalPages;

    public Page(List<T> data, int page, int limit, long totalItems) {
        this.data = data;
        this.page = page;
        this.limit = limit;
        this.totalItems = totalItems;

        this.totalPages = (int) Math.ceil((double) totalItems / limit);
    }

    public List<T> getData() { return data; }
    public int getPage() { return page; }
    public int getLimit() { return limit; }
    public long getTotalItems() { return totalItems; }
    public int getTotalPages() { return totalPages; }
}

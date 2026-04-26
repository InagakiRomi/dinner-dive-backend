package com.romi.my_dinnerdive.dto;

/** 歷史紀錄查詢參數 */
public class RestaurantHistoryQueryParams {

    /** 排序欄位 */
    private String orderBy;

    /** 排序方向 */
    private String sort;

    /** 每頁筆數 */
    private Integer limit;

    /** 起始位移 */
    private Integer offset;

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}

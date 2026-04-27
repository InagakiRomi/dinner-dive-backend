package com.romi.my_dinnerdive.dto;

import com.romi.my_dinnerdive.constant.RestaurantCategory;

/** 
 * 用來接收查詢餐廳清單時的各種條件參數：
 * <p>
 * 包含分類、關鍵字、排序、分頁設定等，集中包裝成一個物件給 Service 使用
 */
public class RestaurantQueryParams {
    /** 所屬群組 */
    private Integer groupId;

    /** 餐廳分類 */
    private RestaurantCategory category;

    /** 關鍵字搜尋，通常對應到餐廳名稱或備註的模糊查詢 */
    private String search;

    /** 指定排序欄位 */
    private String orderBy;

    /** 指定排序升降 */
    private String sort;

    /** 每次查詢最多回傳幾筆資料 */
    private Integer limit;

    /** 資料從第幾筆開始 */
    private Integer offset;

    public RestaurantCategory getCategory() {
        return category;
    }

    public void setCategory(RestaurantCategory category) {
        this.category = category;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

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

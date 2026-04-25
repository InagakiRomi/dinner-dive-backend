package com.romi.my_dinnerdive.util;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分頁回傳格式封裝類
 * <p>
 * 用於回應 API 查詢結果時，包含：筆數限制、起始位置、總筆數、資料清單等欄位。
 * @param <T> 回傳資料的型別
 */
@Schema(description = "分頁回應資料")
public class Page<T> {

    /** 每次查詢最多回傳幾筆資料 */
    @Schema(description = "每頁回傳資料筆數", example = "10")
    private Integer limit;

    /** 資料從第幾筆開始 */
    @Schema(description = "起始位移", example = "0")
    private Integer offset;

     /** 總筆數 */
    @Schema(description = "符合條件的資料總筆數", example = "35")
    private Integer total;

    /** 回傳的資料內容清單 */
    @Schema(description = "本頁資料內容")
    private List<T> results;

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
    public Integer getTotal() {
        return total;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }
    public List<T> getResults() {
        return results;
    }
    public void setResults(List<T> results) {
        this.results = results;
    }
}

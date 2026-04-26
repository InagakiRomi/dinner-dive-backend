package com.romi.my_dinnerdive.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romi.my_dinnerdive.dto.RestaurantHistoryQueryParams;
import com.romi.my_dinnerdive.dto.RestaurantHistoryResponse;
import com.romi.my_dinnerdive.model.RestaurantHistory;
import com.romi.my_dinnerdive.service.RestaurantHistoryService;
import com.romi.my_dinnerdive.util.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/** 提供歷史紀錄查詢 API */
@Validated
@RestController
@Tag(name = "抽選歷史紀錄", description = "提供抽選餐廳的歷史紀錄查詢功能")
public class RestaurantHistoryController {

    @Autowired
    private RestaurantHistoryService restaurantHistoryService;

    @GetMapping("/restaurantHistories")
    @Operation(summary = "查詢抽選歷史紀錄", description = "支援排序與分頁查詢抽選歷史紀錄")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "400", description = "查詢參數格式錯誤")
    })
    public ResponseEntity<Page<RestaurantHistoryResponse>> getRestaurantHistories(
            @Parameter(description = "排序欄位，預設 selected_at，可填：selected_at、restaurant_name、category、history_id")
            @RequestParam(defaultValue = "selected_at") String orderBy,
            @Parameter(description = "排序方式，預設 DESC，可填 ASC 或 DESC")
            @RequestParam(defaultValue = "DESC") String sort,
            @Parameter(description = "每頁筆數，預設 10，最大 1000")
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit,
            @Parameter(description = "起始位移，預設 0，最小為 0")
            @RequestParam(defaultValue = "0") @Min(0) Integer offset
    ) {
        RestaurantHistoryQueryParams queryParams = new RestaurantHistoryQueryParams();
        queryParams.setOrderBy(orderBy);
        queryParams.setSort(sort);
        queryParams.setLimit(limit);
        queryParams.setOffset(offset);

        List<RestaurantHistory> histories = restaurantHistoryService.getHistories(queryParams);
        Integer total = restaurantHistoryService.countHistory();
        List<RestaurantHistoryResponse> responseList = histories.stream()
                .map(RestaurantHistoryResponse::new)
                .toList();

        Page<RestaurantHistoryResponse> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);
        page.setResults(responseList);

        return ResponseEntity.status(HttpStatus.OK).body(page);
    }
}

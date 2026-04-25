package com.romi.my_dinnerdive.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.romi.my_dinnerdive.dto.DishRequest;
import com.romi.my_dinnerdive.model.Dish;
import com.romi.my_dinnerdive.service.DishService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** 提供與「餐廳餐點」相關的 RESTful API，包含餐點的查詢等功能 */
@RestController
@Tag(name = "餐點管理", description = "提供餐點查詢、新增與刪除功能")
public class DishController {

    @Autowired
    private DishService dishService;

    /** 根據餐廳 ID 查詢餐點資料 */
    @GetMapping("/restaurants/{restaurantId}/dishes")
    @Operation(
            summary = "查詢餐廳餐點",
            description = "依餐廳 ID 取得該餐廳所有餐點資料"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "404", description = "查無此餐廳或無餐點資料")
    })
    public ResponseEntity<List<Dish>> getDishes(
            @Parameter(description = "餐廳 ID")
            @PathVariable Integer restaurantId
    ){
        List<Dish> dish = dishService.findByRestaurantId(restaurantId);

        return ResponseEntity.status(HttpStatus.OK).body(dish);
    }

    /** 新增一筆餐點資料 */
    @PostMapping(value = "/dishes")
    @Operation(
            summary = "新增餐點",
            description = "建立一筆新的餐點資料"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "請求資料驗證失敗")
    })
    public ResponseEntity<Dish> createDish(
            // 從前端取得請求資料，並驗證格式
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "新增餐點的請求內容",
                    content = @Content(schema = @Schema(implementation = DishRequest.class))
            )
            @RequestBody @Valid DishRequest dishRequest
    ) {
        // 呼叫 Service 層，將新增資料寫入資料庫，取得新產生的 dishId
        Integer dishId = dishService.createDish(dishRequest);

        // 根據新產生的 ID，再查詢該筆資料
        Dish dish = dishService.getDishById(dishId);

        // 回傳 HTTP 201（Created）狀態碼，並附上新增成功的資料
        return ResponseEntity.status(HttpStatus.CREATED).body(dish);
    }

    /** 刪除指定的餐點資料 */
    @DeleteMapping("/dishes/{dishId}")
    @Operation(
            summary = "刪除餐點",
            description = "依餐點 ID 刪除指定餐點資料"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "刪除成功"),
            @ApiResponse(responseCode = "404", description = "查無此餐點")
    })
    public ResponseEntity<?> deleteDish(
            @Parameter(description = "餐點 ID")
            @PathVariable Integer dishId
    ) {

        // 呼叫 Service 層刪除該餐點資料（若不存在也不會報錯）
        dishService.deleteDishById(dishId);

        // 回傳 HTTP 204（No Content），代表刪除成功
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

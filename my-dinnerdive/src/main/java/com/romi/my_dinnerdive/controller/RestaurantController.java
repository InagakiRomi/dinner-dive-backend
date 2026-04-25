package com.romi.my_dinnerdive.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romi.my_dinnerdive.constant.RestaurantCategory;
import com.romi.my_dinnerdive.dto.RestaurantRequest;
import com.romi.my_dinnerdive.dto.RestaurantResponse;
import com.romi.my_dinnerdive.dto.RestaurantQueryParams;
import com.romi.my_dinnerdive.model.Restaurant;
import com.romi.my_dinnerdive.service.RestaurantService;
import com.romi.my_dinnerdive.util.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.web.bind.annotation.PutMapping;


/** 提供與「餐廳」相關的 RESTful API，包含餐廳的查詢、新增、修改、刪除、隨機抽選等功能 */
@Validated
@RestController
@Tag(name = "餐廳管理", description = "提供餐廳的查詢、新增、修改、刪除與隨機抽選功能")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;


    /**
     * 查詢餐廳清單的 API，支援條件查詢（如分類、關鍵字）、排序（如依名稱或建立時間）、分頁等功能
     * 
     * @param category ：餐廳分類
     * @param search ：關鍵字查詢
     * @param orderBy ：排序欄位
     * @param sort ：排序方式
     * @param limit ：每頁資料筆數
     * @param offset ：跳過的資料筆數
     * @return 回傳包含查詢結果與分頁資訊的 Page 物件
     */
    @GetMapping("/restaurants")
    @Operation(
            summary = "查詢餐廳列表",
            description = "依分類、關鍵字、排序與分頁條件查詢餐廳資料，回傳分頁結果"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "400", description = "查詢參數格式錯誤")
    })
    public ResponseEntity<Page<RestaurantResponse>> getRestaurants(
            // 查詢條件        
            @Parameter(description = "餐廳分類過濾條件，可選：MAIN、SNACK、DRINK")
            @RequestParam(required = false) RestaurantCategory category, // 可選：餐廳分類過濾
            @Parameter(description = "關鍵字模糊查詢，通常比對餐廳名稱或備註")
            @RequestParam(required = false) String search,               // 可選：模糊搜尋

            // 排序
            @Parameter(description = "排序欄位，預設 restaurant_id")
            @RequestParam(defaultValue = "restaurant_id") String orderBy, // 排序欄位，預設 ID
            @Parameter(description = "排序方式，預設 ASC，可填 ASC 或 DESC")
            @RequestParam(defaultValue = "ASC") String sort,              // 排序方式，預設升冪

            //分頁
            @Parameter(description = "每頁筆數，預設 10，最大 1000")
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0) Integer limit, // 每頁筆數
            @Parameter(description = "起始位移，預設 0，最小為 0")
            @RequestParam(defaultValue = "0")  @Min(0) Integer offset            // 分頁起始點
    ) {
        // 建立查詢參數物件，封裝所有條件
        RestaurantQueryParams restaurantQueryParams = new RestaurantQueryParams();
        restaurantQueryParams.setCategory(category); // 設定分類
        restaurantQueryParams.setSearch(search);     // 設定模糊搜尋字串
        restaurantQueryParams.setOrderBy(orderBy);   // 設定排序欄位
        restaurantQueryParams.setSort(sort);         // 設定排序順序
        restaurantQueryParams.setLimit(limit);       // 設定每頁幾筆
        restaurantQueryParams.setOffset(offset);     // 設定從第幾筆開始取
        
        // 根據查詢條件從資料庫取得餐廳清單
        List<Restaurant> restaurantList = restaurantService.getRestaurants(restaurantQueryParams);

        // 取得符合條件的餐廳總數，用於分頁
        Integer total = restaurantService.countRestaurant(restaurantQueryParams);

        // 將資料庫中的餐廳物件轉換成回應用的格式（Restaurant → RestaurantResponse）
        List<RestaurantResponse> responseList = restaurantList.stream()
                .map(RestaurantResponse::new) // 用建構子轉換格式
                .toList();                    // 收集轉換後的清單

        // 封裝分頁結果
        Page<RestaurantResponse> page = new Page<>();
        page.setLimit(limit);
        page.setOffset(offset);
        page.setTotal(total);
        page.setResults(responseList);

        // 回傳 HTTP 200（OK）與分頁結果
        return ResponseEntity.status(HttpStatus.OK).body(page);
    }

    /**
     * 根據餐廳 ID 查詢資料
     *
     * @param restaurantId ：餐廳的唯一 ID
     * @return 成功則回傳該餐廳資料（HTTP 200），查無資料則回傳 HTTP 404
     */
    @GetMapping("/restaurants/{restaurantId}")
    @Operation(
            summary = "查詢單一餐廳",
            description = "依餐廳 ID 取得單一餐廳詳細資料"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "404", description = "查無此餐廳")
    })
    public ResponseEntity<Restaurant> getRestaurant(
            @Parameter(description = "餐廳 ID")
            @PathVariable Integer restaurantId
    ){

        // 根據 ID 從資料庫查詢對應餐廳
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        // 如果有找到該筆資料
        if(restaurant != null){
            // 回傳 HTTP 200 並附帶該餐廳資料
            return ResponseEntity.status(HttpStatus.OK).body(restaurant);
        } else {
            // 沒有該餐廳資料，回傳 HTTP 404（Not Found）
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 新增一筆餐廳資料
     *
     * @param restaurantRequest ：包含新增資料的請求物件，會進行格式與欄位驗證
     * @param visitedCount ：初始選擇次數
     * @return 回傳新增成功的餐廳資料與 HTTP 201（已建立）狀態碼
     */
    @PostMapping(value = "/restaurants")
    @Operation(
            summary = "新增餐廳",
            description = "建立一筆新的餐廳資料"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "請求資料驗證失敗")
    })
    public ResponseEntity<Restaurant> createRestaurant(
            // 從前端取得請求資料，並驗證格式    
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "新增餐廳的請求內容",
                    content = @Content(schema = @Schema(implementation = RestaurantRequest.class))
            )
            @RequestBody @Valid RestaurantRequest restaurantRequest,

            // 可選：設定初始選擇次數
            @Parameter(description = "初始選擇次數，預設 0（目前僅保留參數，不影響新增流程）")
            @RequestParam(defaultValue = "0") Integer visitedCount
    ) {
        // 呼叫 Service 層，將新增資料寫入資料庫，取得新產生的 restaurantId
        Integer restaurantId = restaurantService.createRestaurant(restaurantRequest);

        // 根據新產生的 ID，再查詢該筆資料
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        // 回傳 HTTP 201（Created）狀態碼，並附上新增成功的資料
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurant);
    }

    /**
     * 修改一筆指定 ID 的餐廳資料
     *
     * @param restaurantId ：要修改的餐廳 ID
     * @param restaurantRequest ：包含修改內容的請求物件，會進行驗證
     * @return 若修改成功，回傳更新後資料與 HTTP 200；若查無資料，回傳 HTTP 404
     */
    @PutMapping("/restaurants/{restaurantId}")
    @Operation(
            summary = "完整更新餐廳",
            description = "依餐廳 ID 覆蓋更新餐廳資料"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "請求資料驗證失敗"),
            @ApiResponse(responseCode = "404", description = "查無此餐廳")
    })
    public ResponseEntity<Restaurant> updateRestaurant(
                                                       @Parameter(description = "要更新的餐廳 ID")
                                                       @PathVariable Integer restaurantId,
                                                       @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                               required = true,
                                                               description = "完整更新餐廳的請求內容",
                                                               content = @Content(schema = @Schema(implementation = RestaurantRequest.class))
                                                       )
                                                       @RequestBody @Valid RestaurantRequest restaurantRequest) {
        
        // 先確認要修改的餐廳是否存在
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        // 如果不存在，回傳 HTTP 404
        if (restaurant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 存在的話，呼叫 Service 層更新該筆資料
        restaurantService.updateRestaurant(restaurantId, restaurantRequest);

        // 再查一次更新後的資料，回傳給前端
        Restaurant updatedRestaurant = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRestaurant);
    }

    /**
     * 刪除指定的餐廳資料
     *
     * @param restaurantId ：要刪除的餐廳 ID
     * @return 若刪除成功，回傳 HTTP 204（無內容）；若查無資料，回傳 HTTP 404
     */
    @DeleteMapping("/restaurants/{restaurantId}")
    @Operation(
            summary = "刪除餐廳",
            description = "依餐廳 ID 刪除指定餐廳資料"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "刪除成功"),
            @ApiResponse(responseCode = "404", description = "查無此餐廳")
    })
    public ResponseEntity<?> deleteRestaurant(
            @Parameter(description = "要刪除的餐廳 ID")
            @PathVariable Integer restaurantId
    ) {

        // 呼叫 Service 層刪除該餐廳資料（若不存在也不會報錯）
        restaurantService.deleteRestaurantById(restaurantId);

        // 回傳 HTTP 204（No Content），代表刪除成功
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 抽選一間隨機餐廳（可根據分類過濾）
     *
     * @param category ：可選分類條件（例如只抽主食類）
     * @return 回傳隨機選中的餐廳資料
     */
    @GetMapping("/random")
    @Operation(
            summary = "抽選隨機餐廳",
            description = "隨機抽出一間餐廳，可用分類作為過濾條件"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "抽選成功"),
            @ApiResponse(responseCode = "404", description = "查無可抽選的餐廳")
    })
    public ResponseEntity<RestaurantResponse> getRandomRestaurant(
            // 可選分類（若指定，從該類別中抽）
            @Parameter(description = "餐廳分類過濾條件，可選：MAIN、SNACK、DRINK")
            @RequestParam(required = false) RestaurantCategory category
    ) {
        // 將查詢條件包裝成物件
        RestaurantQueryParams params = new RestaurantQueryParams();
        params.setCategory(category);

         // 抽出一筆隨機餐廳
        Restaurant random = restaurantService.getRandomRestaurant(params);

        // 回傳餐廳資料
        return ResponseEntity.ok(new RestaurantResponse(random));
    }

    /** 清除所有已抽過的餐廳，通常用於重置抽選 */
    @PostMapping("/clearRandom")
    @Operation(
            summary = "重置抽選紀錄",
            description = "清除所有已抽選過的餐廳紀錄，讓餐廳可重新參與抽選"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "重置成功")
    })
    public void clearRandomRestaurant() {
        // 將已抽過的餐廳清空
        restaurantService.clearRandomRestaurant();
    }

    /**
     * 選定一間餐廳並更新其選擇紀錄
     * <p>
     * 包含：將選擇次數加一、更新最後選擇時間
     *
     * @param restaurantId ：要選擇的餐廳 ID
     * @return 成功則回傳更新後資料（HTTP 200）；查無資料則回傳 HTTP 404
     */
    @PatchMapping("/choose/{restaurantId}")
    @Operation(
            summary = "選定餐廳",
            description = "將指定餐廳標記為已選擇，更新選擇次數與最後選擇時間"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新選擇紀錄成功"),
            @ApiResponse(responseCode = "404", description = "查無此餐廳")
    })
    public ResponseEntity<Restaurant> chooseRestaurant(
            @Parameter(description = "要選擇的餐廳 ID")
            @PathVariable Integer restaurantId
    ) {
        // 查詢該餐廳是否存在
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

        // 檢查 restaurant 是否存在
        if (restaurant == null) {
            // 若找不到，回傳 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 更新該餐廳的選擇紀錄（次數加一、更新時間）
        restaurantService.chooseRestaurant(restaurantId);

        // 查詢更新後的資料
        Restaurant chooseRestaurant = restaurantService.getRestaurantById(restaurantId);

        // 回傳最新狀態
        return ResponseEntity.status(HttpStatus.OK).body(chooseRestaurant);
    }
}
package com.romi.my_dinnerdive.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/** 定義餐廳有哪些分類 */
@Schema(description = "餐廳分類列舉")
public enum RestaurantCategory {

    // 餐廳分類，每個括號裡的是「對外顯示的中文名稱」
    MAIN("主食"),
    SNACK("輕食"),
    DRINK("飲料");

    // 用來存放「對外顯示的分類名稱」的變數，例如「主食」
    private final String displayName;

    // 建構子：當 enum 被建立時，會把 displayName 的值設定進來
    RestaurantCategory(String displayName) {
        this.displayName = displayName;
    }

    /** 取得分類的中文名稱 */
    public String getDisplayName() {
        return displayName;
    }
}

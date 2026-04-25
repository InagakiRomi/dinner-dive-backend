package com.romi.my_dinnerdive;

import java.util.logging.*;

import com.romi.my_dinnerdive.logging.LoggingDemo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/** 應用程式進入點，啟動 Spring Boot 應用，載入所有組件與設定，並啟動內建伺服器 */
@SpringBootApplication // 啟用自動組態、元件掃描與 Spring Boot 機制
public class MyDinnerdiveApplication {

    /**
     * main 方法為專案的啟動入口
     *
     * @param args 命令列參數（通常不使用）
     */
     public static void main(String[] args) {
         // 啟動 Spring Boot，並取得應用上下文
        ConfigurableApplicationContext context = SpringApplication.run(MyDinnerdiveApplication.class, args);

        // 取得 LoggingDemo 實例並印出主流程啟動 log
        LoggingDemo loggingDemo = context.getBean(LoggingDemo.class);
        Logger logger = loggingDemo.printMainLog();
        logger.log(Level.INFO, "可啟動應用程式，請輸入 http://localhost:8080/dinnerHome 來存取應用程式。");
        logger.log(Level.INFO, "如果要測試API，請輸入 http://localhost:8080/swagger-ui/index.html 。");
        
    }
}
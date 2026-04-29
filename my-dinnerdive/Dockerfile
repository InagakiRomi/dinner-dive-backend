# 第一階段：使用 Maven + JDK 17 建置應用程式
FROM maven:3.9.0-eclipse-temurin-17 AS build
# 將專案檔案複製到容器內
COPY . /app
# 設定 Maven 執行目錄
WORKDIR /app/my-dinnerdive
# 編譯並打包 Jar（略過測試）
RUN mvn clean package -DskipTests

# 第二階段：使用精簡 JRE 17 作為執行環境
FROM eclipse-temurin:17-jre
# 複製第一階段產出的 Jar
COPY --from=build /app/my-dinnerdive/target/*.jar app.jar
# 對外開放應用程式連接埠
EXPOSE 9000
# 容器啟動時執行應用程式
CMD ["java", "-jar", "app.jar"]
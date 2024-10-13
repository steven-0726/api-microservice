# **專案 README**

## **目錄**

1. [專案簡介](#專案簡介)
2. [專案架構](#專案架構)
3. [環境需求](#環境需求)
4. [服務說明](#服務說明)
    - [api-microservice](#api-microservice)
    - [eureka-server](#eureka-server)
    - [api-gateway](#api-gateway)
5. [配置參數說明](#配置參數說明)
    - [api-microservice 參數](#api-microservice-參數)
    - [eureka-server 參數](#eureka-server-參數)
    - [api-gateway 參數](#api-gateway-參數)
6. [服務啟動與停止](#服務啟動與停止)
7. [服務後台連結與使用方式](#服務後台連結與使用方式)
    - [Eureka 服務註冊中心](#eureka-服務註冊中心)
    - [API 微服務](#api-微服務)
    - [API 閘道](#api-閘道)
8. [測試與驗證](#測試與驗證)
9. [注意事項](#注意事項)
10. [常見問題](#常見問題)
11. [聯絡方式](#聯絡方式)

---

## **專案簡介**

本專案是一個基於 **Spring Boot 3** 和 **Java 17** 的微服務架構，旨在提供一個高可用、可擴展的服務平台。該架構包含以下組件：

- **`api-microservice`**：主要的業務微服務，負責處理資料操作。
- **`eureka-server`**：服務註冊中心，管理微服務的註冊與發現。
- **`api-gateway`**：API 閘道，提供統一的入口，並處理路由、斷路器等功能。

---

## **專案架構**

```
├── api-microservice
├── eureka-server
└── api-gateway
```

- **api-microservice**：提供資料處理 API，並註冊到 Eureka 服務註冊中心。
- **eureka-server**：作為服務註冊中心，管理各微服務的註冊與發現。
- **api-gateway**：作為統一入口，處理請求路由、負載平衡和斷路器等功能。

---

## **環境需求**

- **Java 開發套件 (JDK)**：**Java 17**
- **Apache Maven**：建議使用 **3.6.x** 或以上版本
- **資料庫**：Oracle 資料庫（api-microservice 使用）
- **開發工具**：如 **IntelliJ IDEA**、**Eclipse**、**VS Code** 等
- **網路環境**：確保各服務埠號可訪問

---

## **服務說明**

### **api-microservice**

- **功能**：提供資料的新增、查詢等操作。
- **技術棧**：Spring Boot 3、Spring Data JPA、Resilience4j、Eureka Client、SpringDoc OpenAPI 3。
- **埠號**：`8080`

### **eureka-server**

- **功能**：作為服務註冊中心，管理微服務的註冊與發現。
- **技術棧**：Spring Boot 3、Eureka Server。
- **埠號**：`8761`

### **api-gateway**

- **功能**：提供統一的 API 入口，實現路由轉發、負載平衡、斷路器等功能。
- **技術棧**：Spring Boot 3、Spring Cloud Gateway、Resilience4j、Eureka Client。
- **埠號**：`8081`

---

## **配置參數說明**

### **api-microservice 參數**

**`src/main/resources/application.properties`**

```properties
# 資料庫連線設定
spring.datasource.url=jdbc:oracle:thin:@<資料庫地址>:<埠號>:<SID>
spring.datasource.username=<資料庫使用者名稱>
spring.datasource.password=<資料庫密碼>
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA 設定
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# 伺服器埠號
server.port=8080

# Eureka 客戶端設定
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
spring.application.name=api-microservice

# Resilience4j 斷路器設定
resilience4j.circuitbreaker.instances.dataService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.dataService.minimum-number-of-calls=10
resilience4j.circuitbreaker.instances.dataService.sliding-window-size=100
resilience4j.circuitbreaker.instances.dataService.permitted-number-of-calls-in-half-open-state=10
resilience4j.circuitbreaker.instances.dataService.wait-duration-in-open-state=10s
```

- **資料庫連線設定**：配置 Oracle 資料庫的連線資訊。
    - **`spring.datasource.url`**：資料庫連線 URL。
    - **`spring.datasource.username`**：資料庫使用者名稱。
    - **`spring.datasource.password`**：資料庫密碼。
- **JPA 設定**：配置 Hibernate 的相關屬性。
- **伺服器埠號**：設定服務運行的埠號，默認為 `8080`。
- **Eureka 客戶端設定**：配置 Eureka 註冊中心的地址，並指定應用名稱。
- **Resilience4j 斷路器設定**：配置斷路器的相關參數，如失敗率閾值、滑動窗口大小等。

### **eureka-server 參數**

**`src/main/resources/application.properties`**

```properties
server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

# Eureka 服務端設定（可選）
eureka.instance.hostname=localhost
```

- **伺服器埠號**：設定 Eureka Server 運行的埠號，默認為 `8761`。
- **Eureka 服務端設定**：
    - **`eureka.client.register-with-eureka`**：設為 `false`，表示 Eureka Server 本身不註冊到 Eureka。
    - **`eureka.client.fetch-registry`**：設為 `false`，表示 Eureka Server 不會從 Eureka 獲取註冊表。

### **api-gateway 參數**

**`src/main/resources/application.properties`**

```properties
# 伺服器埠號
server.port=8081

# Eureka 客戶端設定
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
spring.application.name=api-gateway

# 啟用服務發現的路由定位器
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lower-case-service-id=true

# Resilience4j 斷路器設定
resilience4j.circuitbreaker.instances.apiMicroserviceCircuitBreaker.sliding-window-size=100
resilience4j.circuitbreaker.instances.apiMicroserviceCircuitBreaker.minimum-number-of-calls=10
resilience4j.circuitbreaker.instances.apiMicroserviceCircuitBreaker.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.apiMicroserviceCircuitBreaker.wait-duration-in-open-state=10s

# 路由和斷路器設定
spring.cloud.gateway.routes[0].id=api-microservice
spring.cloud.gateway.routes[0].uri=lb://api-microservice
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/data/**
spring.cloud.gateway.routes[0].filters[0]=CircuitBreaker=name=apiMicroserviceCircuitBreaker,fallbackUri=/fallback

# 日誌級別（可選，用於偵錯）
logging.level.org.springframework.cloud.gateway=DEBUG
```

- **伺服器埠號**：設定 API Gateway 運行的埠號，默認為 `8081`。
- **Eureka 客戶端設定**：配置 Eureka 註冊中心的地址，並指定應用名稱。
- **服務發現路由定位器**：啟用自動根據 Eureka 註冊的服務進行路由。
- **Resilience4j 斷路器設定**：配置斷路器的相關參數，名稱需與路由配置中的斷路器名稱一致。
- **路由和斷路器設定**：配置路由規則和斷路器過濾器。
    - **`uri`**：指定後端服務的地址，`lb://` 表示使用負載平衡。
    - **`predicates`**：定義路由的匹配規則，如路徑匹配。
    - **`filters`**：定義過濾器，這裡使用了斷路器過濾器，並指定了回退 URI。
- **日誌級別**：可選，設定日誌的輸出級別。

---

## **服務啟動與停止**

### **啟動順序**

1. **Eureka Server**

    ```bash
    cd eureka-server
    mvn clean install
    mvn spring-boot:run
    ```

    - **運行埠號**：`8761`
    - **說明**：先啟動服務註冊中心，以便其他微服務能夠註冊。

2. **API 微服務**

    ```bash
    cd ../api-microservice
    mvn clean install
    mvn spring-boot:run
    ```

    - **運行埠號**：`8080`
    - **說明**：啟動業務微服務，並註冊到 Eureka Server。

3. **API Gateway**

    ```bash
    cd ../api-gateway
    mvn clean install
    mvn spring-boot:run
    ```

    - **運行埠號**：`8081`
    - **說明**：啟動 API 閘道，作為統一入口，並通過 Eureka 進行服務發現。

### **停止服務**

- **使用 Ctrl+C**：在終端中按下 **Ctrl+C** 組合鍵，停止正在運行的服務。
- **使用 Maven 命令**

    ```bash
    mvn spring-boot:stop
    ```

---

## **服務後台連結與使用方式**

### **Eureka 服務註冊中心**

- **訪問 URL**：`http://localhost:8761`
- **功能**：查看已註冊的微服務實例、健康狀態等資訊。
- **使用方式**：在瀏覽器中訪問上述 URL，即可查看服務註冊中心的控制台。

### **API 微服務**

- **Swagger UI**：`http://localhost:8080/swagger-ui/index.html`
- **OpenAPI 文件**：`http://localhost:8080/v3/api-docs`
- **功能**：提供資料的新增、查詢等 API。
- **使用方式**：
    - **Swagger UI**：方便地測試 API，查看 API 文檔。
    - **直接調用 API**：使用 Postman 或其他 HTTP 客戶端，發送請求到 `http://localhost:8080/api/data`。

### **API 閘道**

- **訪問 URL**：`http://localhost:8081`
- **功能**：作為統一的 API 入口，處理路由、負載平衡和斷路器等功能。
- **使用方式**：
    - **調用微服務 API**：通過 API Gateway 訪問微服務，例如 `http://localhost:8081/api/data`。
    - **斷路器回退**：當後端服務不可用時，Gateway 會返回定義的回退內容。

---

## **測試與驗證**

### **1. 訪問 Eureka Server 控制台**

- 確認在瀏覽器中訪問 `http://localhost:8761`，可以看到已註冊的服務。

### **2. 通過 API Gateway 訪問微服務**

- **使用 Postman 發送請求**
    - **URL**：`http://localhost:8081/api/data`
    - **方法**：POST
    - **Body**：選擇 `raw`，格式為 `JSON`，輸入資料。
    - **範例資料**：

    ```json
    {
        "txAmt": 100.0,
        "txDate": "2023-01-01T10:00:00",
        "sing": "example",
        "creator": "user1"
    }
    ```

- **預期結果**：收到成功的回應，資料已寫入資料庫。

### **3. 驗證斷路器功能**

- **停止 API 微服務**

    ```bash
    cd api-microservice
    mvn spring-boot:stop
    ```

- **再次發送請求**

    - 預期收到回退訊息，如 `"服務暫時不可用，請稍後再試。"`。

- **查看 API Gateway 日誌**

    - 確認斷路器已經觸發。

### **4. 驗證負載平衡**

- **啟動多個 API 微服務實例**

    - **啟動第一個實例（埠號 8080）**

        ```bash
        cd api-microservice
        mvn clean install
        mvn spring-boot:run
        ```

    - **在另一個終端中啟動第二個實例（埠號 8082）**

        **使用命令行參數指定埠號：**

        ```bash
        cd api-microservice
        mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"
        ```

        **或使用 Profiles：**

        ```bash
        mvn spring-boot:run -Dspring-boot.run.profiles=8082
        ```

- **發送多次請求**

    - **操作**：通過 API Gateway 發送多次請求，使用以下命令：

        ```bash
        curl -X POST http://localhost:8081/api/data \
        -H "Content-Type: application/json" \
        -d '{"txAmt":100.0,"txDate":"2023-01-01T10:00:00","sing":"example","creator":"user1"}'
        ```

    - **觀察**：查看兩個實例的控制台輸出，確認請求被分發到不同的實例，證明負載平衡生效。

---

## **注意事項**

- **版本相容性**

    - 確保所有依賴的版本與 Spring Boot 3、Java 17 相容。

- **配置正確性**

    - 檢查各服務的 `application.properties`，確保參數配置正確。

- **網路與防火牆**

    - 在部署到伺服器環境時，注意開放相應的埠號。

- **安全性**

    - 在生產環境中，建議添加身份驗證和授權機制，保護 API 安全。

---

## **常見問題**

### **1. 無法訪問 Eureka Server**

- **解決方法**：確認 Eureka Server 已經啟動，並運行在正確的埠號。

### **2. 微服務未註冊到 Eureka**

- **解決方法**：檢查微服務的 Eureka 客戶端配置，確認 `eureka.client.service-url.defaultZone` 是否正確。

### **3. 無法通過 API Gateway 訪問微服務**

- **解決方法**：確認 API Gateway 的路由配置正確，並且微服務已經註冊到 Eureka。

### **4. 斷路器未生效**

- **解決方法**：檢查 Resilience4j 的配置，確認斷路器名稱和參數設定正確。

---

## **聯絡方式**

- **維運團隊**：請聯繫 IT 支持部門
- **電子郵件**：support@example.com
- **電話**：+886-1234-5678

---

**感謝您對本專案的支持，如有任何問題，請隨時聯繫我們！**
# **基於 Spring Boot 3 的微服務架構建構指南（Profiles 啟動多個微服務實例）**

## **目錄**

1. [簡介](#簡介)
2. [先決條件](#先決條件)
3. [專案結構](#專案結構)
4. [建立 `api-microservice` 微服務](#建立-api-microservice-微服務)
    - [建立專案目錄](#建立專案目錄)
    - [設定 `pom.xml`](#設定-pomxml)
    - [建立主應用程式類別](#建立主應用程式類別)
    - [設定資料庫連線](#設定資料庫連線)
    - [建立實體類別](#建立實體類別)
    - [建立 Repository 介面](#建立-repository-介面)
    - [建立 Controller](#建立-controller)
    - [設定 OpenAPI 3 (Swagger)](#設定-openapi-3-swagger)
    - [實作斷路器機制](#實作斷路器機制)
    - [新增測試案例](#新增測試案例)
5. [建立 `eureka-server` 服務註冊中心](#建立-eureka-server-服務註冊中心)
    - [建立專案目錄](#建立專案目錄-1)
    - [設定 `pom.xml`](#設定-pomxml-1)
    - [建立主應用程式類別](#建立主應用程式類別-1)
    - [設定 `application.properties`](#設定-applicationproperties)
    - [新增測試案例](#新增測試案例-1)
6. [建立 `api-gateway` API 閘道](#建立-api-gateway-api-閘道)
    - [建立專案目錄](#建立專案目錄-2)
    - [設定 `pom.xml`](#設定-pomxml-2)
    - [建立主應用程式類別](#建立主應用程式類別-2)
    - [設定 `application.properties`](#設定-applicationproperties-1)
    - [設定路由和斷路器](#設定路由和斷路器)
    - [新增 Fallback Controller](#新增-fallback-controller)
    - [新增測試案例](#新增測試案例-2)
7. [啟動順序](#啟動順序)
8. [驗證服務](#驗證服務)
    - [訪問 Eureka Server 控制台](#訪問-eureka-server-控制台)
    - [通過 API Gateway 訪問微服務](#通過-api-gateway-訪問微服務)
    - [驗證斷路器功能](#驗證斷路器功能)
    - [驗證負載平衡](#驗證負載平衡)
9. [更新 README](#更新-readme)
10. [總結](#總結)

---

## **簡介**

本建置指引將引導您如何建立一個基於 **Spring Boot 3** 和 **Java 17** 的微服務架構，並使用 **方案二（Profiles）** 來同時啟動多個 `api-microservice` 實例，以驗證負載平衡功能。

架構包括：

- **`api-microservice`**：提供資料處理介面的微服務，包含測試案例。
- **`eureka-server`**：服務註冊中心，管理微服務實例的註冊和發現，包含測試案例。
- **`api-gateway`**：API 閘道，作為統一入口，處理路由、斷路器、負載平衡等功能，包含測試案例。

---

## **先決條件**

- **Java 開發套件 (JDK)**：**Java 17**
- **Apache Maven**：建議使用 **3.6.x** 或以上版本
- **IDE**：如 **IntelliJ IDEA**、**Eclipse**、**VS Code** 等
- **資料庫**：Oracle 資料庫（`api-microservice` 使用）
- **Maven 和 IDE 設定使用 Java 17**
- **JUnit 5**：用於撰寫和執行測試案例

---

## **專案結構**

```
├── api-microservice
│   ├── pom.xml
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com.example
│   │   │   │       ├── ApiMicroserviceApplication.java
│   │   │   │       ├── controller
│   │   │   │       │   └── DataController.java
│   │   │   │       ├── entity
│   │   │   │       │   └── ZtEdgeCl03Data.java
│   │   │   │       ├── repository
│   │   │   │       │   └── ZtEdgeCl03DataRepository.java
│   │   │   │       └── config
│   │   │   │           └── OpenApiConfig.java
│   │   │   └── resources
│   │   │       ├── application.properties
│   │   │       └── application-8082.properties
│   │   └── test
│   │       └── java
│   │           └── com.example
│   │               └── controller
│   │                   └── DataControllerTest.java
├── eureka-server
│   ├── pom.xml
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com.example
│   │   │   │       └── EurekaServerApplication.java
│   │   │   └── resources
│   │   │       └── application.properties
│   │   └── test
│   │       └── java
│   │           └── com.example
│   │               └── EurekaServerApplicationTests.java
└── api-gateway
    ├── pom.xml
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── com.example
    │   │   │       ├── ApiGatewayApplication.java
    │   │   │       └── controller
    │   │   │           └── FallbackController.java
    │   │   └── resources
    │   │       └── application.properties
    │   └── test
    │       └── java
    │           └── com.example
    │               └── ApiGatewayApplicationTests.java
```

---

## **建立 `api-microservice` 微服務**

### **建立專案目錄**

```bash
mkdir api-microservice
cd api-microservice
```

### **設定 `pom.xml`**

在專案根目錄下編輯 **`pom.xml`**，內容如下：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="
             http://maven.apache.org/POM/4.0.0 
             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>api-microservice</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>api-microservice</name>
    <description>API Microservice Project</description>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.2</version> <!-- Spring Boot 3.x -->
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2022.0.4</spring-cloud.version> <!-- Spring Cloud 2022.0.x -->
    </properties>

    <dependencies>
        <!-- Spring Boot Web Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Data JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- Oracle JDBC Driver -->
        <dependency>
            <groupId>com.oracle.database.jdbc</groupId>
            <artifactId>ojdbc8</artifactId>
            <version>19.19.0.0</version>
        </dependency>

        <!-- Resilience4j for Circuit Breaker -->
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot3</artifactId>
            <version>2.0.2</version>
        </dependency>

        <!-- Spring Cloud Netflix Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- SpringDoc OpenAPI 3 -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.2.0</version>
        </dependency>

        <!-- Lombok (可選) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.28</version>
            <scope>provided</scope>
        </dependency>

        <!-- Spring Boot Configuration Processor -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Starter Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Cloud Dependencies -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>

            <!-- Spring Boot Maven Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**注意事項**：

- **Spring Boot 3** 需要 **Java 17** 或以上版本。
- **Spring Cloud 2022.0.x**（代號 **Kilburn**）與 **Spring Boot 3.x** 相容。
- **Resilience4j** 需要使用 **`resilience4j-spring-boot3`** 模組，版本為 **`2.x`**。
- **OpenAPI 3** 使用 **SpringDoc**，取代 Swagger 2。

### **建立主應用程式類別**

在 `src/main/java/com/example` 目錄下，建立 **`ApiMicroserviceApplication.java`**：

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiMicroserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiMicroserviceApplication.class, args);
    }
}
```

### **設定資料庫連線**

在 `src/main/resources` 目錄下，建立 **`application.properties`**：

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

**注意**：將 `<資料庫地址>`、`<埠號>`、`<SID>`、`<資料庫使用者名稱>` 和 `<資料庫密碼>` 替換為實際的資料庫資訊。

### **建立實體類別**

在 `src/main/java/com/example/entity` 目錄下，建立 **`ZtEdgeCl03Data.java`**：

```java
package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ZT_EDGE_CL03_DATA")
@Data
public class ZtEdgeCl03Data {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oid_generator")
    @SequenceGenerator(name = "oid_generator", sequenceName = "ZT_EDGE_CL03_DATA_SEQ", allocationSize = 1)
    @Column(name = "OID")
    private Long oid;

    @Column(name = "TX_AMT")
    private Double txAmt;

    @Column(name = "TX_DATE")
    private LocalDateTime txDate;

    @Column(name = "SING")
    private String sing;

    @Column(name = "VERSION", nullable = false)
    private String version = "0";

    @Column(name = "STS", nullable = false)
    private String sts = "1";

    @Column(name = "CREATOR")
    private String creator;

    @Column(name = "CREATETIME", nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();
}
```

**注意**：在 Spring Boot 3 中，JPA 使用 `jakarta.persistence` 套件，需要更新導入語句。

### **建立 Repository 介面**

在 `src/main/java/com/example/repository` 目錄下，建立 **`ZtEdgeCl03DataRepository.java`**：

```java
package com.example.repository;

import com.example.entity.ZtEdgeCl03Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZtEdgeCl03DataRepository extends JpaRepository<ZtEdgeCl03Data, Long> {
}
```

### **建立 Controller**

在 `src/main/java/com/example/controller` 目錄下，建立 **`DataController.java`**：

```java
package com.example.controller;

import com.example.entity.ZtEdgeCl03Data;
import com.example.repository.ZtEdgeCl03DataRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
@Tag(name = "資料介面", description = "處理資料的 API")
public class DataController {

    @Autowired
    private ZtEdgeCl03DataRepository repository;

    @PostMapping
    @Operation(summary = "新增資料", description = "接收 JSON 資料並寫入資料庫")
    @CircuitBreaker(name = "dataService", fallbackMethod = "fallbackCreateData")
    public ZtEdgeCl03Data createData(@RequestBody ZtEdgeCl03Data data) {
        return repository.save(data);
    }

    public ZtEdgeCl03Data fallbackCreateData(ZtEdgeCl03Data data, Throwable throwable) {
        // 斷路器觸發後的處理邏輯，可以返回預設值或錯誤訊息
        data.setOid(-1L);
        return data;
    }
}
```

### **設定 OpenAPI 3 (Swagger)**

在 Spring Boot 3 中，使用 **SpringDoc** 來生成 OpenAPI 3 文件。

在 `src/main/java/com/example/config` 目錄下，建立 **`OpenApiConfig.java`**：

```java
package com.example.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {                                    
    @Bean
    public OpenAPI apiInfo() { 
        return new OpenAPI()
            .info(new Info().title("API Microservice")
            .description("API 微服務文件")
            .version("v1.0"))
            .externalDocs(new ExternalDocumentation()
            .description("專案首頁")
            .url("http://localhost:8080"));
    }
}
```

**訪問地址**：

- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI 文件：`http://localhost:8080/v3/api-docs`

### **實作斷路器機制**

在 `DataController.java` 中，已經添加了 Resilience4j 的斷路器註解。

在 `application.properties` 中，已添加 Resilience4j 的設定。

### **新增測試案例**

#### **1. 建立測試類別**

在 `src/test/java/com/example/controller` 目錄下，建立 **`DataControllerTest.java`**：

```java
package com.example.controller;

import com.example.entity.ZtEdgeCl03Data;
import com.example.repository.ZtEdgeCl03DataRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ZtEdgeCl03DataRepository repository;

    @Test
    public void testCreateData() {
        // 模擬資料
        ZtEdgeCl03Data data = new ZtEdgeCl03Data();
        data.setTxAmt(100.0);
        data.setSing("test");
        data.setCreator("tester");

        // 模擬 repository.save() 方法
        Mockito.when(repository.save(any(ZtEdgeCl03Data.class))).thenReturn(data);

        // 發送 POST 請求
        webTestClient.post()
                .uri("/api/data")
                .bodyValue(data)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ZtEdgeCl03Data.class)
                .value(responseData -> {
                    assertEquals(data.getTxAmt(), responseData.getTxAmt());
                    assertEquals(data.getSing(), responseData.getSing());
                });
    }
}
```

**注意**：由於 `api-microservice` 是基於 Spring MVC 的，您可以使用 `TestRestTemplate` 或 `WebTestClient` 進行測試。

#### **2. 執行測試**

在專案根目錄下，執行以下命令運行測試：

```bash
mvn test
```

---

## **建立 `eureka-server` 服務註冊中心**

### **建立專案目錄**

```bash
mkdir eureka-server
cd eureka-server
```

### **設定 `pom.xml`**

編輯 **`pom.xml`**，內容如下：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="
             http://maven.apache.org/POM/4.0.0 
             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>eureka-server</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>eureka-server</name>
    <description>Eureka Server Project</description>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.2</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2022.0.4</spring-cloud.version>
    </properties>

    <dependencies>
        <!-- Spring Cloud Netflix Eureka Server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>

        <!-- Spring Boot Starter Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Lombok (可選) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.28</version>
            <scope>provided</scope>
        </dependency>

        <!-- Spring Boot Starter Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Cloud Dependencies -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>

            <!-- Spring Boot Maven Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### **建立主應用程式類別**

在 `src/main/java/com/example` 目錄下，建立 **`EurekaServerApplication.java`**：

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

### **設定 `application.properties`**

在 `src/main/resources` 目錄下，建立 **`application.properties`**：

```properties
server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

# Eureka 服務端設定（可選）
eureka.instance.hostname=localhost
```

### **新增測試案例**

#### **1. 建立測試類別**

在 `src/test/java/com/example` 目錄下，建立 **`EurekaServerApplicationTests.java`**：

```java
package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EurekaServerApplicationTests {

    @Test
    void contextLoads() {
        // 測試應用程式上下文是否成功加載
    }
}
```

#### **2. 執行測試**

在專案根目錄下，執行以下命令運行測試：

```bash
mvn test
```

---

## **建立 `api-gateway` API 閘道**

### **建立專案目錄**

```bash
mkdir api-gateway
cd api-gateway
```

### **設定 `pom.xml`**

編輯 **`pom.xml`**，內容如下：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="
             http://maven.apache.org/POM/4.0.0 
             https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>api-gateway</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>api-gateway</name>
    <description>API Gateway Project</description>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.2</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2022.0.4</spring-cloud.version>
    </properties>

    <dependencies>
        <!-- Spring Cloud Gateway -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>

        <!-- Spring Cloud Netflix Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- Spring Cloud Circuit Breaker with Resilience4j for Gateway -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
        </dependency>

        <!-- Lombok (可選) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.28</version>
            <scope>provided</scope>
        </dependency>

        <!-- Spring Boot Starter Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- Reactor Test -->
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Cloud Dependencies -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <!-- Maven Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>

            <!-- Spring Boot Maven Plugin -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**注意事項**：

- **新增了 `spring-cloud-starter-circuitbreaker-reactor-resilience4j` 依賴**，這是為了讓 Gateway 支援 Resilience4j 的斷路器功能。
- **移除了 `resilience4j-spring-boot3` 依賴**，因為對於 Gateway 來說，應該使用 Spring Cloud 提供的 Starter。

### **建立主應用程式類別**

在 `src/main/java/com/example` 目錄下，建立 **`ApiGatewayApplication.java`**：

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

### **設定 `application.properties`**

在 `src/main/resources` 目錄下，建立 **`application.properties`**：

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

**注意**：

- **`CircuitBreaker`** 過濾器的名稱需要與 Resilience4j 斷路器實例名稱匹配，例如 `apiMicroserviceCircuitBreaker`。

### **設定路由和斷路器**

在 `application.properties` 中，已經添加了路由和斷路器的設定。

### **新增 Fallback Controller**

在 `src/main/java/com/example/controller` 目錄下，建立 **`FallbackController.java`**：

```java
package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public String fallback() {
        return "服務暫時不可用，請稍後再試。";
    }
}
```

### **新增測試案例**

#### **1. 建立測試類別**

在 `src/test/java/com/example` 目錄下，建立 **`ApiGatewayApplicationTests.java`**：

```java
package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        // 測試應用程式上下文是否成功加載
    }
}
```

#### **2. 執行測試**

在專案根目錄下，執行以下命令運行測試：

```bash
mvn test
```

---

## **啟動順序**

1. **啟動 Eureka Server**

    ```bash
    cd eureka-server
    mvn clean install
    mvn spring-boot:run
    ```

    Eureka Server 將在埠號 **`8761`** 上運行。

2. **啟動 API 微服務**

    ```bash
    cd ../api-microservice
    mvn clean install
    mvn spring-boot:run
    ```

    微服務將在埠號 **`8080`** 上運行，並註冊到 Eureka Server。

3. **啟動 API Gateway**

    ```bash
    cd ../api-gateway
    mvn clean install
    mvn spring-boot:run
    ```

    API Gateway 將在埠號 **`8081`** 上運行。

---

## **驗證服務**

### **訪問 Eureka Server 控制台**

在瀏覽器中訪問 **`http://localhost:8761`**，查看註冊的服務實例。

### **通過 API Gateway 訪問微服務**

使用 Postman 或其他 API 客戶端，向以下 URL 發送請求：

- **URL**：`http://localhost:8081/api/data`
- **方法**：POST
- **Body**：JSON 格式，包含需要寫入的資料。

範例請求：

```json
{
    "txAmt": 100.0,
    "txDate": "2023-01-01T10:00:00",
    "sing": "example",
    "creator": "user1"
}
```

### **驗證斷路器功能**

- **停止 API 微服務**

    ```bash
    cd api-microservice
    mvn spring-boot:stop
    ```

- **再次發送請求**

    - 預期收到回退訊息，如 `"服務暫時不可用，請稍後再試。"`。

- **查看 API Gateway 日誌**

    - 確認斷路器已經觸發。

### **驗證負載平衡**

#### **Profiles 啟動多個 API 微服務實例**

**1. 創建新的 Profiles 配置檔**

在 `src/main/resources` 目錄下，創建 **`application-8082.properties`**，內容如下：

```properties
# 繼承 application.properties 中的配置
# 只需覆蓋需要修改的參數

# 伺服器埠號
server.port=8082

# Eureka 客戶端設定
eureka.instance.instance-id=api-microservice-8082
```

**2. 啟動第一個實例（埠號 8080）**

```bash
cd api-microservice
mvn spring-boot:run
```

- **說明**：使用默認的 `application.properties`，服務運行在埠號 `8080`。

**3. 啟動第二個實例（埠號 8082）**

```bash
cd api-microservice
mvn spring-boot:run -Dspring-boot.run.profiles=8082
```

- **說明**：指定使用 `application-8082.properties`，服務運行在埠號 `8082`。

**4. 驗證**

- **訪問 Eureka Server 控制台**

    - 應該能夠看到兩個 `api-microservice` 實例已註冊。

- **發送多次請求**

    ```bash
    curl -X POST http://localhost:8081/api/data \
    -H "Content-Type: application/json" \
    -d '{"txAmt":100.0,"txDate":"2023-01-01T10:00:00","sing":"example","creator":"user1"}'
    ```

- **觀察**

    - 查看兩個 `api-microservice` 實例的日誌，確認請求被分發到不同的實例，證明負載平衡生效。

---

## **更新 README**

### **[README 文件](README.md)**

請參閱更新後的 [README.md](README.md) 文件，該文件包含了詳細的建置指南、設定說明和使用方法，並特別說明了如何使用 Profiles 同時啟動多個 `api-microservice` 實例。

---

## **總結**

通過以上步驟，您已經成功地建置了一個基於 **Spring Boot 3**、**Java 17** 的微服務架構，並包含了 **JUnit 測試案例**。架構包括：

- **`api-microservice`**：微服務應用程式，提供資料處理介面，包含測試案例。
- **`eureka-server`**：服務註冊中心，管理微服務實例的註冊和發現，包含測試案例。
- **`api-gateway`**：API 閘道，作為統一入口，處理路由、斷路器、負載平衡等功能，包含測試案例。

**您選擇了使用方案二（Profiles）來同時啟動多個 `api-microservice` 實例，並成功驗證了負載平衡功能。**

**注意事項**：

- **版本相容性**：確保所有依賴和插件的版本與 **Spring Boot 3**、**Java 17** 以及 **JUnit 5** 相容。
- **設定正確性**：仔細檢查 `application.properties` 和 `pom.xml` 文件，確保設定正確。
- **網路和防火牆**：如果在伺服器環境中部署，注意開放相應的埠號。
- **安全性**：在生產環境中，考慮添加身份驗證、授權等安全機制。

**後續改進**：

- **設定中心**：可以引入 Spring Cloud Config Server，實現集中化設定管理。
- **服務監控**：使用 Spring Boot Actuator 和監控平台（如 Prometheus、Grafana）監控服務健康狀況。
- **日誌聚合**：引入 ELK（Elasticsearch、Logstash、Kibana）等日誌聚合方案，方便日誌分析。

---

**如有任何問題或需要進一步的協助，請隨時提出！**
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

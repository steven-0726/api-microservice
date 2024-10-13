package com.example.controller;

import com.example.entity.ZtEdgeCl03Data;
import com.example.repository.ZtEdgeCl03DataRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

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
        ResponseEntity<ZtEdgeCl03Data> response = restTemplate.postForEntity("/api/data", data, ZtEdgeCl03Data.class);

        // 驗證回應
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(data.getTxAmt(), response.getBody().getTxAmt());
        assertEquals(data.getSing(), response.getBody().getSing());
    }
}

package org.ctc.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProxyController {



    private final RestTemplate restTemplate;

    public ProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5")
    public Object proxyToTargetApi(@RequestBody MultiValueMap<String, String> formData) {
        // 目標 API 的 URL
        String targetApiUrl = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";

        // 設置請求頭
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 創建 form data
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.addAll(formData);

        // 使用 RestTemplate 發送 POST 請求到目標 API
        return restTemplate.postForObject(targetApiUrl, new HttpEntity<>(requestBody, headers), Object.class);
    }
}

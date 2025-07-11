package com.huha.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;

@RestController
@RequestMapping("/api/avatar")
public class AvatarController {
    
    private final RestTemplate restTemplate;
    private static final String DICEBEAR_API = "https://api.dicebear.com/7.x/avataaars/svg";

    public AvatarController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping
    public ResponseEntity<byte[]> getAvatar(@RequestParam String seed) {
        String url = DICEBEAR_API + "?seed=" + seed;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "image/svg+xml");
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<byte[]> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            byte[].class
        );
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "image/svg+xml");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        responseHeaders.set("Cross-Origin-Resource-Policy", "cross-origin");
        responseHeaders.set("Cross-Origin-Embedder-Policy", "credentialless");
        
        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }
} 
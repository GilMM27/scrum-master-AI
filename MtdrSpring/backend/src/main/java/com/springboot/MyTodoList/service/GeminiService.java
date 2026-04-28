package com.springboot.MyTodoList.service;

import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;

@Service
public class GeminiService {
    private final CloseableHttpClient httpClient;
    private final String geminiApiUrl;
    private final ObjectMapper objectMapper;

    public GeminiService(CloseableHttpClient httpClient, @Qualifier("geminiApiUrl") String geminiApiUrl, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.geminiApiUrl = geminiApiUrl;
        this.objectMapper = objectMapper;
    }

    public String generateText(String prompt) throws IOException, org.apache.hc.core5.http.ParseException {
        Map<String, Object> parts = Map.of("text", prompt);
        Map<String, Object> contents = Map.of("parts", List.of(parts));
        Map<String, Object> body = Map.of("contents", List.of(contents));
        
        String requestBody = objectMapper.writeValueAsString(body);

        HttpPost request = new HttpPost(geminiApiUrl);
        request.addHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }
}

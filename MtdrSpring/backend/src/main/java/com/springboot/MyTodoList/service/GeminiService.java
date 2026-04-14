package com.springboot.MyTodoList.service;

import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {
    private final CloseableHttpClient httpClient;
    private final String geminiApiUrl;

    public GeminiService(CloseableHttpClient httpClient, @Qualifier("geminiApiUrl") String geminiApiUrl) {
        this.httpClient = httpClient;
        this.geminiApiUrl = geminiApiUrl;
    }

    public String generateText(String prompt) throws IOException, org.apache.hc.core5.http.ParseException {
        String requestBody = String.format(
            "{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}",
            prompt
        );

        HttpPost request = new HttpPost(geminiApiUrl);
        request.addHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(requestBody));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return EntityUtils.toString(response.getEntity());
        }
    }
}

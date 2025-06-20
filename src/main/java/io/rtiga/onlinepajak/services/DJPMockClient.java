package io.rtiga.onlinepajak.services;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Service
public class DJPMockClient {

    private final RestTemplate restTemplate;

    public DJPMockClient() {
        this.restTemplate = new RestTemplate();
        // Make sure it handles XML as a string
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public String fetchFakturXml() {
        // Simulate hitting external DJP endpoint (change URL if needed)
        String url = "http://localhost:8080/djp-mock";
        return restTemplate.getForObject(url, String.class);
    }
}

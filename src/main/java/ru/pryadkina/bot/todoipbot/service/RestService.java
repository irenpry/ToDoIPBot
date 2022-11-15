package ru.pryadkina.bot.todoipbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@PropertySource("application.properties")
public class RestService {

    @Value("${todolist.url}")
    private String toDoListUrl;

    public String sendGetRequest(Map<String,Object> params, String endpoint) {
        RestTemplate restTemplate = new RestTemplate();

        String url = toDoListUrl + endpoint;
        return restTemplate.getForObject(url, String.class, params);
    }

    public String sendPostRequest(Map<String,Object> body, String endpoint) {
        RestTemplate restTemplate = new RestTemplate();

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String,Object>> request = new HttpEntity<>(body);
        String url = toDoListUrl + endpoint;
        return restTemplate.postForObject(url, request, String.class);
    }

    public String sendPatchRequest(Map<String,Object> body, String endpoint) {
        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        restTemplate.setRequestFactory(requestFactory);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String,Object>> request = new HttpEntity<>(body);
        String url = toDoListUrl + endpoint;
        return restTemplate.patchForObject(url, request, String.class);
    }

}

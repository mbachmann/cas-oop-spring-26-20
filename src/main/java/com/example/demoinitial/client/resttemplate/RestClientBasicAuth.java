package com.example.demoinitial.client.resttemplate;

import com.example.demoinitial.domain.Person;
import com.example.demoinitial.utils.HasLogger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Backend must be running
 */
public class RestClientBasicAuth implements HasLogger {
    public static void main(String[] args) {
        new RestClientBasicAuth().start();
    }
    public void start() {
        final String uri = "http://localhost:8080/api/persons/1";
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(createHeaders("admin@example.com", "admin"));
        ResponseEntity<Person> response = restTemplate.exchange(uri, HttpMethod.GET, request, Person.class);
        Person person = response.getBody();
        getLogger().info(String.valueOf(person));
    }

    HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
            String authHeader = "Basic " + new String(encodedAuth, StandardCharsets.US_ASCII);
            set("Authorization", authHeader);
        }};
    }
}

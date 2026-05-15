package com.example.demoinitial.client.websocket;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 * Stand alone WebSocketStompClient.
 */
public final class StompClientBasicAuth {

    private StompClientBasicAuth() {
    }

    public static void main(String[] args) {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport( new StandardWebSocketClient()) );
        WebSocketClient client = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

        WebSocketHttpHeaders webSocketHeaders = createWebSocketHeaders("admin@example.com", "admin");
        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        String url = "ws://localhost:8080/broadcast";
        stompClient.connectAsync(url, webSocketHeaders, sessionHandler);
        // Don't close immediately - Type <Enter> to exit
        new Scanner(System.in).nextLine();
    }

    static WebSocketHttpHeaders createWebSocketHeaders(String username, String password) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", getBase64Auth(username, password));
        return headers;
    }

    private static String getBase64Auth (String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));

        return "Basic " + new String(encodedAuth, StandardCharsets.US_ASCII);
    }

}

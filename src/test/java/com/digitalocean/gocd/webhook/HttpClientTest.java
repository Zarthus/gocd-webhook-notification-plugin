package com.digitalocean.gocd.webhook;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class HttpClientTest {

    static class EchoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = String.format("[echo] %s - ", t.getRequestMethod());
            try (InputStream input = t.getRequestBody()) {
                response += IOUtils.toString(input, StandardCharsets.UTF_8);
            }
            t.sendResponseHeaders(200, response.length());
            try (OutputStream output = t.getResponseBody()) {
                IOUtils.write(response, output, StandardCharsets.UTF_8);
            }
        }
    }

    private HttpServer server;
    private int serverPort;

    @Before
    public void startHttpServer() throws Exception {
        // get the jvm to allocate a random free port
        ServerSocket s = new ServerSocket(0);
        serverPort = s.getLocalPort();
        s.close();

        server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/echo", new EchoHandler());
        server.setExecutor(null); // create a default executor
        server.start();
    }

    @After
    public void stopHttpServer() {
        server.stop(1);
    }

    @Test
    public void postToEndpoint() throws Exception {
        String endpoint = String.format("http://127.0.0.1:%s/echo", serverPort);
        HttpClient client = new HttpClient();
        String response = client.postToEndpoint(endpoint, "wibble");
        assertEquals("[echo] POST - wibble", response);
    }
}

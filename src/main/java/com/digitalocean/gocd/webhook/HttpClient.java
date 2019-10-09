package com.digitalocean.gocd.webhook;

import com.digitalocean.gocd.webhook.tls.TrustAllHttpsEndpoints;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    private final boolean trustAllHttps;

    // for subclassing in tests
    public HttpClient() {
        this(false);
    }

    public HttpClient(boolean trustAllHttps) {
        this.trustAllHttps = trustAllHttps;
    }

    public String postToEndpoint(String endpoint, String requestBody) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (endpoint.startsWith("https://") && trustAllHttps) {
            TrustAllHttpsEndpoints.configure(conn);
        }
        try {
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 30 sec timeout for connect & read
            conn.setConnectTimeout(30 * 1000);
            conn.setReadTimeout(30 * 1000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            try (OutputStream output = conn.getOutputStream()) {
                IOUtils.write(requestBody, output, StandardCharsets.UTF_8);
                output.flush();
            }
            String response;
            int responseCode = conn.getResponseCode();
            try (InputStream input = conn.getInputStream()) {
                response = IOUtils.toString(input, StandardCharsets.UTF_8);
            }
            if (responseCode / 100 != 2) {
                throw new Exception("Received " + responseCode + " from " + endpoint + " with message: " + response);
            }
            return response;
        } finally {
            conn.disconnect();
        }
    }
}

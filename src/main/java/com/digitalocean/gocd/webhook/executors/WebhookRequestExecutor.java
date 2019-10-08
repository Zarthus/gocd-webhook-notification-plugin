package com.digitalocean.gocd.webhook.executors;

import com.digitalocean.gocd.webhook.HttpClient;
import com.digitalocean.gocd.webhook.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class WebhookRequestExecutor implements RequestExecutor {

    private final List<String> endpoints;
    private final String requestBody;
    private final HttpClient sender;

    public WebhookRequestExecutor(List<String> endpoints, String requestBody) {
        this(endpoints, requestBody, new HttpClient());
    }

    public WebhookRequestExecutor(List<String> endpoints, String requestBody, HttpClient sender) {
        this.endpoints = endpoints;
        this.requestBody = requestBody;
        this.sender = sender;
    }

    @Override
    public GoPluginApiResponse execute() {
        JSONArray exceptions = new JSONArray();
        for (String endpoint : endpoints) {
            try {
                sender.postToEndpoint(endpoint, requestBody);
            } catch (Exception e) {
                exceptions.put(e.toString());
            }
        }
        JSONObject res = new JSONObject();
        if (exceptions.isEmpty()) {
            res.put("status", "success");
        } else {
            res.put("status", "failure");
            res.put("messages", exceptions);
        }
        return new DefaultGoPluginApiResponse(200, res.toString());
    }
}

package com.digitalocean.gocd.webhook.executors;

import com.digitalocean.gocd.webhook.HttpClient;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebhookRequestExecutorTest {

    @Test
    public void shouldSendRequestBodyToEachEndpoint() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<String> endpoints = Arrays.asList("http://example.com/alpha", "http://example.com/beta");

        WebhookRequestExecutor executor = new WebhookRequestExecutor(endpoints, "wibble", client);
        GoPluginApiResponse response = executor.execute();

        verify(client).postToEndpoint("http://example.com/alpha", "wibble");
        verify(client).postToEndpoint("http://example.com/beta", "wibble");

        assertEquals(200, response.responseCode());
        assertEquals("{\"status\":\"success\"}", response.responseBody());
    }

    @Test
    public void shouldHandleSingleEndpointFailure() throws Exception {
        HttpClient client = mock(HttpClient.class);
        List<String> endpoints = Arrays.asList("http://example.com/alpha", "http://example.com/beta");

        when(client.postToEndpoint("http://example.com/alpha", "wibble")).thenThrow(new Exception("oops"));

        WebhookRequestExecutor executor = new WebhookRequestExecutor(endpoints, "wibble", client);
        GoPluginApiResponse response = executor.execute();

        verify(client).postToEndpoint("http://example.com/beta", "wibble");

        assertEquals(200, response.responseCode());

        JSONObject obj = new JSONObject(response.responseBody());
        assertEquals("failure", obj.getString("status"));

        JSONArray messages = obj.getJSONArray("messages");
        assertEquals(1, messages.length());
        assertEquals("java.lang.Exception: oops", messages.getString(0));
    }
}

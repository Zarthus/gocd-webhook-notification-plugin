package com.digitalocean.gocd.webhook.executors;

import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GetViewRequestExecutorTest {

    @Test
    public void shouldReadTemplateFileOnClasspath() throws IOException {
        String template = GetViewRequestExecutor.readTemplate();
        assertThat(template, containsString("Plugin is configured by a property file."));
    }

    @Test
    public void shouldReturnTheTemplateResource() throws Exception {
        GoPluginApiResponse response = new GetViewRequestExecutor().execute();
        assertThat(response.responseCode(), is(200));

        JSONObject obj = new JSONObject(response.responseBody());
        assertThat(obj.getString("template"), is(GetViewRequestExecutor.readTemplate()));
    }
}

package com.digitalocean.gocd.webhook.executors;

import com.digitalocean.gocd.webhook.Resources;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONObject;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GetViewRequestExecutorTest {

    @Test
    public void shouldReturnTheTemplateResource() {
        GoPluginApiResponse response = new GetViewRequestExecutor().execute();
        assertThat(response.responseCode(), is(200));

        String expected = Resources.readResource("plugin-settings.template.html");

        JSONObject obj = new JSONObject(response.responseBody());
        assertThat(obj.getString("template"), is(expected));
    }
}

package com.digitalocean.gocd.webhook.executors;

import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ValidateConfigurationExecutorTest {

    @Test
    public void shouldReturnSuccessfulEmptyResponse() {
        GoPluginApiResponse response = new ValidateConfigurationExecutor().execute();
        assertThat(response.responseCode(), is(200));
        assertThat(response.responseBody(), is("[]"));
    }
}

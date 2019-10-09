package com.digitalocean.gocd.webhook;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {

    @Test
    public void shouldReadConfigFile() throws Exception {
        File configFilePath = new File(SystemUtils.getUserDir(), "src/test/resources/test_webhook_notify.properties");
        Configuration configuration = Configuration.loadConfiguration(configFilePath);

        List<String> endpoints = configuration.getStageEndpoints();
        assertThat(endpoints.size(), is(2));
        assertThat(endpoints, containsInAnyOrder("http://example.com/stage/alpha", "http://example.com/stage/beta"));

        endpoints = configuration.getAgentEndpoints();
        assertThat(endpoints.size(), is(2));
        assertThat(endpoints, containsInAnyOrder("http://example.com/agent/alpha", "http://example.com/agent/beta"));
    }
}

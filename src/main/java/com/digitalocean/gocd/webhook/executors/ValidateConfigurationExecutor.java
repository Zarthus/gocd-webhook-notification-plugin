package com.digitalocean.gocd.webhook.executors;

import com.digitalocean.gocd.webhook.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class ValidateConfigurationExecutor implements RequestExecutor {

    public GoPluginApiResponse execute() {
        return DefaultGoPluginApiResponse.success("[]");
    }
}

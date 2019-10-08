package com.digitalocean.gocd.webhook.executors;

import com.digitalocean.gocd.webhook.RequestExecutor;
import com.digitalocean.gocd.webhook.Resources;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONObject;

public class GetViewRequestExecutor implements RequestExecutor {

    @Override
    public GoPluginApiResponse execute() {
        JSONObject obj = new JSONObject();
        obj.put("template", Resources.readResource("plugin-settings.template.html"));
        return new DefaultGoPluginApiResponse(200, obj.toString());
    }
}

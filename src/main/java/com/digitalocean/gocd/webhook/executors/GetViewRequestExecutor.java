package com.digitalocean.gocd.webhook.executors;

import com.digitalocean.gocd.webhook.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GetViewRequestExecutor implements RequestExecutor {

    @Override
    public GoPluginApiResponse execute() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("template", readTemplate());
        return new DefaultGoPluginApiResponse(200, obj.toString());
    }

    static String readTemplate() throws IOException {
        return IOUtils.resourceToString("plugin-settings.template.html",
                StandardCharsets.UTF_8, GetViewRequestExecutor.class.getClassLoader());
    }
}

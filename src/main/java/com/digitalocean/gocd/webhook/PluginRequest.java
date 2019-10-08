package com.digitalocean.gocd.webhook;

/**
 * Enumerable that represents one of the messages that the server sends to the plugin
 */
public enum PluginRequest {
    // elastic agent related requests that the server makes to the plugin
    REQUEST_NOTIFICATIONS_INTERESTED_IN("notifications-interested-in"),
    REQUEST_STAGE_STATUS("stage-status"),
    REQUEST_AGENT_STATUS("agent-status"),

    // settings related requests that the server makes to the plugin
    PLUGIN_SETTINGS_GET_CONFIGURATION(Constants.GO_PLUGIN_SETTINGS_PREFIX + ".get-configuration"),
    PLUGIN_SETTINGS_GET_VIEW(Constants.GO_PLUGIN_SETTINGS_PREFIX + ".get-view"),
    PLUGIN_SETTINGS_VALIDATE_CONFIGURATION(Constants.GO_PLUGIN_SETTINGS_PREFIX + ".validate-configuration");

    private final String requestName;

    PluginRequest(String requestName) {
        this.requestName = requestName;
    }

    public String requestName() {
        return requestName;
    }

    public static PluginRequest fromString(String requestName) {
        if (requestName != null) {
            for (PluginRequest request : PluginRequest.values()) {
                if (requestName.equalsIgnoreCase(request.requestName)) {
                    return request;
                }
            }
        }
        return null;
    }
}

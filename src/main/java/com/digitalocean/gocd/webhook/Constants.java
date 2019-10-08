package com.digitalocean.gocd.webhook;

import com.thoughtworks.go.plugin.api.GoPluginIdentifier;

import java.util.Collections;

public interface Constants {
    // The type of this extension
    String EXTENSION_TYPE = "notification";

    // The extension point API version that this plugin understands
    String API_VERSION = "3.0";

    // The identifier of this plugin
    GoPluginIdentifier PLUGIN_IDENTIFIER = new GoPluginIdentifier(EXTENSION_TYPE, Collections.singletonList(API_VERSION));

    // The prefix for plugin settings requests
    String GO_PLUGIN_SETTINGS_PREFIX = "go.plugin-settings";
}

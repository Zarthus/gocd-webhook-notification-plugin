package com.digitalocean.gocd.webhook;

import com.thoughtworks.go.plugin.api.logging.Logger;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.unmodifiableList;

class Configuration {

    private static Logger LOGGER = Logger.getLoggerFor(Configuration.class);

    private static final String CRUISE_SERVER_DIR = "CRUISE_SERVER_DIR";
    private static final String WEBHOOK_NOTIFY_CONF = "WEBHOOK_NOTIFY_CONF";

    private static final String CONFIG_FILE_NAME = "webhook_notify.conf";
    private static final String HOME_PLUGIN_CONFIG_PATH = System.getProperty("user.home") + File.separator + CONFIG_FILE_NAME;

    /*
     * Adapted from https://github.com/ashwanthkumar/gocd-slack-build-notifier/blob/master/src/main/java/in/ashwanthkumar/gocd/slack/GoNotificationPlugin.java#L179
     */
    private static File findConfigFile() {
        File pluginConfig;
        // case 1: Look for an environment variable by WEBHOOK_NOTIFY_CONF and if a file identified by the value exist
        String goNotifyConfPath = System.getenv(WEBHOOK_NOTIFY_CONF);
        if (StringUtils.isNotEmpty(goNotifyConfPath)) {
            pluginConfig = new File(goNotifyConfPath);
            if (pluginConfig.exists()) {
                LOGGER.info(String.format("Configuration file found using WEBHOOK_NOTIFY_CONF at %s", pluginConfig.getAbsolutePath()));
                return pluginConfig;
            }
        }
        // case 2: Look for a file called webhook_notify.conf in the home folder
        pluginConfig = new File(HOME_PLUGIN_CONFIG_PATH);
        if (pluginConfig.exists()) {
            LOGGER.info(String.format("Configuration file found at Home Dir as %s", pluginConfig.getAbsolutePath()));
            return pluginConfig;
        }
        // case 3: Look for a file - webhook_notify.conf in the current working directory of the server
        String goServerDir = System.getenv(CRUISE_SERVER_DIR);
        pluginConfig = new File(goServerDir + File.separator + CONFIG_FILE_NAME);
        if (pluginConfig.exists()) {
            LOGGER.info(String.format("Configuration file found using CRUISE_SERVER_DIR at %s", pluginConfig.getAbsolutePath()));
            return pluginConfig;
        }
        throw new RuntimeException("Unable to find webhook_notify.conf. Please make sure you've set it up right.");
    }

    private static Configuration loadConfiguration(File pluginConfig) throws Exception {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(pluginConfig)) {
            props.load(input);
        }
        List<String> stageEndpoints = new ArrayList<>();
        List<String> agentEndpoints = new ArrayList<>();
        Enumeration<?> names = props.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            if (name.startsWith("stage.status.endpoint.")) {
                stageEndpoints.add(props.getProperty(name));
            } else if (name.startsWith("agent.status.endpoint.")) {
                agentEndpoints.add(props.getProperty(name));
            }
        }
        return new Configuration(unmodifiableList(stageEndpoints), unmodifiableList(agentEndpoints));
    }

    private static Configuration current;
    private static long lastModified;
    private static File configFile;

    static Configuration getCurrent() throws Exception {
        if (current != null && lastModified == configFile.lastModified()) {
            return current;
        }
        if (configFile == null) {
            configFile = findConfigFile();
        }
        lastModified = configFile.lastModified();
        current = loadConfiguration(configFile);
        return current;
    }

    private final List<String> stageEndpoints;
    private final List<String> agentEndpoints;

    private Configuration(List<String> stageEndpoints, List<String> agentEndpoints) {
        this.stageEndpoints = stageEndpoints;
        this.agentEndpoints = agentEndpoints;
    }

    List<String> stageStatusEndpoints() {
        return stageEndpoints;
    }

    List<String> agentStatusEndpoints() {
        return agentEndpoints;
    }
}

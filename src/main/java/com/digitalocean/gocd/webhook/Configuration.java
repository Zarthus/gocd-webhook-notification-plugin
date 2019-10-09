package com.digitalocean.gocd.webhook;

import com.thoughtworks.go.plugin.api.logging.Logger;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Collections.unmodifiableList;

class Configuration {

    private static Logger LOGGER = Logger.getLoggerFor(Configuration.class);

    private static final String CRUISE_SERVER_DIR = "CRUISE_SERVER_DIR";
    private static final String WEBHOOK_NOTIFY_CONFIG = "WEBHOOK_NOTIFY_CONFIG";
    private static final String CONFIG_FILE_NAME = "webhook_notify.properties";

    /*
     * Adapted from https://github.com/ashwanthkumar/gocd-slack-build-notifier/blob/master/src/main/java/in/ashwanthkumar/gocd/slack/GoNotificationPlugin.java#L179
     */
    private static File findConfigFile() {
        File pluginConfig;
        // case 1: Look for an environment variable by WEBHOOK_NOTIFY_CONFIG and if a file identified by the value exist
        String configFilePath = System.getenv(WEBHOOK_NOTIFY_CONFIG);
        if (StringUtils.isNotEmpty(configFilePath)) {
            pluginConfig = new File(configFilePath);
            if (pluginConfig.exists()) {
                LOGGER.info(String.format("Configuration file found using WEBHOOK_NOTIFY_CONFIG at %s", pluginConfig.getAbsolutePath()));
                return pluginConfig;
            }
        }
        // case 2: Look for a file called webhook_notify.properties in the home folder
        pluginConfig = new File(SystemUtils.getUserHome(), CONFIG_FILE_NAME);
        if (pluginConfig.exists()) {
            LOGGER.info(String.format("Configuration file found at Home Dir as %s", pluginConfig.getAbsolutePath()));
            return pluginConfig;
        }
        // case 3: Look for a file - webhook_notify.properties in the current working directory of the server
        pluginConfig = new File(System.getenv(CRUISE_SERVER_DIR), CONFIG_FILE_NAME);
        if (pluginConfig.exists()) {
            LOGGER.info(String.format("Configuration file found using CRUISE_SERVER_DIR at %s", pluginConfig.getAbsolutePath()));
            return pluginConfig;
        }
        throw new RuntimeException("Unable to find webhook_notify.properties. Please make sure you've set it up right.");
    }

    static Configuration loadConfiguration(File pluginConfig) throws Exception {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(pluginConfig)) {
            props.load(input);
        }

        boolean trustAllHttps = BooleanUtils.toBoolean(props.getProperty("trust.all.https", "false"));
        HttpClient client = new HttpClient(trustAllHttps);

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
        return new Configuration(client, stageEndpoints, agentEndpoints);
    }

    private static final Lock lock = new ReentrantLock(true);
    private static Configuration current;
    private static long lastModified;
    private static File configFile;

    static Configuration getCurrent() throws Exception {
        lock.lock();
        try {
            if (current != null && lastModified == configFile.lastModified()) {
                return current;
            }
            if (configFile == null) {
                configFile = findConfigFile();
            }
            lastModified = configFile.lastModified();
            current = loadConfiguration(configFile);
            return current;
        } finally {
            lock.unlock();
        }
    }

    private final HttpClient client;
    private final List<String> stageEndpoints;
    private final List<String> agentEndpoints;

    private Configuration(HttpClient client, List<String> stageEndpoints, List<String> agentEndpoints) {
        this.stageEndpoints = unmodifiableList(stageEndpoints);
        this.agentEndpoints = unmodifiableList(agentEndpoints);
        this.client = client;
    }

    HttpClient getClient() {
        return client;
    }

    List<String> getStageEndpoints() {
        return stageEndpoints;
    }

    List<String> getAgentEndpoints() {
        return agentEndpoints;
    }
}

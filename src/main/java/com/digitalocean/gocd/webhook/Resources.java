package com.digitalocean.gocd.webhook;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Resources {

    public static String readResource(String resourceFile) {
        try {
            return IOUtils.resourceToString(resourceFile, StandardCharsets.UTF_8, Resources.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException("Could not read from classpath resource " + resourceFile, e);
        }
    }
}

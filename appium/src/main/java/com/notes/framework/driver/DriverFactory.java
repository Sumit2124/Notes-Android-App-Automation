package com.notes.framework.driver;

import com.notes.framework.config.Configuration;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class DriverFactory {
    public static AndroidDriver createDriver(Configuration config) throws Exception {
        String execution = config.required("execution");
        UiAutomator2Options options;
        URL server;
        if ("local".equalsIgnoreCase(execution)) {
            options = localOptions(config);
            server = URI.create(config.required("local.serverUrl")).toURL();
        } else if ("cloud".equalsIgnoreCase(execution)) {
            options = cloudOptions(config);
            server = cloudServer(config);
        } else {
            throw new IllegalArgumentException("execution must be local or cloud");
        }
        return new AndroidDriver(server, options);
    }

    private static UiAutomator2Options localOptions(Configuration config) {
        UiAutomator2Options options = commonOptions(config);
        options.setDeviceName(config.required("local.deviceName"));
        options.setAppPackage(config.required("local.appPackage"));
        options.setAppActivity(config.required("local.appActivity"));
        if (!config.get("local.udid").isEmpty()) {
            options.setUdid(config.get("local.udid"));
        }
        return options;
    }

    public static UiAutomator2Options cloudOptions(Configuration config) {
        UiAutomator2Options options = commonOptions(config);
        options.setDeviceName(config.required("cloud.deviceName"));
        if (!config.get("cloud.platformVersion").isEmpty()) {
            options.setPlatformVersion(config.get("cloud.platformVersion"));
        }
        if (!config.get("cloud.app").isEmpty()) {
            options.setApp(config.get("cloud.app"));
        }
        Properties properties = config.getProperties();
        String prefix = "cloud.capability.";
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                options.setCapability(key.substring(prefix.length()), config.required(key));
            }
        }
        return options;
    }

    private static UiAutomator2Options commonOptions(Configuration config) {
        String noReset = config.required("noReset");
        if (!"true".equalsIgnoreCase(noReset) && !"false".equalsIgnoreCase(noReset)) {
            throw new IllegalArgumentException("noReset must be true or false");
        }
        return new UiAutomator2Options().setNoReset(Boolean.parseBoolean(noReset));
    }

    private static URL cloudServer(Configuration config) throws Exception {
        URI uri = URI.create(config.required("cloud.serverUrl"));
        String username = System.getenv("CLOUD_USERNAME");
        String accessKey = System.getenv("CLOUD_ACCESS_KEY");
        if ((username == null || username.isEmpty()) != (accessKey == null || accessKey.isEmpty())) {
            throw new IllegalArgumentException("Set both CLOUD_USERNAME and CLOUD_ACCESS_KEY");
        }
        if (username != null && !username.isEmpty()) {
            uri = withCredentials(uri, username, accessKey);
        }
        return uri.toURL();
    }

    private static URI withCredentials(URI uri, String username, String accessKey) throws URISyntaxException {
        return new URI(uri.getScheme(), username + ":" + accessKey, uri.getHost(), uri.getPort(),
                uri.getPath(), uri.getQuery(), uri.getFragment());
    }
}

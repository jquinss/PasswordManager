package com.jquinss.passwordmanager.config;

import com.jquinss.passwordmanager.util.misc.OSChecker;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = AppConfig.class.getResourceAsStream("/com/jquinss/passwordmanager/config/config.properties")) {
            props.load(in);
            for (Map.Entry<Object, Object> e : props.entrySet()) {
                props.setProperty((String) e.getKey(), buildFullPath((String) e.getValue()));
            }
        }
        catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    private static String buildFullPath(String path) {
        String dataDir = OSChecker.getOSDataDirectory();
        String[] folders = path.split("\\\\");
        return Path.of(dataDir, folders).toString();
    }
}

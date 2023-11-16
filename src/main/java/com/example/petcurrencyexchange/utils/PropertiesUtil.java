package com.example.petcurrencyexchange.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }
    private PropertiesUtil() {

    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
    public static void loadProperties(){
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            PROPERTIES.load(inputStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

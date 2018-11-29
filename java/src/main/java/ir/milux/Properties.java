package ir.milux;

import java.io.FileInputStream;

public class Properties {
    static final String configfileaddress = "/etc/otpw/otpw-config.properties";

    public static String getProperty(String property) {
        FileInputStream inputStream = null;
        java.util.Properties properties = new java.util.Properties();
        try {
            inputStream = new FileInputStream(configfileaddress);
            properties.load(inputStream);

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties.getProperty(property);
    }
}

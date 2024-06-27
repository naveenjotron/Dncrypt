package baseCodes;

import java.io.*;
import java.util.Properties;

public class PreferencesManager {
    private static final String CONFIG_FILE_NAME = "preferences.properties";
    private static final String DISCLAIMER_KEY = "disclaimerShown";
    private static final String TIMESTAMP_KEY = "timestamp";

    private Properties properties;
    private File configFile;

    public PreferencesManager(String appDirectory) {
        properties = new Properties();
        configFile = new File(appDirectory+"/assets/preferences/", CONFIG_FILE_NAME);

        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDisclaimerShown() {
        String disclaimerShown = properties.getProperty(DISCLAIMER_KEY, "false");
        return Boolean.parseBoolean(disclaimerShown);
    }

    public void setDisclaimerShown(boolean shown) {
        properties.setProperty(DISCLAIMER_KEY, Boolean.toString(shown));
        properties.setProperty(TIMESTAMP_KEY, Long.toString(System.currentTimeMillis()));
        saveProperties();
    }

    public long getTimestamp() {
        String timestamp = properties.getProperty(TIMESTAMP_KEY, "0");
        return Long.parseLong(timestamp);
    }

    private void saveProperties() {
        try (OutputStream output = new FileOutputStream(configFile)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


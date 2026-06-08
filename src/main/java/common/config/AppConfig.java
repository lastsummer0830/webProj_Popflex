package common.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AppConfig {

	private static final String CONFIG_FILE_NAME = "config.properties";
    private static final char UTF8_BOM = '\uFEFF';
    private static final Properties props = new Properties();

    static {
        try (InputStream input = AppConfig.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE_NAME)) {

            if (input == null) {
                throw new RuntimeException(
                    "config.properties 파일을 찾을 수 없습니다.  src/main/resources/config.properties 위치를 확인하세요."
                );
            }

            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            normalizeBomKeys();

        } catch (IOException e) {
            throw new RuntimeException("config.properties 읽기 실패", e);
        }
    }
    private static void normalizeBomKeys() {
        for (String key : props.stringPropertyNames()) {
            if (!key.isEmpty() && key.charAt(0) == UTF8_BOM) {
                String normalizedKey = key.substring(1);
                if (!props.containsKey(normalizedKey)) {
                    props.setProperty(normalizedKey, props.getProperty(key));
                }
                props.remove(key);
            }
        }
    }

    private static String getRequired(String key) {
    	String value = props.getProperty(key);
    	
    	if(value == null || value.trim().isEmpty()) {
    		throw new RuntimeException("config.properties에 '" + key + "' 값이 없습니다.");
    	}
    	return value.trim();
    }
    
//  DB
    public static String getDbDriver() {
    	return getRequired("DB_DRIVER");
    }
    public static String getDbUrl() {
    	return getRequired("DB_URL");
    }
    public static String getDbUser() {
    	return getRequired("DB_USER");
    }
    public static String getDbPassword() {
    	return getRequired("DB_PASSWORD");
    }

//  KMDb API
    public static String getKmdbServiceKey() {
        return getRequired("KMDB_SERVICE_KEY");
    }

    public static String getKmdbApiUrl() {
        return getRequired("KMDB_API_URL");
    }

//  NAVER login
    public static String getNaverClientId() {
        return getRequired("NAVER_CLIENT_ID");
    }

    public static String getNaverClientSecret() {
        return getRequired("NAVER_CLIENT_SECRET");
    }

    public static String getNaverCallbackUrl() {
        return props.getProperty("NAVER_CALLBACK_URL");
    }
}

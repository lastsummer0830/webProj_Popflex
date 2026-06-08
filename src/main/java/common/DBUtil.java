package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import common.config.AppConfig;

public class DBUtil {

    static {
        try {
            Class.forName(AppConfig.getDbDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Oracle JDBC 드라이버 로드 실패", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
        		AppConfig.getDbUrl(),
        		AppConfig.getDbUser(),
        		AppConfig.getDbPassword()
        	);
    }

    public static void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

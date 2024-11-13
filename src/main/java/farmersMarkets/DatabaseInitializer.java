package farmersMarkets;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * The DatabaseInitializer class represents the initialization
 * of the database used in farmersMarkets.
 * @author Chev Kodama
 * @version 1.0
 */
public class DatabaseInitializer {
	/**
	 * Uses the db.properties file in the resources folder to
	 * access the local MySQL server application.
	 * Uses the farmers_markets_db.sql script in the resources
	 * folder to initialize the database, if it has not yet been so.
	 */
    public void initializeDatabase() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/db.properties")) {
        	properties.load(input);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            try (Connection connection = DriverManager.getConnection(url, user, password);
                 Statement statement = connection.createStatement()) {

                try (InputStream scriptStream = new FileInputStream("src/main/resources/farmers_markets_db.sql");
                     BufferedReader reader = new BufferedReader(new InputStreamReader(scriptStream))) {

                    StringBuilder sqlBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sqlBuilder.append(line).append("\n");
                    }

                    String[] sqlStatements = sqlBuilder.toString().split(";");

                    for (String sql : sqlStatements) {
                        if (!sql.trim().isEmpty()) {
                            statement.execute(sql.trim());
                        }
                    }
                } catch (IOException e) {
                    System.err.println("SQL script not found in the resources folder");
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                System.err.println("SQL error occurred.");
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("Error loading db.properties");
            e.printStackTrace();
        }
    }
}

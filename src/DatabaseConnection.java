import java.sql.*;

public class DatabaseConnection {
    // Paramètres de connexion déjà correctement configurés pour Oracle
    private static final String URL = "jdbc:oracle:thin:@//gaia.emp.uqtr.ca:1521/coursbd.uqtr.ca";
    private static final String USER = "SMI1002_029";
    private static final String PASSWORD = "26jvbd88";   
    
    private static Connection connection;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                if (connection != null) {
                    System.out.println("Connecté à la base de données avec succès !");
                } else {
                    System.out.println("Échec de la connexion à la base de données.");
                }

            } catch (ClassNotFoundException e) {
                System.out.println("Pilote JDBC Oracle introuvable.");
                e.printStackTrace();
                throw new SQLException("Pilote Oracle JDBC non trouvé", e);
            } catch (SQLException e) {
                System.out.println("Échec de la connexion.");
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion à la base de données fermée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la fermeture de la connexion.");
            e.printStackTrace();
        }
    }
}
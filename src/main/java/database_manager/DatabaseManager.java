package database_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.User;

/**
 * La classe consente di interagire col database usato per la
 * gestione del servizio web.
 */
public class DatabaseManager {
    private final String SERVER_URL = "jdbc:mariadb://truecloud.ddns.net:3306";
    private final String DATABASE_NAME = "contacts";
    private final String USER = "contacts";
    private final String PASSWORD = "Qny2U1lWz76jnqjg";
    private final Connection connection;

    public DatabaseManager() throws SQLException {
        String f = String.format("%s/%s?connectTimeout=0", SERVER_URL, DATABASE_NAME);
        this.connection = DriverManager.getConnection(f, USER, PASSWORD);      
    }

    /**
     * Controlla che le credenziali fornite appartengano a qualche utente.
     * 
     * @param user utente per il quale testare le credenziali
     * 
     * @return true se le credenziali identificano un solo utente, altrimenti false.
     *      Restituisce false anche se si verifica un errore durante l'esecuzione della query
     */
    public boolean testCredentials(User user) {
        try {
            PreparedStatement query = this.connection
                    .prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
            query.setString(1, user.getEmail());
            query.setString(2, user.getPassword());
            ResultSet result = query.executeQuery();
            if (result.last()) {
                return result.getRow() == 1; 
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Chiude la connessione con il database.
     * 
     * @throws SQLException errore durante la chiusura della connessione
     */
    public void close() throws SQLException {
        this.connection.close();
    }
}
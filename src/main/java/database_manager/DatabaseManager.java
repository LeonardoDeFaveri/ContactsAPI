package database_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import models.*;

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
                    .prepareStatement("SELECT test_credentials(?, ?)");
            query.setString(1, user.getEmail());
            query.setString(2, user.getPassword());
            ResultSet result = query.executeQuery();
                result.first();
            if (result.getBoolean(1)) {
                return true; 
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Registra un nuovo utente sul servizio. Crea sia l'utente che il
     * contatto associato.
     * 
     * @param contact contatto da registrare
     * 
     * @return id del contatto se è stato creato con successo,
     *     0 se l'utente esiste già e -1 se si è verificato un
     *     errore durante l'inserimento del contatto
     */
    public int registerUser(Contact contact) {
        try {
            PreparedStatement query = this.connection
                    .prepareStatement("SELECT insert_user(?, ?, ?, ?, ?)");
            query.setString(1, contact.getOwner().getEmail());
            query.setString(2, contact.getOwner().getPassword());
            query.setString(3, contact.getFirstName());
            query.setString(4, contact.getFamilyName());
            if (contact.getSecondName().equals("")) {
                query.setNull(5, Types.VARCHAR);
            } else {
                query.setString(5, contact.getSecondName());
            }
            ResultSet result = query.executeQuery();
            result.first();
            return result.getInt(1);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    /**
     * Inserisce un nuovo contatto.
     * 
     * @param contact contatto da inserire
     * 
     * @return id del contatto se è stato creato con successo
     *      o se esisteva già, altrimenti -1
     */
    public int insertContact(Contact contact) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT insert_contact(?, ?, ?, ?, ?)");
            query.setString(1, contact.getFirstName());
            query.setString(2, contact.getFamilyName());
            if (contact.getSecondName().equals("")) {
                query.setNull(3, Types.VARCHAR);
            } else {
                query.setString(3, contact.getSecondName());
            }
            query.setString(4, contact.getOwner().getEmail());
            if (contact.getAssociatedUser() == null) {
                query.setNull(5, Types.VARCHAR);
            } else {
                query.setString(5, contact.getAssociatedUser().getEmail());
            }
            ResultSet result = query.executeQuery();
            result.first();
            return result.getInt(1);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    /**
     * Inserisce un nuovo gruppo.
     * 
     * @param group gruppo da inserire
     * 
     * @return id del gruppo se è stato creato con successo
     *      o se esisteva già, altrimenti -1
     */
    public int insertGroup(Group group) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT insert_group(?, ?)");
            query.setString(1, group.getName());
            query.setString(2, group.getOwner().getEmail());
            ResultSet result = query.executeQuery();
            result.first();
            return result.getInt(1);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    /**
     * Inserisce dei nuovi numeri di telefono e li associa ad un contatto.
     * 
     * @param phoneNumbers numeri da inserire
     * @param contactId id del contatto al quale associare i numeri
     *
     * @return lista di tutti i numeri di telefono che non sono stati inseriti,
     *      se non ci sono stati problemi la lista è vuota
     */
    public ArrayList<PhoneNumber> insertPhoneNumbers(ArrayList<PhoneNumber> phoneNumbers, int contactId) {
        ArrayList<PhoneNumber> notInserted = new ArrayList<>();
        phoneNumbers.forEach((phoneNumber) -> {
            try {
                PreparedStatement query = this.connection
                        .prepareStatement("SELECT insert_phone_number(?, ?, ?, ?, ?, ?)");
                query.setString(1, phoneNumber.getCountryCode());
                query.setString(2, phoneNumber.getAreaCode());
                query.setString(3, phoneNumber.getPrefix());
                query.setString(4, phoneNumber.getPhoneLine());
                if (phoneNumber.getDescription().equals("")) {
                    query.setNull(5, Types.VARCHAR);
                } else {
                    query.setString(5, phoneNumber.getDescription());
                }
                query.setInt(6, contactId);
                ResultSet result = query.executeQuery();
                result.first();
                if (result.getInt(1) == -1) {
                    notInserted.add(phoneNumber);
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                notInserted.add(phoneNumber);
            }
        });
        return notInserted;
    }

    /**
     * Inserisce dei nuovi indirizzi email e li associa ad un contatto.
     * 
     * @param emails indirizzi da inserire
     * @param contactId id del contatto al quale associare gli indirizzi
     *
     * @return lista di tutti gli indirizzi email che non sono stati inseriti,
     *      se non ci sono stati problemi la lista è vuota
     */
    public ArrayList<Email> insertEmails(ArrayList<Email> emails, int contactId) {
        ArrayList<Email> notInserted = new ArrayList<>();
        emails.forEach((email) -> {
            try {
                PreparedStatement query = this.connection
                        .prepareStatement("SELECT insert_email(?, ?, ?)");
                query.setString(1, email.getEmail());
                if (email.getDescription().equals("")) {
                    query.setNull(2, Types.VARCHAR);
                } else {
                    query.setString(2, email.getDescription());
                }
                query.setInt(3, contactId);
                query.executeQuery();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                notInserted.add(email);
            }
        });
        return notInserted;
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
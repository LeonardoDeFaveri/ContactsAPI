package database_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import database_manager.column_labels.*;
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
     * Inserisce dei contatti in un gruppo.
     * 
     * @param contacts contatti da inserire
     * @param groupId id del gruppo nel quale inserire i contatti
     * 
     * @return lista di tutti i contatti che non sono stati inseriti,
     *      se non ci sono stati problemi la lista è vuota
     */
    public ArrayList<Contact> insertContactsInGroup(ArrayList<Contact> contacts, int groupId) {
        ArrayList<Contact> notInserted = new ArrayList<>();
        contacts.forEach((contact) -> {
            try {
                PreparedStatement query = this.connection
                    .prepareStatement("SELECT insert_contact_in_group(?, ?)");
                query.setInt(1, groupId);
                query.setInt(2, contact.getId());
                ResultSet result = query.executeQuery();
                result.first();
                if (result.getBoolean(1) == false) {
                    notInserted.add(contact);
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                notInserted.add(contact);
            }
        });
        return notInserted;
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
                ResultSet resul = query.executeQuery();
                resul.first();
                if (!resul.getBoolean(1)) {
                    notInserted.add(email);
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                notInserted.add(email);
            }
        });
        return notInserted;
    }

    /**
     * Inserisce una chiamata.
     * 
     * @param call chiamata da inserire
     * 
     * @return id della chiamata è stata inserita, altrimenti -1
     */
    public int insertCall(Call call) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT insert_call(?, ?, ?, ?)");
            query.setInt(1, call.getCaller().getId());
            query.setInt(2, call.getCalled().getId());
            query.setTimestamp(3, call.getTimestamp());
            query.setLong(4, call.getDuration());
            ResultSet result = query.executeQuery();
            result.first();
            return result.getInt(1);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

    /**
     * Estrae un utente in base al proprio indirizzo email.
     * 
     * @param email indirizzo email dell'utente
     * 
     * @return utente se è stato trovato, altrimenti null
     */
    public User getUser(String email) {
        User user = null;
        try {
            PreparedStatement query = this.connection.prepareStatement(
                "SELECT * FROM users WHERE email = ?"
            );
            query.setString(1, email);
            ResultSet result = query.executeQuery();
            if (result.first()) {
                user = new User(
                    email,
                    result.getString(UserLabels.PASSWORD)
                );
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return user;
    }

    /**
     * Estrae un contatto in base all'id specificato.
     * 
     * @param id id del contatto da estrarre
     * @param user utente che sta eseguendo la query
     * 
     * @return contatto se è stato trovato, altrimenti null
     */
    public Contact getContact(int id, User user) {
        Contact contatto = null;
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT * FROM contacts WHERE id = ? AND owner_user = ?");
            query.setInt(1, id);
            query.setString(2, user.getEmail());
            ResultSet result = query.executeQuery();
            if (result.first()) {
                String associatedUserEmail = result.getString(ContactLabels.ASSOCIATED_USER);
                contatto = new Contact(
                    id,
                    result.getString(ContactLabels.FIRST_NAME),
                    result.getString(ContactLabels.FAMILY_NAME),
                    result.getString(ContactLabels.SECOND_NAME),
                    user,
                    (associatedUserEmail == null) ? null : this.getUser(associatedUserEmail),
                    this.getEmails(id, user),
                    this.getPhoneNumbers(id, user)
                );
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return contatto;
    }

    /**
     * Estrae tutti i contatti associati ad un utente.
     * 
     * @param user utente per il quale estrarre i contatti
     * 
     * @return contatti se ne sono stati trovati, altrimenti 
     *      un array vuoto
     */
    public ArrayList<Contact> getContacts(User user) {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT * FROM contacts WHERE owner_user = ?");
            query.setString(1, user.getEmail());
            ResultSet result = query.executeQuery();
            if (result.first()) {
                do {
                    int id = result.getInt(ContactLabels.ID);
                    String associatedUserEmail = result.getString(ContactLabels.ASSOCIATED_USER);
                    contacts.add(new Contact(id,
                        result.getString(ContactLabels.FIRST_NAME),
                        result.getString(ContactLabels.FAMILY_NAME),
                        result.getString(ContactLabels.SECOND_NAME),
                        user,
                        (associatedUserEmail == null) ? null : this.getUser(associatedUserEmail),
                        this.getEmails(id, user),
                        this.getPhoneNumbers(id, user)
                    ));
                } while (result.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return contacts;
    }

    /**
     * Estrae tutti i numeri di telefono di un contatto.
     * 
     * @param id id del contatto per il quale estrarre i
     *      numeri di telefono
     * @param user utente che sta eseguendo la query
     * 
     * @return numeri di telefono associati al contatto se ne sono
     *      stati trovati, altrimenti un'array vuoto
     */
    public ArrayList<PhoneNumber> getPhoneNumbers(int id, User user) {
        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
        try {
            PreparedStatement query = this.connection
                .prepareStatement(
                    "SELECT * FROM phone_numbers P " +
                        "INNER JOIN contacts_numbers CN " +
                            "ON P.id = CN.phone_id " +
                        "INNER JOIN contacts C " + 
                            "ON CN.contact_id = C.id " +
                    "WHERE C.id = ? AND C.owner_user = ?"
                );
            query.setInt(1, id);
            query.setString(2, user.getEmail());
            ResultSet result = query.executeQuery();
            if(result.first()) {
                do {
                    phoneNumbers.add(new PhoneNumber(
                        result.getInt(PhoneNumbersLabels.ID),
                        result.getString(PhoneNumbersLabels.COUNTRY_CODE),
                        result.getString(PhoneNumbersLabels.AREA_CODE),
                        result.getString(PhoneNumbersLabels.PREFIX),
                        result.getString(PhoneNumbersLabels.PHONE_LINE),
                        result.getString(PhoneNumbersLabels.DESCRIPTION)
                    ));
                } while (result.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return phoneNumbers;
    }

    /**
     * Estrae tutti gli indirizzi email di un contatto.
     * 
     * @param id id del contatto per il quale estrarre gli
     *      indirizzi email
     * @param user utente che sta eseguendo la query
     * 
     * @return indirizzi email associati al contatto se ne sono
     *      stati trovati, altrimenti un'array vuoto
     */
    public ArrayList<Email> getEmails (int id, User user) {
        ArrayList<Email> emails = new ArrayList<>();
        try {
            PreparedStatement query = this.connection
                .prepareStatement(
                    "SELECT * FROM emails E " +
                    "INNER JOIN contacts_emails CE " +
                        "ON E.email = CE.email " +
                    "INNER JOIN contacts C " +
                        "ON CE.contact_id = C.id " +
                    "WHERE C.id = ? AND C.owner_user = ?"
                );
            query.setInt(1, id);
            query.setString(2, user.getEmail());
            ResultSet result = query.executeQuery();
            if (result.first()) {
                do {
                    emails.add(new Email(
                        result.getString(EmailLabels.EMAIL),
                        result.getString(EmailLabels.DESCRIPTION)
                    ));
                } while (result.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return emails;
    }

    /**
     * Estrae tutti i gruppi di un utente.
     * 
     * @param user utente per il quale estrarre i gruppi
     * 
     * @return gruppi se sono stati trovati, altrimenti un
     *      array vuoto
     */
    public ArrayList<Group> getGroups(User user) {
        ArrayList<Group> groups = new ArrayList<>();
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT * FROM groups WHERE owner_user = ?");
            query.setString(1, user.getEmail());
            ResultSet result = query.executeQuery();
            if (result.first()) {
                do {
                    int id = result.getInt(GroupLabels.ID);
                    groups.add(new Group(
                        id,
                        user,
                        result.getString(GroupLabels.NAME),
                        this.getContactsByGroup(id, user)
                    ));
                } while (result.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return groups;
    }

    /**
     * Estrae un gruppo in base all'id specificato.
     * 
     * @param id id del gruppo da estrarre
     * @param user utente che sta eseguendo la query
     * 
     * @return gruppo se è stato trovato, altrimenti null
     */
    public Group getGroup(int id, User user) {
        Group group = null;
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT * FROM groups WHERE id = ? AND owner_user = ?");
            query.setInt(1, id);
            query.setString(2, user.getEmail());
            ResultSet result = query.executeQuery();
            if (result.first()) {
                group = new Group(
                    id,
                    this.getUser(result.getString(GroupLabels.OWNER)),
                    result.getString(GroupLabels.NAME),
                    this.getContactsByGroup(id, user)
                );
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return group;
    }

    /**
     * Estrae tutti i contatti appartenenti ad un gruppo.
     * 
     * @param id id del gruppo per il quale estrarre i contatti
     * @param user utente che sta eseguendo la query
     * 
     * @return contatti se sono stati trovati, altrimenti un array vuoto
     */
    public ArrayList<Contact> getContactsByGroup(int id, User user) {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            PreparedStatement query = this.connection
                .prepareStatement(
                    "SELECT * FROM contacts C " + 
                        "INNER JOIN groups_contacts GC " +
                            "ON C.id = GC.contact_id " +
                        "INNER JOIN groups G " +
                            "ON GC.group_id = G.id " +
                        "WHERE G.id = ? AND G.owner_user = ?"
                );
            query.setInt(1, id);
            query.setString(2, user.getEmail());
            ResultSet result = query.executeQuery();
            if (result.first()) {
                do {
                    int contactId = result.getInt(ContactLabels.ID);
                    contacts.add(new Contact(
                        id,
                        result.getString(ContactLabels.FIRST_NAME),
                        result.getString(ContactLabels.FAMILY_NAME),
                        result.getString(ContactLabels.SECOND_NAME),
                        user,
                        this.getUser(ContactLabels.ASSOCIATED_USER),
                        this.getEmails(contactId, user),
                        this.getPhoneNumbers(contactId, user)
                    ));
                } while (result.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return contacts;
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
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
            return result.getBoolean(1);
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
            if (contact.getSecondName() == null) {
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

//--------------------------------------------------------------------------------------------

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
     * Estrae il numero di telefono specificato.
     * 
     * @param id id del numero di telefono da estrarre
     * 
     * @return numero di telefono estratto
     */
    public PhoneNumber getPhoneNumber(int id) {
        PhoneNumber phoneNumber = null;
        try {
            PreparedStatement query = this.connection
                .prepareStatement(
                    "SELECT * FROM phone_numbers WHERE id = ?"
                );
            query.setInt(1, id);
            ResultSet result = query.executeQuery();
            if(result.first()) {
                phoneNumber = new PhoneNumber(
                    result.getInt(PhoneNumbersLabels.ID),
                    result.getString(PhoneNumbersLabels.COUNTRY_CODE),
                    result.getString(PhoneNumbersLabels.AREA_CODE),
                    result.getString(PhoneNumbersLabels.PREFIX),
                    result.getString(PhoneNumbersLabels.PHONE_LINE),
                    result.getString(PhoneNumbersLabels.DESCRIPTION)
                );
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return phoneNumber;
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
                    "SELECT P.* FROM phone_numbers P " +
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
     * 
     * @return indirizzi email associati al contatto se ne sono
     *      stati trovati, altrimenti un'array vuoto
     */
    public ArrayList<Email> getEmails (int id, User user) {
        ArrayList<Email> emails = new ArrayList<>();
        try {
            PreparedStatement query = this.connection
                .prepareStatement(
                    "SELECT E.* FROM emails E " +
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
     * Estrae un indirizzo email.
     * 
     * @param emailAddress indirizzo email da estrarre
     * 
     * @return indirizz email se è stato trovato, altrimenti null
     */
    public Email getEmail (String emailAddress) {
        Email email = null;
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT * FROM emails WHERE email = ?");
            query.setString(1, emailAddress);
            ResultSet result = query.executeQuery();
            if (result.first()) {
                email = new Email(
                    result.getString(EmailLabels.EMAIL),
                    result.getString(EmailLabels.DESCRIPTION)
                );
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return email;
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
                    "SELECT C.* FROM contacts C " + 
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
     * Estrae una chiamata fatta o ricevuta da un utente.
     * 
     * @param id id della chiamata da estrarre
     * @param user utente che sta eseguendo la query
     * 
     * @return chiamata se è stata trovata, altrimenti null
     */
    public Call getCall(int id, User user) {
        Call call = null;
        try {
            PreparedStatement query = this.connection
                .prepareStatement(
                    "SELECT CA.* FROM calls CA " +
                        "INNER JOIN contacts CO " +
                            "ON CA.caller_contact_id = CO.id OR CA.called_contact_id = CO.id " +
                    "WHERE CA.id = ? AND CO.associated_user = ?"
                );
            query.setInt(1, id);
            query.setString(2, user.getEmail());
            ResultSet result = query.executeQuery();
            if (result.first()) {
                call = new Call(
                    result.getInt(CallLabels.ID),
                    this.getPhoneNumber(result.getInt(CallLabels.CALLER_NUMBER)),
                    this.getContact(result.getInt(CallLabels.CALLER_CONTACT), user),
                    this.getPhoneNumber(result.getInt(CallLabels.CALLED_NUMBER)),
                    this.getContact(result.getInt(CallLabels.CALLED_CONTACT), user),
                    result.getTimestamp(CallLabels.TIMESTAMP),
                    result.getLong(CallLabels.DURATION)
                );
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return call;
    }

    /**
     * Estrae tutte le chiamate fatte o ricevute da un utente.
     * 
     * @param user utente per il quale estrarre le chiamate
     * 
     * @return chiamate se sono state trovate, altrimenti un array vuoto
     */
    public ArrayList<Call> getCalls(User user) {
        ArrayList<Call> calls = new ArrayList<>();
        try {
            PreparedStatement query = this.connection
                .prepareStatement(
                    "SELECT CA.* FROM calls CA " +
                        "INNER JOIN contacts CO " +
                            "ON CA.caller_contact_id = CO.id OR CA.called_contact_id = CO.id " +
                    "WHERE CO.associated_user = ?"
                );
            query.setString(1, user.getEmail());
            ResultSet result = query.executeQuery();
            if (result.first()) {
                do {
                    calls.add(new Call(
                        result.getInt(CallLabels.ID),
                        this.getPhoneNumber(result.getInt(CallLabels.CALLER_NUMBER)),
                        this.getContact(result.getInt(CallLabels.CALLER_CONTACT), user),
                        this.getPhoneNumber(result.getInt(CallLabels.CALLED_NUMBER)),
                        this.getContact(result.getInt(CallLabels.CALLED_CONTACT), user),
                        result.getTimestamp(CallLabels.TIMESTAMP),
                        result.getLong(CallLabels.DURATION)
                    ));
                } while (result.next());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return calls;
    }

//--------------------------------------------------------------------------------------------

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
            if (contact.getSecondName() == null) {
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
                if (phoneNumber.getDescription() == null) {
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
                if (email.getDescription() == null) {
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
                .prepareStatement("SELECT insert_call(?, ?, ?, ?, ?, ?)");
            query.setInt(1, call.getCallerNumber().getId());
            query.setInt(2, call.getCallerContact().getId());
            query.setInt(3, call.getCalledNumber().getId());
            Contact called = call.getCalledContact();
            if (called == null) {
                query.setNull(4, Types.INTEGER);
            } else {
                query.setInt(4, called.getId());
            }
            query.setTimestamp(5, call.getTimestamp());
            query.setLong(6, call.getDuration());
            ResultSet result = query.executeQuery();
            result.first();
            return result.getInt(1);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return -1;
        }
    }

//--------------------------------------------------------------------------------------------

    /**
     * Modifica email e password di un utente.
     * 
     * @param oldUser credenziali prima della modifica
     * @param newUser credenziali dopo la modifica
     * 
     * @return true se le credenziali sono state modificate,
     *      altrimenti false
     */
    public boolean updateUser(User oldUser, User newUser) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("UPDATE users SET email = ?, password = ? WHERE email = ?");
            query.setString(1, newUser.getEmail());
            query.setString(2, newUser.getPassword());
            query.setString(3, oldUser.getEmail());
            return query.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Modifica le proprietà:
     *  * firtst_name
     *  * family_name
     *  * second_name
     * del contatto.
     * 
     * @param contactId id del contatto da modificare
     * @param newContact contatto dopo la modifica
     * 
     * @return true se la modifica è andata a buon fine, altrimenti false
     */
    public boolean updateContact(int contactId, Contact newContact) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("UPDATE contacts SET first_name = ?, family_name = ?, second_name = ? WHERE id = ?");
            query.setString(1, newContact.getFirstName());
            query.setString(2, newContact.getFamilyName());
            String secondName = newContact.getSecondName();
            if (secondName == null) {
                query.setNull(3, Types.VARCHAR);
            } else {
                query.setString(3, secondName);
            }
            query.setInt(4, contactId);
            return query.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Aggiorna un numero telefono associato ad un contatto.
     * 
     * @param contactId id del contatto per il quale modificare il
     *      numero di telefono
     * @param phomeNumberId id del vecchio numero di telefono associato
     * @param newPhoneNumber nuovo numero di telefono da associare
     * 
     * @return true se la modifica è andata a buon fine, altrimenti false
     */
    public boolean updateContactPhoneNumber(int contactId, int phoneNumberId, PhoneNumber newPhoneNumber) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT update_contact_phone_number(?, ?, ?, ?, ?, ?, ?)");
            query.setInt(1, contactId);
            query.setInt(2, phoneNumberId);
            query.setString(3, newPhoneNumber.getCountryCode());
            query.setString(4, newPhoneNumber.getAreaCode());
            query.setString(5, newPhoneNumber.getPrefix());
            query.setString(6, newPhoneNumber.getPhoneLine());
            String description = newPhoneNumber.getDescription();
            if (description == null) {
                query.setNull(7, Types.VARCHAR);
            } else {
                query.setString(7, description);
            }
            ResultSet result = query.executeQuery();
            result.first();
            return result.getBoolean(1);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Aggiorna un indirizzo email associato ad un contatto.
     * 
     * @param contactId id del contatto per il quale modificare il
     *      numero di telefono
     * @param oldEmail vecchio indirizzo email associato
     * @param newEmail nuovo indirizzo email da associare
     * 
     * @return true se la modifica è andata a buon fine, altrimenti false
     */
    public boolean updateContactEmail(int contactId, String oldEmail, Email newEmail) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("SELECT update_contact_email(?, ?, ?, ?)");
            query.setInt(1, contactId);
            query.setString(2, oldEmail);
            query.setString(3, newEmail.getEmail());
            String description = newEmail.getDescription();
            if (description == null) {
                query.setNull(4, Types.VARCHAR);
            } else {
                query.setString(4, description);
            }
            ResultSet result = query.executeQuery();
            result.first();
            return result.getBoolean(1);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Modifica il nome di un gruppo.
     * 
     * @param groupId id del gruppo al quale modificare il nome
     * @param name nuovo nome del gruppo
     * 
     * @return true se la modifica è andata a buon fine, altrimenti false
     */
    public boolean updateGroupName(int groupId, String name) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("UPDATE groups SET name = ? WHERE id = ?");
            query.setString(1, name);
            query.setInt(2, groupId);
            return query.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

//--------------------------------------------------------------------------------------------

    /**
     * Elimina un utente e tutte le risorsa ad esso associate.
     * 
     * @param userEmail indirizzo email dell'utente da eliminare
     * 
     * @return true se l'utente è stato eliminato, altrimenti false
     */
    public boolean deleteUser(String userEmail) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("DELETE FROM users WHERE email = ?");
            query.setString(1, userEmail);
            return query.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Elimina un contatto e tutte le risorse ad esso associate.
     * 
     * @param contactId id del contatto da eliminare
     * 
     * @return true se il contatto è stato eliminato, altrimenti false
     */
    public boolean deleteContact(int contactId) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("DELETE FROM contacts WHERE id = ?");
            query.setInt(1, contactId);
            return query.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Elimina un numero di telefono associato ad un contatto.
     * 
     * @param contactId id del contatto per il quale eliminare il numero di telefono
     * @param numberId id del numero di telefono da eliminare
     * 
     * @return true se il numero di telefono è stato eliminato, altrimenti false
     */
    public boolean deletePhoneNumber(int contactId, int numberId) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("DELETE FROM contacts_numbers WHERE contact_id = ? AND phone_id = ?");
            query.setInt(1, contactId);
            query.setInt(2, numberId);
            return query.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Elimina tutti i numeri di telefono associati ad un contatto.
     * 
     * @param contactId id del contatto per il quale eliminare i numeri di telefono
     * 
     * @return true se i numeri di telefono sono stati eliminati, altrimenti false
     */
    public boolean deletePhoneNumbers(int contactId) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("DELETE FROM contacts_numbers WHERE contact_id = ?");
            query.setInt(1, contactId);
            return query.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Elimina un indirizzo email associato ad un contatto.
     * 
     * @param contactId id del contatto per il quale eliminare l'indirizzo
     *      email
     * @param email indirizzo email da eliminare
     * 
     * @return true se l'indirizzo email è stato eliminato, altrimenti false
     */
    public boolean deleteEmail(int contactId, String email) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("DELETE FROM contacts_email WHERE contact_id = ? AND email = ?");
            query.setInt(1, contactId);
            query.setString(2, email);
            return query.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

    /**
     * Elimina tutti gli indirizzo email associati ad un contatto.
     * 
     * @param contactId id del contatto per il quali eliminare gli
     *      indirizzo email
     * 
     * @return true se gli indirizzo email sono stati eliminati, altrimenti false
     */
    public boolean deleteEmails(int contactId) {
        try {
            PreparedStatement query = this.connection
                .prepareStatement("DELETE FROM contacts_email WHERE contact_id = ?");
            query.setInt(1, contactId);
            return query.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }

//--------------------------------------------------------------------------------------------
    
    /**
     * Chiude la connessione con il database.
     * 
     * @throws SQLException errore durante la chiusura della connessione
     */
    public void close() throws SQLException {
        this.connection.close();
    }
}
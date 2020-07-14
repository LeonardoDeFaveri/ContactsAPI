package models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.keys.ContactKeys;

/**
 * La classe rappresenta un contatto della rubrica.
 */
public class Contact {
    private int id;
    private String firstName;
    private String familyName;
    private String secondName;
    private User owner;
    private User associatedUser;
    private ArrayList<Email> emails;
    private ArrayList<PhoneNumber> phoneNumbers;

    public Contact(int id, String firstName, String familyName, String secondName, User owner, User associatedUser,
        ArrayList<Email> emails, ArrayList<PhoneNumber> phoneNumbers) {
        this.id = id;
        this.firstName = firstName;
        this.familyName = familyName;
        this.secondName = secondName;
        this.owner = owner;
        this.associatedUser = associatedUser;
        this.emails = emails;
        this.phoneNumbers = phoneNumbers;
    }

    public Contact(int id, String firstName, String familyName, String secondName, User owner, User associatedUser) {
        this(id, firstName, familyName, secondName, owner, 
            associatedUser, new ArrayList<Email>(), new ArrayList<PhoneNumber>());
    }

    /**
     * Crea un'istanza a partire da un'oggetto JSON.
     * 
     * @param JSONContact rappresentazione JSON del contatto
     * 
     * @throws JSONException errore durante la lettura di alcuni campi, che
     *      probabilmente non sono stati forniti
     */
    public Contact(JSONObject JSONContact) throws JSONException {
        this.id = JSONContact.optInt(ContactKeys.ID, -1);
        this.firstName = JSONContact.getString(ContactKeys.FIRST_NAME);
        this.familyName = JSONContact.getString(ContactKeys.FAMILY_NAME);
        this.secondName = JSONContact.optString(ContactKeys.SECOND_NAME);
        if (this.secondName.equals("")) {
            this.secondName = null;
        }
        this.owner = new User(JSONContact.getJSONObject(ContactKeys.OWNER));
        JSONObject user = JSONContact.optJSONObject(ContactKeys.ASSOCIATED_USER);
        if (user == null) {
            this.associatedUser = null;
        } else {
            this.associatedUser = new User(user);
        }
        
        JSONArray JSONPhoneNumbers = JSONContact.optJSONArray(ContactKeys.PHONE_NUMBERS);
        if (JSONPhoneNumbers == null) {
            this.phoneNumbers = new ArrayList<>();
        } else {
            this.phoneNumbers = new ArrayList<>(JSONPhoneNumbers.length());
            JSONPhoneNumbers.forEach((object) -> {
                if (object instanceof JSONObject) {
                    JSONObject JSONPhoneNumber = (JSONObject) object;
                    this.phoneNumbers.add(new PhoneNumber(JSONPhoneNumber));
                }
            });
        }
        
        JSONArray JSONEmails = JSONContact.optJSONArray(ContactKeys.EMAILS);
        if (JSONEmails == null) {
            this.emails = new ArrayList<>();
        } else {
            this.emails = new ArrayList<>(JSONEmails.length());
            JSONEmails.forEach((object) -> {
                if (object instanceof JSONObject) {
                    JSONObject JSONEmail = (JSONObject) object;
                    this.emails.add(new Email(JSONEmail));
                }
            });
        }
    }

    /**
     * Restituisce l'id del contatto.
     * 
     * @return id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Restituisce il primo nome del contatto.
     * 
     * @return primo nome
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Restituisce il cognome del contatto.
     * 
     * @return cognome
     */
    public String getFamilyName() {
        return this.familyName;
    }

    /**
     * Restituisce il secondo nome del contatto.
     * 
     * @return secondo nome
     */
    public String getSecondName() {
        return this.secondName;
    }

    /**
     * Restituisce l'utente che possiede questo contatto nella propria rubrica.
     * 
     * @return utente proprietario
     */
    public User getOwner() {
        return this.owner;
    }

    /**
     * Restituisce l'utente rappresentato da questo contatto. Tutti gli utenti sono
     * infatti rappresentanti da un contatto.
     * 
     * @return utente associato
     */
    public User getAssociatedUser() {
        return this.associatedUser;
    }

    /**
     * Restituisce tutti gli indirizzi email associati al contatto.
     * 
     * @return indirizzi email
     */
    public ArrayList<Email> getEmails() {
        return this.emails;
    }

    /**
     * Restituisce tutti i numeri di telefono associati al contatto.
     * 
     * @return numeri di telefono
     */
    public ArrayList<PhoneNumber> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    /**
     * Restituisce una rappresentazione, sotto forma di oggetto JSON,
     * dell'istanza.
     * 
     * @return rappresentazione JSON dell'istanza 
     */
    public JSONObject toJSON() {
        return new JSONObject(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || (obj instanceof Contact) == false) {
            return false;
        } else {
            Contact contact = (Contact) obj;
            return this.id == contact.id && this.firstName.equals(contact.firstName) &&
                this.familyName.equals(contact.familyName) &&
                (
                    (this.secondName == null && contact.secondName == null) ||
                    (this.secondName.equals(contact.secondName))
                ) && this.owner.equals(contact.owner) &&
                (
                    (this.associatedUser == null && contact.associatedUser == null) ||
                    (this.associatedUser.equals(contact.associatedUser))
                ) &&
                this.phoneNumbers.equals(contact.phoneNumbers) &&
                this.emails.equals(contact.emails);
        }
    }
}
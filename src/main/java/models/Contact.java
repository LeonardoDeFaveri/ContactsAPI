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
        return id;
    }

    /**
     * Restituisce il primo nome del contatto.
     * 
     * @return primo nome
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Restituisce il cognome del contatto.
     * 
     * @return cognome
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Restituisce il secondo nome del contatto.
     * 
     * @return secondo nome
     */
    public String getSecondName() {
        return secondName;
    }

    /**
     * Restituisce l'utente che possiede questo contatto nella propria rubrica.
     * 
     * @return utente proprietario
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Restituisce l'utente rappresentato da questo contatto. Tutti gli utenti sono
     * infatti rappresentanti da un contatto.
     * 
     * @return utente associato
     */
    public User getAssociatedUser() {
        return associatedUser;
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
     * Controlla che due istanze di Contact rappresentino contatti diversi.
     * 
     * @param c1 istanza di Contact da confrontare
     * 
     * @return true se rappresentano lo stesso contatto, altrimenti false
     */
    public boolean equals(Contact c1) {
        return c1.id == this.id && c1.firstName.equals(this.firstName) && 
            c1.familyName.equals(this.familyName) && c1.secondName.equals(this.secondName) &&
            c1.owner.equals(this.owner) && c1.associatedUser.equals(this.associatedUser) &&
            c1.emails.equals(this.emails) && c1.phoneNumbers.equals(this.phoneNumbers);
    }

    /**
     * Restituisce una rappresentazione, sotto forma di oggetto JSON,
     * dell'istanza.
     * 
     * @return rappresentazione JSON dell'istanza 
     */
    public JSONObject toJSON() {
        JSONObject contact = new JSONObject();
        
        JSONArray phoneNumbers = new JSONArray(this.phoneNumbers.size());
        this.phoneNumbers.forEach((object) -> {
            if (object instanceof PhoneNumber) {
                PhoneNumber phoneNumber = (PhoneNumber) object;
                phoneNumbers.put(phoneNumber.toJSON());
            }
        });
        JSONArray emails = new JSONArray(this.emails.size());
        this.emails.forEach((object) -> {
            if (object instanceof Email) {
                Email email = (Email) object;
                emails.put(email.toJSON());
            }
        });

        contact.put(ContactKeys.ID, this.id);
        contact.put(ContactKeys.FIRST_NAME, this.firstName);
        contact.put(ContactKeys.FAMILY_NAME, this.familyName);
        contact.put(ContactKeys.SECOND_NAME, this.secondName);
        contact.put(ContactKeys.OWNER, this.owner);
        contact.put(ContactKeys.ASSOCIATED_USER, this.associatedUser);
        contact.put(ContactKeys.PHONE_NUMBERS, phoneNumbers);
        contact.put(ContactKeys.EMAILS, emails);
        return contact;
    }
}
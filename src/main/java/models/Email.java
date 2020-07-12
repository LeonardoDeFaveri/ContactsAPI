package models;

import org.json.JSONException;
import org.json.JSONObject;

import utils.keys.EmailKeys;

/**
 * La classe rappresenta uno dei tanti indirizzi email che possono 
 * essere assegnati ad un contatto. Oltre all'indirizzo può anche 
 * essere specificata una descrizione.
 */
public class Email {
    private String email;
    private String description;

    public Email(String email, String description) {
        this.email = email;
        this.description = description;
    }

    /**
     * Crea un'istanza a partire da un'oggetto JSON.
     * 
     * @param JSONEmail rappresentazione JSON dell'indirizzo email.
     * 
     * @throws JSONException errore durante la lettura di alcuni campi, che
     *      probabilmente non sono stati forniti
     */
    public Email(JSONObject JSONEmail) throws JSONException {
        this.email = JSONEmail.getString(EmailKeys.EMAIL);
        this.description = JSONEmail.optString(EmailKeys.DESCRIPTION);
    }

    /**
     * Restituisce l'indirizzo email.
     * 
     * @return indirizzo email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Restituisce la descrizione dell'indirizzo email.
     * 
     * @return descrizione
     */
    public String getDescription() {
        return this.description;
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
        if (obj == null || (obj instanceof Email) == false) {
            return false;
        } else {
            Email email = (Email) obj;
            return this.email.equals(email.email) && 
                (
                    (this.description == null && email.description == null) ||
                    (this.description.equals(email.description))
                );
        }
    }
}
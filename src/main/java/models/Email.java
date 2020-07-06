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
        return email;
    }

    /**
     * Restituisce la descrizione dell'indirizzo email.
     * 
     * @return descrizione
     */
    public String getDescription() {
        return description;
    }

    /**
     * Controlla che due istanze di Email rappresentino email diverse.
     * 
     * @param e1 istanza di Email da confrontare
     * 
     * @return true se rappresentano la stessa email, altrimenti false
     */
    public boolean equals(Email e1) {
        return e1.email.equals(this.email) && e1.description.equals(this.description);
    }

    /**
     * Restituisce una rappresentazione, sotto forma di oggetto JSON,
     * dell'istanza.
     * 
     * @return rappresentazione JSON dell'istanza 
     */
    public JSONObject toJSON() {
        JSONObject email = new JSONObject();
        email.put(EmailKeys.EMAIL, this.email);
        email.put(EmailKeys.DESCRIPTION, this.description);
        return email;
    }
}
package models;

import org.json.JSONException;
import org.json.JSONObject;

import utils.keys.UserKeys;

/**
 * La classe rappresenta un'utente del servizio web.
 */
public class User {
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Crea un'istanza a partire da un'oggetto JSON.
     * 
     * @param JSONUser rappresentazione JSON dell'utente
     * 
     * @throws JSONException errore durante la lettura di alcuni campi, che
     *      probabilmente non sono stati forniti
     */
    public User(JSONObject JSONUser) throws JSONException {
        this.email = JSONUser.getString(UserKeys.EMAIL);
        this.password = JSONUser.optString(UserKeys.PASSWORD);
    }

    /**
     * Restituisce l'email dell'utente.
     * 
     * @return email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Restituisce la password dell'utente.
     * 
     * @return password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Controlla che due istanze di User rappresentino utenti diversi.
     * 
     * @param u1 istanza di User da confrontare
     * 
     * @return true se rappresentano lo stesso utente, altrimenti false
     */
    public boolean equals(User u1) {
        return u1.email.equals(this.email) && u1.password.equals(this.password);        
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
}
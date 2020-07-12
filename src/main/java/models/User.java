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
        if (obj == null || (obj instanceof User) == false) {
            return false;
        } else {
            User user = (User) obj;
            return this.email.equals(user.email) && this.password.equals(user.password);
        }
    }
}
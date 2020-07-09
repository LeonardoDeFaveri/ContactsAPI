package utils;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import models.*;

/**
 * La classe rappresenta uno strumento utile per effettuare il parsing
 * dei messaggi JSON ricevuti dalle applicazioni che utilizzano il servizio.
 */
public class JSONparser {
    JSONObject object;

    /**
     * Crea un JSONObject a partire da un oggetto di tipo BufferedReader. 
     * L'oggetto è creato a partire dall'intero contenuto del BufferedReader.
     * 
     * @param reader buffered reader dal quale estrarre il test da convertire in JSON
     * @throws IOException errore durante la lettura del testo dal buffered reader
     */
    public JSONparser(BufferedReader reader) throws IOException, JSONException {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        String jsonString = stringBuilder.toString();
        this.object = new JSONObject(jsonString);
    }

    /**
     * Crea un JSONObject a partire da una stringa.
     *
     * @param jsonString stringa da convertire
     */
    public JSONparser(String jsonString) {
        this.object = new JSONObject(jsonString);
    }

    /**
     * Controlla se la richiesta è solo per un login.
     * 
     * @return true se è una richiesta di solo login, altrimenti false
     */
    public boolean isJustLogin() {
        return this.object.optBoolean("justLogin");
    }

    /**
     * Estrae le credenziali necessarie alla creazione di un nuovo utente e
     * del relativo contatto.
     * 
     * @return nuovo contatto se è stato trovato, altrimenti null
     */
    public Contact getRegistrationCredentials() {
        try {
            return new Contact(this.object.getJSONObject("contact"));
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Restituisce il contatto specificato.
     * 
     * @return contatto se è stato trovato, altrimenti null
     */
    public Contact getContact() {
        try {
            return new Contact(this.object.getJSONObject("contact"));
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Restituisce il gruppo specificato.
     * 
     * @return gruppo se è stato trovato, altrimenti null
     */
    public Group getGroup() {
        try {
            return new Group(this.object.getJSONObject("group"));
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Restituisce la chiamata specificato.
     * 
     * @return chiamata se è stata trovata, altrimenti null
     */
    public Call getCall() {
        try {
            return new Call(this.object.getJSONObject("call"));
        } catch (JSONException ex) {
            return null;
        }
    }
}
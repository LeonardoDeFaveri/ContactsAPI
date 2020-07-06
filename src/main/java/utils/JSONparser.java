package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.Contact;
import models.Email;
import models.PhoneNumber;
import models.User;
import utils.keys.UserKeys;

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
    public JSONparser(BufferedReader reader) throws IOException {
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
     * Estrae il valore del campo action.
     * 
     * @return il valore in formato stringa se è stato
     * trovato, altrimenti null
     */
    public String getAction() {
        try{
            return this.object.getString("action");
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Estrae le credenziali da usare per autenticare un utente.
     * Per la password si suppone ne venga inviato l'hash calcolato
     * con una funzione di hash a 256 bit.
     * 
     * @return credenziali dell'utente se sono state trovate, altrimenti
     * null
     */
    public User getLoginCredentials() {
        try {
            return new User(this.object.getJSONObject("user"));
        } catch (JSONException ex) {
            return null;
        }
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
     * Controlla che la strigna possa essere un valore valido
     * per il campo "action" del documento json. Non viene fatta
     * distinzione tra le maiuscole e li minuscole, però sarebbe
     * preferibile, per convenzione, usare stringhe minuscole.
     * 
     * @param action stringa da controllare
     * 
     * @return true se la stringa è corretta, altrimenti false
     */
    public static boolean checkActionValidity(String action) {
        return action.equalsIgnoreCase(Actions.LOGIN) ||
            action.equalsIgnoreCase(Actions.REGISTER) ||
            action.equalsIgnoreCase(Actions.CREATE);
    }
}
package models;

import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import utils.keys.CallKeys;

/**
 * La classe rappresenta una telefonata tra due contatti.
 */
public class Call {
    private int id;
    private Contact caller;
    private Contact called;
    private Timestamp timestamp;
    private long duration;

    public Call(int id, Contact caller, Contact called, Timestamp timestamp, long duration) {
        this.caller = caller;
        this.called = called;
        this.timestamp = timestamp;
        this.duration = duration;
    }

    public Call(Contact caller, Contact called, Timestamp timestamp) {
        this(-1, caller, called, timestamp, 0);
    }

    /**
     * Crea un'istanza a partire da un'oggetto JSON.
     * 
     * @param JSONContact rappresentazione JSON del contatto
     * 
     * @throws JSONException errore durante la lettura di alcuni campi, che
     *      probabilmente non sono stati forniti
     */
    public Call(JSONObject JSONCall) throws JSONException{
        this.id = JSONCall.optInt(CallKeys.ID, -1);
        this.caller = new Contact(JSONCall.getJSONObject(CallKeys.CALLER));
        this.called = new Contact(JSONCall.getJSONObject(CallKeys.CALLED));
        this.timestamp = new Timestamp(JSONCall.getLong(CallKeys.TIMESTAMP));
        this.duration = JSONCall.optLong(CallKeys.DURATION);
    }

    /**
     * Restituisce l'id della chiamata.
     * 
     * @return id della chiamata
     */
    public int getId() {
        return this.id;
    }

    /**
     * Restituisce il contatto che ha effettuato la
     * chiamata.
     * 
     * @return contatto chiamante
     */
    public Contact getCaller() {
        return caller;
    }

    /**
     * Restituisce il contatto che è stato chiamato.
     * 
     * @return contatto chiamato
     */
    public Contact getCalled() {
        return called;
    }

    /**
     * Restituisce data e ora della chiamata.
     * 
     * @return data e ora della chiamata
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Restituisce la durata in secondi della
     * chiamata. Se il contatto chiamato non ha
     * risposto, la durata è 0.
     * 
     * @return durata in secondi della chiamata
     */
    public long getDuration() {
        return duration;
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
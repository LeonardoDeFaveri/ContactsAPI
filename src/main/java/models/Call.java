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
    private PhoneNumber callerNumber;
    private Contact callerContact;
    private PhoneNumber calledNumber;
    private Contact calledContact;
    private Timestamp timestamp;
    private long duration;

    public Call(int id, PhoneNumber callerNumber, Contact callerContact, PhoneNumber calledNumber,
            Contact calledContact, Timestamp timestamp, long duration) {
        this.id = id;
        this.callerNumber = callerNumber;
        this.callerContact = callerContact;
        this.calledNumber = calledNumber;
        this.calledContact = calledContact;
        this.timestamp = timestamp;
        this.duration = duration;
    }

    public Call(PhoneNumber callerNumber, Contact callerContact, PhoneNumber calledNumber, Timestamp timestamp) {
        this.callerNumber = callerNumber;
        this.callerContact = callerContact;
        this.calledNumber = calledNumber;
        this.timestamp = timestamp;
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
        this.callerNumber = new PhoneNumber(JSONCall.getJSONObject(CallKeys.CALLER_NUMBER));
        this.callerContact = new Contact(JSONCall.getJSONObject(CallKeys.CALLER_CONTACT));
        this.calledNumber = new PhoneNumber(JSONCall.getJSONObject(CallKeys.CALLED_NUMBER));
        this.calledContact = new Contact(JSONCall.getJSONObject(CallKeys.CALLED_CONTACT));
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
     * Restituisce il numero di telefono che 
     * ha effettuato la chiamata.
     * 
     * @return numero chiamante
     */
    public PhoneNumber getCallerNumber() {
        return this.callerNumber;
    }

    /**
     * Restituisce il contatto che ha effettuato la
     * chiamata.
     * 
     * @return contatto chiamante
     */
    public Contact getCallerContact() {
        return this.callerContact;
    }

    /**
     * Restituisce il numero di telefono che 
     * ha effettuato la chiamata.
     * 
     * @return numero chiamante
     */
    public PhoneNumber getCalledNumber() {
        return this.calledNumber;
    }

    /**
     * Restituisce il contatto che è stato chiamato.
     * 
     * @return contatto chiamato
     */
    public Contact getCalledContact() {
        return this.calledContact;
    }

    /**
     * Restituisce data e ora della chiamata.
     * 
     * @return data e ora della chiamata
     */
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    /**
     * Restituisce la durata in secondi della
     * chiamata. Se il contatto chiamato non ha
     * risposto, la durata è 0.
     * 
     * @return durata in secondi della chiamata
     */
    public long getDuration() {
        return this.duration;
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
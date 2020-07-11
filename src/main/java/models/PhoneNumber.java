package models;

import java.util.regex.*;

import org.json.JSONException;
import org.json.JSONObject;

import utils.keys.PhoneNumberKeys;

/**
 * La classe rappresenta un numero di telefono suddiviso nelle sue singole
 * parti. Inoltre è possibile fornire una descrizione per il numero.
 * 
 * Parti di un numero di telefono: 
 * 1) +ww : country code
 * 2) xxx : area code
 * 3) yyy : prefix 
 * 4) zzzz : phone line
 * 
 * Numero di telefono completo: +ww xxx yyyzzzz
 */
public class PhoneNumber {
    private int id;
    private String countryCode;
    private String areaCode;
    private String prefix;
    private String phoneLine;
    private String description;

    public PhoneNumber(int id, String countryCode, String areaCode, String prefix, String phoneLine,
            String description) {
        this.id = id;
        this.countryCode = countryCode;
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.phoneLine = phoneLine;
        this.description = description;
    }

    /**
     * Crea un'istanza della classe a partire da una stringa contenente un numero di telefono.
     * La stringa deve avere iniziare con il country code e questo deve essere separato con almeno
     * uno spazio dal resto del numero. Il country code deve essere specificato col il più ('+').
     * 
     * @param phoneNumber numero da convertire
     */
    public PhoneNumber(int id, String phoneNumber) {
        this.id = id;
        
        Pattern countryCodeFinder = Pattern.compile("(\\+\\d{1,3})");
        Matcher matcher = countryCodeFinder.matcher(phoneNumber);
        matcher.find();
        this.countryCode = matcher.group().substring(1);
        
        Pattern areaAndPrefixFinder = Pattern.compile("(\\d{3})");
        matcher = areaAndPrefixFinder.matcher(phoneNumber);
        matcher.find();
        this.areaCode = matcher.group();
        matcher.find(matcher.end());
        this.prefix = matcher.group();
        
        Pattern phoneLineFinder = Pattern.compile("(\\d{4}$)");
        matcher = phoneLineFinder.matcher(phoneNumber);
        matcher.find();
        this.phoneLine = matcher.group();
    }

    /**
     * Crea un'istanza a partire da un'oggetto JSON.
     * 
     * @param JSONPhoneNumber rappresentazione JSON del numero di telefono
     * 
     * @throws JSONException errore durante la lettura di alcuni campi, che
     *      probabilmente non sono stati forniti
     */
    public PhoneNumber(JSONObject JSONPhoneNumber) throws JSONException {
        this.id = JSONPhoneNumber.optInt(PhoneNumberKeys.ID, -1);
        this.countryCode = JSONPhoneNumber.getString(PhoneNumberKeys.COUNTRY_CODE);
        this.areaCode = JSONPhoneNumber.getString(PhoneNumberKeys.AREA_CODE);
        this.prefix = JSONPhoneNumber.getString(PhoneNumberKeys.PREFIX);
        this.phoneLine = JSONPhoneNumber.getString(PhoneNumberKeys.PHONE_LINE);
        this.description = JSONPhoneNumber.optString(PhoneNumberKeys.DESCRIPTION);
    }

    /**
     * Restituisce l'id del numero di telefono.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il codice identificativo della nazione
     * del numero (+xx). Non viene incluso il '+'.
     * 
     * @return codice nazione
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Restituisce il codice dell'area di appartenenza del numero.
     * Il primo blocco di 3 numeri.
     * 
     * @return codice di area
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Restituisce il prefisso del numero. Il secondo blocco di 3 numeri.
     * 
     * @return prefisso
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Restituisce il numero specifico di una scheda. Gli ultimi numeri dopo il
     * prefisso.
     * 
     * @return phone line
     */
    public String getPhoneLine() {
        return phoneLine;
    }

    /**
     * Restituisce la descrizione del numero.
     * 
     * @return descrizione
     */
    public String getDescription() {
        return description;
    }

    /**
     * Controlla che due istanze di PhoneNumber rappresentino
     * numeri diversi.
     * 
     * @param p1 istanza di PhoneNumber da controllare
     * 
     * @return true se rappresentano numeri uguali, altrimenti false
     */
    public boolean equals(PhoneNumber p1) {
        return p1.toString().equals(this.toString());
    }

    /**
     * Restituisce una rappresentazione del numero di telefono
     * come stringa. Il formato è il seguente: +www xxxyyyzzzz
     */
    @Override
    public String toString() {
        return String.format("+%s %s%s%s", this.countryCode, this.areaCode, this.prefix, this.phoneLine);
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
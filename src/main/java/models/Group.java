package models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.keys.GroupKeys;

/**
 * La classe rappresenta un gruppo di contatti.
 */
public class Group {
    private int id;
    private User owner;
    private String name;
    private ArrayList<Contact> contacts;

    public Group(int id, User owner, String name, ArrayList<Contact> contacts) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.contacts = contacts;
    }

    public Group(int id, User owner, String name) {
        this(id, owner, name, new ArrayList<Contact>());
    }

    /**
     * Crea un'istanza a partire da un'oggetto JSON.
     * 
     * @param JSONGroup rappresentazione JSON del gruppo
     * 
     * @throws JSONException errore durante la lettura di alcuni campi, che
     *      probabilmente non sono stati forniti
     */
    public Group(JSONObject JSONGroup) throws JSONException {
        this.id = JSONGroup.optInt(GroupKeys.ID, -1);
        this.owner = new User(JSONGroup.getJSONObject(GroupKeys.OWNER));
        this.name = JSONGroup.getString(GroupKeys.NAME);
        
        JSONArray JSONContacts = JSONGroup.optJSONArray(GroupKeys.CONTACTS);
        if (JSONContacts == null) {
            this.contacts = new ArrayList<>();
        } else {
            this.contacts = new ArrayList<>(JSONContacts.length());
            JSONContacts.forEach((object) -> {
                if (object instanceof JSONObject) {
                    JSONObject JSONContact = (JSONObject) object;
                    this.contacts.add(new Contact(JSONContact));
                }
            });
        }
    }

    /**
     * Restituisce l'id del gruppo.
     * 
     * @return id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Restituisce l'utente proprietario del gruppo.
     * 
     * @return utente proprietario
     */
    public User getOwner() {
        return this.owner;
    }

    /**
     * Restituisce il nome del gruppo.
     * 
     * @return nome del gruppo
     */
    public String getName() {
        return this.name;
    }

    /**
     * Restituisce i contatti presenti nel gruppo.
     * 
     * @return contatti del gruppo
     */
    public ArrayList<Contact> getContacts() {
        return this.contacts;
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
        if (obj == null || (obj instanceof Group) == false) {
            return false;
        } else {
            Group group = (Group) obj;
            return this.id == group.id && this.name.equals(group.name) &&
                this.owner.equals(group.owner) && this.contacts.equals(group.contacts);
        }
    }
}
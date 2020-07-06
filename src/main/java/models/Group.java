package models;

import java.util.ArrayList;

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

    /**
     * Restituisce l'id del gruppo.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce l'utente proprietario del gruppo.
     * 
     * @return utente proprietario
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Restituisce il nome del gruppo.
     * 
     * @return nome del gruppo
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce i contatti presenti nel gruppo.
     * 
     * @return contatti del gruppo
     */
    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    /**
     * Controlla che due istanze di Group rappresentino gruppi diversi.
     * 
     * @param g1 istanza di Group da confrontare
     * 
     * @return true se rappresentano lo stesso gruppo, altrimenti false
     */
    public boolean equals(Group g1) {
        return g1.id == this.id && g1.name.equals(this.name) &&
            g1.owner.equals(this.owner) && g1.contacts.equals(this.contacts);
    }
}
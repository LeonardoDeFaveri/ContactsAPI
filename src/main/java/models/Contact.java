package models;

import java.util.Arrays;

/**
 * La classe rappresenta un contatto della rubrica.
 */
public class Contact {
    private int id;
    private String firstName;
    private String familyName;
    private String secondName;
    private User owner;
    private User associatedUser;
    private Email[] emails;
    private PhoneNumber[] phoneNumbers;

    public Contact(int id, String firstName, String familyName, String secondName, User owner, User associatedUser,
            Email[] emails, PhoneNumber[] phoneNumbers) {
        this.id = id;
        this.firstName = firstName;
        this.familyName = familyName;
        this.secondName = secondName;
        this.owner = owner;
        this.associatedUser = associatedUser;
        this.emails = emails;
        this.phoneNumbers = phoneNumbers;
    }

    /**
     * Restituisce l'id del contatto.
     * 
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il primo nome del contatto.
     * 
     * @return primo nome
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Restituisce il cognome del contatto.
     * 
     * @return cognome
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Restituisce il secondo nome del contatto.
     * 
     * @return secondo nome
     */
    public String getSecondName() {
        return secondName;
    }

    /**
     * Restituisce l'utente che possiede questo contatto nella propria rubrica.
     * 
     * @return utente proprietario
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Restituisce l'utente rappresentato da questo contatto. Tutti gli utenti sono
     * infatti rappresentanti da un contatto.
     * 
     * @return utente associato
     */
    public User getAssociatedUser() {
        return associatedUser;
    }

    /**
     * Restituisce tutti gli indirizzi email associati al contatto.
     * 
     * @return indirizzi email
     */
    public Email[] getEmails() {
        return this.emails;
    }

    /**
     * Restituisce tutti i numeri di telefono associati al contatto.
     * 
     * @return numeri di telefono
     */
    public PhoneNumber[] getPhoneNumbers() {
        return this.phoneNumbers;
    }

    /**
     * Controlla che due istanze di Contact rappresentino contatti diversi.
     * 
     * @param c1 istanza di Contact da confrontare
     * 
     * @return true se rappresentano lo stesso contatto, altrimenti false
     */
    public boolean equals(Contact c1) {
        return c1.id == this.id && c1.firstName.equals(this.firstName) && 
            c1.familyName.equals(this.familyName) && c1.secondName.equals(this.secondName) &&
            c1.owner.equals(this.owner) && c1.associatedUser.equals(this.associatedUser) &&
            Arrays.equals(c1.emails, this.emails) && Arrays.equals(c1.phoneNumbers, this.phoneNumbers);
    }
}
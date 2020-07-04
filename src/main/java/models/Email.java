package models;

/**
 * La classe rappresenta una delle tante email che possono
 * essere assegnate ad un contatto. Oltre all'indirizzo può
 * anche essere specificata una descrizione.
 */
public class Email {
    private String email;
    private String description;

    public Email(String email, String description) {
        this.email = email;
        this.description = description;
    }

    /**
     * Restituisce l'indirizzo email.
     * 
     * @return indirizzo email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Restituisce la descrizione dell'indirizzo email.
     * 
     * @return descrizione
     */
    public String getDescription() {
        return description;
    }

    /**
     * Controlla che due istanze di Email rappresentino email diverse.
     * 
     * @param e1 istanza di Email da confrontare
     * 
     * @return true se rappresentano la stessa email, altrimenti false
     */
    public boolean equals(Email e1) {
        return e1.email.equals(this.email) && e1.description.equals(this.description);
    }
}
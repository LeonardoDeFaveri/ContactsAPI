package models;

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
     * Controlla che due istanze di User rappresentino utenti diversi.
     * 
     * @param u1 istanza di User da confrontare
     * 
     * @return true se rappresentano lo stesso utente, altrimenti false
     */
    public boolean equals(User u1) {
        return u1.email.equals(this.email) && u1.password.equals(this.password);        
    }
}
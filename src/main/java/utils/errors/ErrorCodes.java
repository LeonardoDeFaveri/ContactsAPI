package utils.errors;

/**
 * Quest'interfaccia contiene tutti i possibili codici di errore
 * che possono essere generati dal servizio.
 */
public interface ErrorCodes {
    /**
     * Si è verificato un errore che non ha permesso di portare
     * a termine con successo la registrazione di un nuovo utente.
     */
    public final int REGISTRATION_FAILURE = 1;
    /**
     * Si è tentato di registrare un nuovo utente con un
     * indirizzo email già associato ad un utente.
     */
    public final int DUPLICATED_USER = 2;
    /**
     * Nella richiesta non sono stati forniti i parametri necessari
     * ad autenticare l'utente, oppure sono stati forniti ma il campo
     * 'Authentication' di è di tipo 'Basic'.
     */
    public final int MISSING_AUTHENTICATION = 3;
    /**
     * Il corpo della richiesta contiene degli errori nella sintassi.
     */
    public final int WRONG_SYNTAX = 4;
    /**
     * Non è stato possibile inserire uno o più valori.
     */
    public final int INSERTION_FAILURE = 5;
    /**
     * L'URL della richiesta non è completo.
     */
    public final int MISSING_URL_COMPONENT = 6;
    /**
     * Nell'url sono presenti componenti che non sono gestiti.
     */
    public final int WRONG_URL_COMPONENT = 7;
    /**
     * Il content type è della richiesta è errato.
     */
    public final int INVALID_CONTENT_TYPE = 8;
    /**
     * L'autenticazione dell'utente è fallita.
     */
    public final int FAILED_AUTHENTICATION = 9;
    /**
     * Le credenziali con le quali si è autenticato l'utente non
     * sono le stesse presenti nel campo 'owner' o, se specificato,
     * nel campo 'associateUser'.
     */
    public final int CREDENTIALS_MISMATCH = 10;
    /**
     * Nell'URL della richiesta è stato fornito un valore che non
     * può essere tradotto in un id numerico.
     */
    public final int WRONG_OBJECT_ID = 11;
    /**
     * È stata inviata un richiesta di modifica di una risorsa
     * (metodo PUT), ma la risorsa modificata coincide con la risorsa
     * originale.
     */
    public final int DATA_NOT_MODIFIABLE = 12;
    /**
     * La modifica di una risorsa non è andata a buon fine.
     */
    public final int DATA_NOT_MODIFIED = 13;
    /**
     * L'eliminazione di una risorsa non è andata a buon fine
     * perchè la risorsa non appartiene all'utente che sta cercando
     * di eliminarla.
     */
    public final int DELETION_UNAUTHORIZED = 14;
    /**
     * L'eliminazione di una risorsa non è andata a buon fine.
     */
    public final int DELETION_FAILED = 15;
    /**
     * L'eliminazione della risorsa non può essere effettuata
     * in quanto la risorsa non può essere eliminata.
     */
    public final int DELETION_NOT_ALLOWED = 16;
    /**
     * Si è tentato di accedere ad una risorsa che non esiste oppure
     * alla quale l'utente non può accedere. 
     */
    public final int INACCESSIBLE_OR_NON_EXISTING_RESOURCE = 17;
}
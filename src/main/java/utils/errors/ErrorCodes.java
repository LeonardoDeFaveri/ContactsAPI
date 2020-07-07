package utils.errors;

/**
 * Quest'interfaccia contiene tutti i possibili codici di errore
 * che possono essere generati dal servizio.
 */
public interface ErrorCodes {
    public final int REGISTRATION_FAILURE = 1;
    public final int DUPLICATED_USER = 2;
    public final int INVALID_CONTENT_TYPE = 3;
    public final int WRONG_SYNTAX = 4;
}
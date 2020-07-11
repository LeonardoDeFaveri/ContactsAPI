package utils.errors;

/**
 * Quest'interfaccia contiene tutti i possibili codici di errore
 * che possono essere generati dal servizio.
 */
public interface ErrorCodes {
    public final int REGISTRATION_FAILURE = 1;
    public final int DUPLICATED_USER = 2;
    public final int MISSING_AUTHENTICATION = 3;
    public final int WRONG_SYNTAX = 4;
    public final int INSERTION_FAILURE = 5;
    public final int MISSING_URL_COMPONENT = 6;
    public final int WRONG_URL_COMPONENT = 7;
    public final int INVALID_CONTENT_TYPE = 8;
    public final int FAILED_AUTHENTICATION = 9;
    public final int CREDENTIALS_MISMATCH = 10;
    public final int WRONG_OBJECT_ID = 11;
}
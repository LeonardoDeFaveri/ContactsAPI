package utils.errors;

import org.json.JSONObject;

public class ErrorHandler {
    public static JSONObject getError(int errorCode) {
        JSONObject error = null;
        switch (errorCode) {
            case ErrorCodes.REGISTRATION_FAILURE:
                error = getRegistrationFailureError();
                break;

            case ErrorCodes.DUPLICATED_USER:
                error = getDuplicatedUserError();
                break;

            case ErrorCodes.MISSING_AUTHENTICATION:
                error = getMissingAuthenticationError();
                break;

            case ErrorCodes.WRONG_SYNTAX:
                error = getWrongSyntaxError();
                break;

            case ErrorCodes.INSERTION_FAILURE:
                error = getInsertionFailureError();
                break;

            case ErrorCodes.MISSING_URL_COMPONENT:
                error = getMissingURLComponent();
                break;

            case ErrorCodes.WRONG_URL_COMPONENT:
                error = getWrongURLComponentError();
                break;

            case ErrorCodes.INVALID_CONTENT_TYPE:
                error = getInvalidContentTypeError();
                break;

            case ErrorCodes.FAILED_AUTHENTICATION:
                error = getFailedAuthenticationError();
                break;

            case ErrorCodes.CREDENTIALS_MISMATCH:
                error = getCredentialsMismatchError();
                break;

            case ErrorCodes.WRONG_OBJECT_ID:
                error = getWrongObjectId();
                break;

            case ErrorCodes.DATA_NOT_MODIFIABLE:
                error = getDataNotModifiableError();
                break;

            case ErrorCodes.DATA_NOT_MODIFIED:
                error = getDataNotModifiedError();
                break;

            case ErrorCodes.DELETION_UNAUTHORIZED:
                error = getDelitionUnauthorizedError();
                break;

            default:
                error = new JSONObject();
                break;
        }
        return error;
    }

    /**
     * Si è verificato un errore che non ha permesso di portare
     * a termine con successo la registrazione di un nuovo utente.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getRegistrationFailureError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Error during registration");
        error.put(ErrorKeys.CODE, ErrorCodes.REGISTRATION_FAILURE);
        error.put(ErrorKeys.MESSAGE, "An error during registration has occured");
        error.put(ErrorKeys.SUGGESTION, "Retry later");
        return error;
    }

    /**
     * Si sta tentando di registrare un nuovo utente con un
     * indirizzo email già associato ad un utente.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getDuplicatedUserError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.WARNING);
        error.put(ErrorKeys.TITLE, "Error during registration");
        error.put(ErrorKeys.CODE, ErrorCodes.DUPLICATED_USER);
        error.put(ErrorKeys.MESSAGE, "A user with the same email has been detected");
        error.put(ErrorKeys.SUGGESTION, "Try logging in with the same credentials used to register");
        return error;
    }

    /**
     * Nella richiesta non sono stati forniti i parametri necessari
     * ad autenticare l'utente, oppure sono stati forniti ma il campo
     * 'Authentication' di è di tipo 'Basic'.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getMissingAuthenticationError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Missing Authenticatio");
        error.put(ErrorKeys.CODE, ErrorCodes.MISSING_AUTHENTICATION);
        error.put(ErrorKeys.MESSAGE, "Authentication parameters have not been provided");
        error.put(ErrorKeys.SUGGESTION,
            "Provide authentication parameters or check the 'Authentication' header: it must be of type 'basic'");
        return error;
    }

    /**
     * Il corpo della richiesta contiene degli errori nella sintassi.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getWrongSyntaxError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Error while reading the message");
        error.put(ErrorKeys.CODE, ErrorCodes.WRONG_SYNTAX);
        error.put(ErrorKeys.MESSAGE, "An error has occured while trying to read the message and pull out the needed information.");
        error.put(ErrorKeys.SUGGESTION, "Try checking the syntax");
        return error;
    }

    /**
     * Non è stato possibile inserire uno o più valori.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getInsertionFailureError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.CODE, ErrorCodes.INSERTION_FAILURE);
        error.put(ErrorKeys.TITLE, "Insertion failure");
        error.put(ErrorKeys.MESSAGE, "Some values have not been inserted");
        error.put(ErrorKeys.SUGGESTION, "Try checking the values and retry");
        return error;
    }

    /**
     * L'URL della richiesta non è completo.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getMissingURLComponent() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Error while interpreting the URL");
        error.put(ErrorKeys.CODE, ErrorCodes.MISSING_URL_COMPONENT);
        error.put(ErrorKeys.MESSAGE, "The URL is incomplete");
        error.put(ErrorKeys.SUGGESTION, "Try specifying one or more components in the URL");
        return error;
    }

    /**
     * Nell'url sono presenti componenti che non sono gestiti.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getWrongURLComponentError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Error while interpreting the URL");
        error.put(ErrorKeys.CODE, ErrorCodes.WRONG_URL_COMPONENT);
        error.put(ErrorKeys.MESSAGE, "The URL is incorrect");
        error.put(ErrorKeys.SUGGESTION, "Try specifying valid components in the URL");
        return error;
    }

    /**
     * Il content type è della richiesta è errato.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getInvalidContentTypeError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Error while receiving the message");
        error.put(ErrorKeys.CODE, ErrorCodes.INVALID_CONTENT_TYPE);
        error.put(ErrorKeys.MESSAGE, "An error has occured while receiving the message. The contentType is incorrect");
        error.put(ErrorKeys.SUGGESTION, "Try changing the content type to 'application/json' or try sending a JSON file");
        return error;
    }

    /**
     * L'autenticazione dell'utente è fallita.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getFailedAuthenticationError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Failed Authenticatio");
        error.put(ErrorKeys.CODE, ErrorCodes.FAILED_AUTHENTICATION);
        error.put(ErrorKeys.MESSAGE, "The authentication process has failed");
        error.put(ErrorKeys.SUGGESTION, "Try checking the authentication parameters provided");
        return error;
    }

    /**
     * Le credenziali con le quali si è autenticato l'utente non
     * sono le stesse presenti nel campo 'owner' o, se specificato,
     * nel campo 'associateUser'.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getCredentialsMismatchError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Failed Authentication");
        error.put(ErrorKeys.CODE, ErrorCodes.CREDENTIALS_MISMATCH);
        error.put(ErrorKeys.MESSAGE,
            "The credentials of the user are different from the credentials of the owner user and/or from the credentials of the associated user");
        error.put(ErrorKeys.SUGGESTION,
            "Try checking the authentication parameters provided or the owner and/or associated user sent");
        return error;
    }

    /**
     * Nell'URL della richiesta è stato fornito un valore che non
     * può essere tradotto in un id numerico.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getWrongObjectId() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Error while interpreting the URL");
        error.put(ErrorKeys.CODE, ErrorCodes.WRONG_OBJECT_ID);
        error.put(ErrorKeys.MESSAGE, "The object id is incorrect");
        error.put(ErrorKeys.SUGGESTION, "Try checking the object id in the URL");
        return error;
    }

    /**
     * È stata inviata un richiesta di modifica di una risorsa
     * (metodo PUT), ma la risorsa modificata coincide con la risorsa
     * originale.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getDataNotModifiableError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.WARNING);
        error.put(ErrorKeys.TITLE, "Resource modification not required");
        error.put(ErrorKeys.CODE, ErrorCodes.DATA_NOT_MODIFIABLE);
        error.put(ErrorKeys.MESSAGE, "The original resource and the updated one are equal, so none update operation has been performed");
        error.put(ErrorKeys.SUGGESTION, "Try checking the values of the fields that you want to modify");
        return error;
    }

    /**
     * La modifica di una risorsa non è andata a buon fine.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getDataNotModifiedError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Resource modification failed");
        error.put(ErrorKeys.CODE, ErrorCodes.DATA_NOT_MODIFIED);
        error.put(ErrorKeys.MESSAGE, "The modification of one or more resources has failed");
        error.put(ErrorKeys.SUGGESTION, "Try checking the values of the fields that you want to modify");
        return error;
    }

    /**
     * L'eliminazione di una risorsa non è andata a buon fine
     * perchè la risorsa non appartiene all'utente che sta cercando
     * di eliminarla.
     * 
     * @return messaggio di errore
     */
    private static JSONObject getDelitionUnauthorizedError() {
        JSONObject error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Resource delition unauthorized");
        error.put(ErrorKeys.CODE, ErrorCodes.DELETION_UNAUTHORIZED);
        error.put(ErrorKeys.MESSAGE, "You cannot delete a resource that doesn't belong to you");
        error.put(ErrorKeys.SUGGESTION, "Try checking the resource identifier provided or try logging in with the right credentials");
        return error;
    }
}
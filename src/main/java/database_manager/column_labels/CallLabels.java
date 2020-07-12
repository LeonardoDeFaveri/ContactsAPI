package database_manager.column_labels;

/**
 * Quest'interfaccia contiene i nomi di tutte le colonne
 * della tabella 'calls'.
 */
public interface CallLabels {
    public final String ID = "id";
    public final String CALLER_NUMBER = "caller_number_id";
    public final String CALLER_CONTACT = "caller_contact_id";
    public final String CALLED_NUMBER = "called_number_id";
    public final String CALLED_CONTACT = "called_contact_id";
    public final String TIMESTAMP = "call_timestamp";
    public final String DURATION = "duration";
}
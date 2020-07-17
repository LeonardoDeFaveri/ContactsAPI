START TRANSACTION;

/**
 *  test_credentials cerca un utente con email
 *  e password coindenti con i valori dei parametri.
 *
 *  @RETURN true viene trovato un utente, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION test_credentials (
    p_email VARCHAR (320),
    p_password CHAR (64)
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (SELECT COUNT(email) FROM users WHERE email = p_email AND password = p_password) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

-- CONTROLLI DI ESISTENZA ------------------------------------------------------------------------

/**
 *  check_user_existence controlla l'esistenza di un utente.
 *
 *  @RETURN true se l'utente esiste, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION check_user_existence (
    p_email VARCHAR (320)
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (SELECT COUNT(email) FROM users WHERE email = p_email) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

/**
 *  check_contact_existence controlla l'esistenza di un contatto.
 *
 *  @RETURN true se il contatto esiste, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION check_contact_existence (
    p_first_name VARCHAR (30),
    p_family_name VARCHAR (30),
    p_owner_user VARCHAR (320)
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (
        SELECT COUNT(id) FROM contacts WHERE
            first_name = p_first_name AND
            family_name = p_family_name AND
            owner_user = p_owner_user
    ) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

/**
 *  check_group_existence controlla l'esistenza di un gruppo.
 *
 *  @RETURN true se il gruppo esiste, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION check_group_existence (
    p_name VARCHAR (50),
    p_owner_user VARCHAR (320)
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (
        SELECT COUNT(id) FROM groups WHERE
            name = p_name AND
            owner_user = p_owner_user
    ) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

/**
 *  check_phone_number_existence controlla l'esistenza di un numero
 *  di telefono.
 *
 *  @RETURN true se il numero di telefono esiste, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION check_phone_number_existence (
    p_country_code VARCHAR (3),
    p_area_code CHAR (3),
    p_prefix CHAR (3),
    p_phone_line CHAR (4)
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (
        SELECT COUNT(id) FROM phone_numbers WHERE
            country_code = p_country_code AND
            area_code = p_area_code AND
            prefix = p_prefix AND
            phone_line = p_phone_line
    ) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

/**
 *  check_email_existence controlla l'esistenza di un indirizzo
 *  email.
 *
 *  @RETURN true se l'indirizzo email esiste, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION check_email_existence (
    p_email VARCHAR (320)
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (
        SELECT COUNT(email) FROM emails WHERE email = p_email
    ) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

/**
 *  check_contact_to_group_association_existence controlla l'esistenza
 *  di una chiamata.
 *
 *  @RETURN true se la chiamata esiste, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION check_call_existence (
    p_caller_number_id INT,
    p_caller_contact_id INT,
    p_called_number_id INT,
    p_call_timestamp DATETIME
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (
        SELECT COUNT(caller_number_id) FROM calls WHERE
            caller_number_id = p_caller_number_id AND caller_contact_id = p_caller_contact_id AND called_number_id = p_called_number_id AND call_timestamp = p_call_timestamp
    ) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

-- CONTROLLI DI ESISTENZA ASSOCIAZIONI -----------------------------------------------------------

/**
 *  check_phone_number_to_contact_association_existence controlla l'esistenza
 *  dell'associazione tra un numero di telefono e un contatto.
 *
 *  @RETURN true se l'associazione esiste, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION check_phone_number_to_contact_association_existence (
    p_phone_id INT,
    p_contact_id INT
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (
        SELECT COUNT(phone_id) FROM contacts_numbers WHERE
            phone_id = p_phone_id AND contact_id = p_contact_id
    ) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

/**
 *  check_email_to_contact_association_existence controlla l'esistenza
 *  dell'associazione tra un indirizzo email e un contatto.
 *
 *  @RETURN true se l'associazione esiste, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION check_email_to_contact_association_existence (
    p_email VARCHAR (320),
    p_contact_id INT
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (
        SELECT COUNT(email) FROM contacts_emails WHERE
            email = p_email AND contact_id = p_contact_id
    ) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

/**
 *  check_contact_to_group_association_existence controlla l'esistenza
 *  dell'associazione tra un contatto e un gruppo.
 *
 *  @RETURN true se l'associazione esiste, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION check_contact_to_group_association_existence (
    p_group_id INT,
    p_contact_id INT
) RETURNS BOOLEAN READS SQL DATA
BEGIN
    IF (
        SELECT COUNT(group_id) FROM groups_contacts WHERE
            group_id = p_group_id AND contact_id = p_contact_id AND until IS NULL
    ) = 0 THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END //
DELIMITER ;

-- INSERIMENTI -----------------------------------------------------------------------------------

/**
 *  insert_user crea un nuovo utente e il contatto ad
 *  esso associato.
 *
 *  @RETURN id del contatto se è stato creato con successo,
 *     0 se l'utente esiste già e -1 se si è verificato un
 *     errore durante l'inserimento del contatto
 */
DELIMITER //
CREATE OR REPLACE FUNCTION insert_user (
    p_email VARCHAR (320),
    p_password CHAR (64),
    p_first_name VARCHAR (30),
    p_family_name VARCHAR (30),
    p_second_name VARCHAR (60)
) RETURNS INT MODIFIES SQL DATA
BEGIN
    IF (SELECT check_user_existence(p_email)) = FALSE THEN   
        INSERT INTO users (email, password) VALUES (p_email, p_password);
        RETURN (
            SELECT insert_contact(
                p_first_name,p_family_name, p_second_name,
                p_email, p_email
            )
        );
    ELSE 
        RETURN 0;
    END IF;
END //
DELIMITER ;

/**
 *  insert_contact crea un nuovo contatto.
 *
 *  @RETURN id del contatto se è stato creato con successo
 *      o se esisteva già, altrimenti -1
 */
DELIMITER //
CREATE OR REPLACE FUNCTION insert_contact (
    p_first_name VARCHAR (30),
    p_family_name VARCHAR (30),
    p_second_name VARCHAR (60),
    p_owner_user VARCHAR (320),
    p_associated_user VARCHAR (320)
) RETURNS INT MODIFIES SQL DATA
BEGIN
    DECLARE var_contact_id INT;

    IF (SELECT check_contact_existence(p_first_name, p_family_name, p_owner_user) = FALSE) THEN
        INSERT INTO contacts (first_name, family_name, second_name, owner_user, associated_user)
            VALUES (p_first_name, p_family_name, p_second_name, p_owner_user, p_associated_user);
    END IF;

    SELECT id INTO var_contact_id FROM contacts WHERE
        first_name = p_first_name AND
        family_name = p_family_name AND
        owner_user = p_owner_user;

    IF var_contact_id IS NULL THEN
        RETURN -1;
    END IF;

    RETURN var_contact_id;
END //
DELIMITER ;

/**
 *  insert_group crea un nuovo gruppo.
 *
 *  @RETURN id del gruppo se è stato creato con successo
 *      o se esisteva già, altrimenti -1
 */
DELIMITER //
CREATE OR REPLACE FUNCTION insert_group (
    p_name VARCHAR (50),
    p_owner_user VARCHAR (320)
) RETURNS INT MODIFIES SQL DATA
BEGIN
    DECLARE var_group_id INT;

    IF (SELECT check_group_existence(p_name, p_owner_user) = FALSE) THEN
        INSERT INTO groups (name, owner_user) VALUES (p_name, p_owner_user);
    END IF;

    SELECT id INTO var_group_id FROM groups WHERE
        name = p_name AND
        owner_user = p_owner_user;

    IF var_group_id IS NULL THEN
        RETURN -1;
    END IF;

    RETURN var_group_id;
END //
DELIMITER ;

/**
 * insert_contact_in_group inserisce un contatto in un gruppo.
 * Quando il contatto viene inserito il campo 'since' viene impostato
 * all'istante attuale.
 *
 * @RETURN true se il contatto è stato inserito o era già presente nel 
 * gruppo, altrimenti false
 */
DELIMITER //
CREATE OR REPLACE FUNCTION insert_contact_in_group (
    p_group_id INT,
    p_contact_id INT
) RETURNS BOOLEAN MODIFIES SQL DATA
BEGIN
    IF (SELECT check_contact_to_group_association_existence(p_group_id, p_contact_id) = FALSE) THEN
        INSERT INTO groups_contacts (group_id, contact_id) VALUES (p_group_id, p_contact_id);
    END IF;

    RETURN (SELECT check_contact_to_group_association_existence(p_group_id, p_contact_id));
END //
DELIMITER ;

/**
 *  insert_phone_number crea un numero di telefono e lo
 *  associa ad un contatto.
 *
 *  @RETURN id del numero di telefono se è stato inserito
 *      con successo, 0 se il numero esiste già ed è già
 *      stato associato al contatto e -1 se si è verificato
 *      un errore durante l'inserimento
 */
DELIMITER //
CREATE OR REPLACE FUNCTION insert_phone_number (
    p_country_code VARCHAR (3),
    p_area_code CHAR (3),
    p_prefix CHAR (3),
    p_phone_line CHAR (4),
    p_description VARCHAR (50),
    p_contact_id INT
) RETURNS INT MODIFIES SQL DATA
BEGIN
    DECLARE var_phone_id INT;

    -- Controlla che il numero di telefono non sia già presente nel database
    IF (
        SELECT check_phone_number_existence(p_country_code, p_area_code, p_prefix, p_phone_line)
    ) = FALSE THEN
        INSERT INTO phone_numbers (country_code, area_code, prefix, phone_line, description)
            VALUES (p_country_code, p_area_code, p_prefix, p_phone_line, p_description);
    END IF;

    -- Estrae l'id del numero di telefono
    SELECT id INTO var_phone_id FROM phone_numbers WHERE
        country_code = p_country_code AND
        area_code = p_area_code AND
        prefix = p_prefix AND
        phone_line = p_phone_line;

    IF var_phone_id IS NULL THEN
        RETURN -1;
    END IF;

    -- Controlla che il numero di telefono non sia già stato associato al contatto specificato
    IF (
        SELECT check_phone_number_to_contact_association_existence(
            var_phone_id, p_contact_id
        )
    ) = FALSE THEN
        INSERT INTO contacts_numbers (phone_id, contact_id) VALUES (var_phone_id, p_contact_id);
        RETURN var_phone_id;
    ELSE
        RETURN 0;
    END IF;
END //
DELIMITER ;

/**
 *  insert_email crea un indirizzo email e lo
 *  associa ad un contatto.
 *
 *  @RETURN TRUE se l'indirizzo email è stato inserito
 *      con successo, FALSE se l'indirizzo esiste già ed è già
 *      stato associato al contatto
 */
DELIMITER //
CREATE OR REPLACE FUNCTION insert_email (
    p_email VARCHAR (320),
    p_description VARCHAR (50),
    p_contact_id INT
) RETURNS BOOLEAN MODIFIES SQL DATA
BEGIN
    IF (SELECT check_email_existence(p_email)) = FALSE THEN
        INSERT INTO emails (email, description) VALUES (p_email, p_description);
    END IF;

    IF (
        SELECT check_email_to_contact_association_existence(
            p_email, p_contact_id
        )
    ) = FALSE THEN
        INSERT INTO contacts_emails (email, contact_id) VALUES (p_email, p_contact_id);
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END //
DELIMITER ;

/**
 *  insert_email inserisce una chiamata.
 *
 *  @RETURN id della chiamata se è stata inserita, altrimenti -1
 */
DELIMITER //
CREATE OR REPLACE FUNCTION insert_call (
    p_caller_number_id INT,
    p_caller_contact_id INT,
    p_called_number_id INT,
    p_called_contact_id INT,
    p_call_timestamp DATETIME,
    p_duration INT
) RETURNS INT MODIFIES SQL DATA
BEGIN
    DECLARE var_call_id INT; 

    IF (
        SELECT check_call_existence(p_caller_number_id, p_caller_contact_id, p_called_number_id, p_call_timestamp)
    ) = FALSE THEN
        INSERT INTO calls (
            caller_number_id, caller_contact_id, called_number_id,
            called_contact_id, call_timestamp, duration
        ) VALUES (
            p_caller_number_id, p_caller_contact_id, p_called_number_id,
            p_called_contact_id, p_call_timestamp, p_duration
        );
    END IF;

    SELECT id INTO var_call_id FROM calls WHERE 
        caller_number_id = p_caller_number_id AND called_number_id = p_called_number_id AND call_timestamp = p_call_timestamp;

    IF var_call_id IS NULL THEN
        RETURN -1;
    ELSE
        RETURN var_call_id;
    END IF;
END //
DELIMITER ;

-- AGGIORNAMENTI ---------------------------------------------------------------------------------

/**
 * update_contact_phone_number sostituisce il numero di telefono
 * associato al contatto con un nuovo numero.
 *
 * @RETURN TRUE se il numero di telefono è stato cambiato, altrimenti FALSE
 */
DELIMITER //
CREATE OR REPLACE FUNCTION update_contact_phone_number (
    p_contact_id INT,
    p_phone_id INT,
    p_country_code VARCHAR (3),
    p_area_code CHAR (3),
    p_prefix CHAR (3),
    p_phone_line CHAR (4),
    p_description VARCHAR (50)
) RETURNS BOOLEAN MODIFIES SQL DATA
BEGIN
    DECLARE var_new_phone_id INT;

    IF (
        SELECT check_phone_number_existence(p_country_code, p_area_code, p_prefix, p_phone_line
    ) = FALSE) THEN
        INSERT INTO phone_numbers (country_code, area_code, prefix, phone_line, description)
            VALUES (p_country_code, p_area_code, p_prefix, p_phone_line, p_description);
    END IF;

    SELECT id INTO var_new_phone_id FROM phone_numbers WHERE
        country_code = p_country_code AND
        area_code = p_area_code AND
        prefix = p_prefix AND
        phone_line = p_phone_line;

    IF var_new_phone_id IS NULL THEN
        RETURN FALSE;
    END IF;

    UPDATE contacts_numbers SET phone_id = var_new_phone_id WHERE 
        contact_id = p_contact_id AND phone_id = p_phone_id;

    IF (SELECT ROW_COUNT()) = 1 THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END //
DELIMITER ;

/**
 * update_contact_email sostitusice l'indirizzo email
 * associato al contatto con un nuovo indirizzo.
 *
 * @RETURN TRUE se l'indirizzo è stato cambiato, altrimenti FALSE
 */
DELIMITER //
CREATE OR REPLACE FUNCTION update_contact_email (
    p_contact_id INT,
    p_old_email VARCHAR (320),
    p_new_email VARCHAR (320),
    p_description VARCHAR (50)
) RETURNS BOOLEAN MODIFIES SQL DATA
BEGIN
    IF (SELECT check_email_existence(p_new_email) = FALSE) THEN
        INSERT INTO emails (email, description) VALUES (p_new_email, p_description);
    END IF;

    UPDATE contacts_emails SET email = p_new_email WHERE 
        contact_id = p_contact_id AND email = p_old_email;

    IF (SELECT ROW_COUNT()) = 1 THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END //
DELIMITER ;

COMMIT;
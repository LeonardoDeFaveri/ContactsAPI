START TRANSACTION;

DROP DATABASE IF EXISTS contacts;
CREATE DATABASE contacts;
USE contacts;

-- Utenti dell'applicazione
CREATE TABLE users (
    email VARCHAR (320) NOT NULL,
    password CHAR (64) NOT NULL,

    PRIMARY KEY (email)
);

-- Contatti di ogni utente
CREATE TABLE contacts (
    id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR (30) NOT NULL,
    family_name VARCHAR (30) NOT NULL,
    second_name VARCHAR (60),
    owner_user VARCHAR (320) NOT NULL,
    -- Ogni utente è rappresentato da un contatto
    associated_user VARCHAR (320),

    PRIMARY KEY (id),
    UNIQUE (first_name, family_name, owner_user),
    CONSTRAINT fk_contact_to_owner FOREIGN KEY (owner_user) 
        REFERENCES users (email)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_contact_to_associated_user FOREIGN KEY (associated_user)
        REFERENCES users (email)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Numeri di telefono di ogni contatto
CREATE TABLE phone_numbers (
    id INT NOT NULL AUTO_INCREMENT,
    country_code VARCHAR (3) NOT NULL,
    area_code CHAR (3) NOT NULL,
    prefix CHAR (3) NOT NULL,
    phone_line CHAR (4) NOT NULL,
    description VARCHAR (50),

    PRIMARY KEY (id),
    UNIQUE (country_code, area_code, prefix, phone_line)
);

-- Associazioni tra i numeri di telefono e i contatti
CREATE TABLE contacts_numbers (
    phone_id INT NOT NULL,
    contact_id INT NOT NULL,

    PRIMARY KEY (phone_id, contact_id),
    CONSTRAINT fk_contacts_numbers_to_phone_numbers FOREIGN KEY (phone_id)
        REFERENCES phone_numbers (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_contacts_numbers_to_contacts FOREIGN KEY (contact_id)
        REFERENCES contacts (id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Email dei contatti
CREATE TABLE emails (
    email VARCHAR (320) NOT NULL,
    description VARCHAR (50),

    PRIMARY KEY (email)
);

-- Associazioni tra gli indirizzi email e i contatti
CREATE TABLE contacts_emails (
    email VARCHAR (320) NOT NULL,
    contact_id INT NOT NULL,

    PRIMARY KEY (email, contact_id),
    CONSTRAINT fk_contacts_emails_to_email FOREIGN KEY (email)
        REFERENCES emails (email)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_contacts_emails_to_contacts FOREIGN KEY (contact_id)
        REFERENCES contacts (id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tutti i gruppi creati dagli utenti
CREATE TABLE groups (
    id INT AUTO_INCREMENT,
    owner_user VARCHAR (320) NOT NULL,
    name VARCHAR (50) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (owner_user, name),
    CONSTRAINT fk_groups_to_users FOREIGN KEY (owner_user)
        REFERENCES users (email)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Associazioni tra i gruppi e i relativi contatti
CREATE TABLE groups_contacts (
    group_id INT NOT NULL AUTO_INCREMENT,
    contact_id INT NOT NULL,
    since DATETIME NOT NULL DEFAULT UTC_TIMESTAMP(),
    until DATETIME,

    PRIMARY KEY (group_id, contact_id, since),
    CONSTRAINT fk_groups_contacts_to_groups FOREIGN KEY (group_id)
        REFERENCES groups (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_groups_contacts_to_contacts FOREIGN KEY (contact_id)
        REFERENCES contacts (id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tutte le chiamate effettuate
CREATE TABLE calls (
    id INT NOT NULL AUTO_INCREMENT,
    caller_number_id INT NOT NULL,
    caller_contact_id INT NOT NULL,
    called_number_id INT NOT NULL,
    called_contact_id INT,
    call_timestamp DATETIME NOT NULL,
    duration BIGINT NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    UNIQUE (caller_number_id, caller_contact_id, called_number_id, call_timestamp),
    CONSTRAINT fk_caller_to_phone_numbers FOREIGN KEY (caller_number_id)
        REFERENCES phone_numbers (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_called_to_phone_numbers FOREIGN KEY (called_number_id)
        REFERENCES phone_numbers (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_caller_to_contacts FOREIGN KEY (caller_contact_id)
        REFERENCES phone_numbers (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_called_to_contacts FOREIGN KEY (called_contact_id)
        REFERENCES phone_numbers (id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

COMMIT;
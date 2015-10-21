DROP DATABASE IF EXISTS pokerdb;
CREATE DATABASE pokerdb;

CREATE TABLE pokerdb.poker_types (
	id INTEGER NOT NULL AUTO_INCREMENT,
	name VARCHAR(10) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT UQ_POKER_TYPES_NAME UNIQUE (name)
);

CREATE TABLE pokerdb.users (
	id INTEGER NOT NULL AUTO_INCREMENT,
	username VARCHAR(20) NOT NULL,
	balance DECIMAL(65,18) NOT NULL,
	reg_date BIGINT,
	password VARCHAR(64) NOT NULL,
	admin BOOLEAN DEFAULT FALSE,
	PRIMARY KEY (id),
	CONSTRAINT UQ_users_username UNIQUE (username)
);
	
CREATE TABLE pokerdb.poker_tables (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    poker_type_id INTEGER,
    max_time INTEGER NOT NULL,
    max_players INTEGER NOT NULL,
    default_pot DECIMAL(19 , 2 ) NOT NULL,
    max_bet DECIMAL(19 , 2 ) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UQ_poker_table_name UNIQUE (name),
    FOREIGN KEY (poker_type_id) REFERENCES pokerdb.poker_types(id) ON DELETE CASCADE
);

--ALTER TABLE pokerdb.users ADD CONSTRAINT CONSTRAINT_USERS_USERNAME_LENGHT CHECK (!(3 <= CHAR_LENGTH(username) && CHAR_LENGTH(username) <= 20));
--ALTER TABLE pokerdb.poker_tables ADD CONSTRAINT CONSTRAINT_POKER_TABLES_NAME_LENGHT CHECK (CHAR_LENGTH(name) < 30);


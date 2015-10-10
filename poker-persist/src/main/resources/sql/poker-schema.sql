DROP DATABASE IF EXISTS pokerdb;
CREATE DATABASE pokerdb;

DROP TABLE IF EXISTS pokerdb.users;
DROP TABLE IF EXISTS pokerdb.poker_tables;

CREATE TABLE pokerdb.users (
	id INTEGER NOT NULL AUTO_INCREMENT,
	username VARCHAR(20) NOT NULL check (CHAR_LENGTH(username) <= 20),
	balance DECIMAL(19,2) NOT NULL,
	reg_date BIGINT,
	password VARCHAR(64) NOT NULL check (CHAR_LENGTH(password) <= 64),
	PRIMARY KEY (id),
	UNIQUE(username)
);
	
CREATE TABLE pokerdb.poker_tables (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL CHECK (CHAR_LENGTH(name) <= 30),
    poker_type VARCHAR(10) CHECK (CHAR_LENGTH(poker_type) <= 10),
    max_time INTEGER NOT NULL,
    max_players INTEGER NOT NULL,
    default_pot DECIMAL(19 , 2 ) NOT NULL,
    max_bet DECIMAL(19 , 2 ) NOT NULL,
    PRIMARY KEY (id)
);
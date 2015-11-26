DROP DATABASE IF EXISTS pokerdb;
CREATE DATABASE pokerdb;

CREATE TABLE pokerdb.poker_types (
	id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
	name VARCHAR(10) NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT UQ_POKER_TYPES_NAME UNIQUE (name)
);

CREATE TABLE pokerdb.users (
	id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
	username VARCHAR(20) NOT NULL,
	balance DECIMAL(65,18) NOT NULL,
	reg_date BIGINT,
	password VARCHAR(64) NOT NULL,
	admin BOOLEAN DEFAULT FALSE,
	PRIMARY KEY (id),
	CONSTRAINT UQ_users_username UNIQUE (username)
);
	
CREATE TABLE pokerdb.poker_tables (
    id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    poker_type_id TINYINT UNSIGNED,
    max_time TINYINT UNSIGNED NOT NULL,
    max_players TINYINT UNSIGNED NOT NULL,
    big_blind DECIMAL(65,18) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UQ_poker_table_name UNIQUE (name),
    FOREIGN KEY (poker_type_id) REFERENCES pokerdb.poker_types(id) ON DELETE CASCADE
);

DELIMITER /

CREATE TRIGGER pokerdb.create_table_trigger BEFORE INSERT ON pokerdb.poker_tables
	FOR EACH ROW
	BEGIN
		IF (CHAR_LENGTH(NEW.name) > 30) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'CONSTRAINT_POKER_TABLES_NAME_LENGHT';
		end IF;
		IF (!(5 <= NEW.max_time && NEW.max_time <= 40)) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'CONSTRAINT_POKER_TABLES_MAX_TIME';
		end IF;
	
		IF (!(2 <= NEW.max_players && NEW.max_players <= 5)) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'CONSTRAINT_POKER_TABLES_MAX_PLAYERS';
		end IF;
	END;
/

CREATE TRIGGER pokerdb.update_table_trigger BEFORE UPDATE ON pokerdb.poker_tables
	FOR EACH ROW
	BEGIN
		IF (CHAR_LENGTH(NEW.name) > 30) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'CONSTRAINT_POKER_TABLES_NAME_LENGHT';
		end IF;
		IF (!(5 <= NEW.max_time && NEW.max_time <= 40)) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'CONSTRAINT_POKER_TABLES_MAX_TIME';
		end IF;
	
		IF (!(2 <= NEW.max_players && NEW.max_players <= 5)) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'CONSTRAINT_POKER_TABLES_MAX_PLAYERS';
		end IF;
	END;
/

CREATE TRIGGER pokerdb.create_user_trigger BEFORE INSERT ON pokerdb.users
	FOR EACH ROW
	BEGIN
		SET @c = CHAR_LENGTH(NEW.username);
		IF (!(3 <= @c && @c <= 20)) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'CONSTRAINT_USERS_USERNAME_LENGHT';
		END IF;
		
		IF (CHAR_LENGTH(NEW.password) > 64) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'CONSTRAINT_USERS_PASSWORD_LENGHT';
		END IF;
	
		SET NEW.reg_date = UNIX_TIMESTAMP();
	END;
/


DELIMITER ;

insert into pokerdb.users (balance, username, password, admin)
	values(
		10000.00,
		'asd',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq',
		TRUE
	);
	
insert into pokerdb.users (balance, username, password, admin)
	values(
		10000.00,
		'asd2',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq',
		TRUE
	);
	
insert into pokerdb.users (balance, username, password, admin)
	values(
		10000.00,
		'asd3',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq',
		FALSE
	);
	
insert into pokerdb.users (balance, username, password, admin)
	values(
		10000.00,
		'asd4',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq',
		FALSE
	);
	
insert into pokerdb.users (balance, username, password, admin)
	values(
		10000.00,
		'asd5',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq',
		FALSE
	);
	
insert into pokerdb.users (balance, username, password, admin)
	values(
		10000.00,
		'asd6',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq',
		FALSE
	);
	
insert into pokerdb.poker_types (name) values('HOLDEM');
insert into pokerdb.poker_types (name) values('CLASSIC');

insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, big_blind)
	values('szerver1', 1, 5, 5, 100);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, big_blind)
	values('#PL_125Q', 2, 39, 5, 200);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, big_blind)
	values('Holdem fun', 1, 15, 5, 100);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, big_blind)
	values('Classic fun', 2, 5, 5, 20);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, big_blind)
	values('?^xW!', 2, 23, 3, 1);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, big_blind)
	values('ûáûúüüéáûúõöüóÍ', 1, 33, 2, 10);
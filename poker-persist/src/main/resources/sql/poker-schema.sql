DROP TABLE IF EXISTS pokerdb.User;
DROP TABLE IF EXISTS pokerdb.PTable;

CREATE TABLE pokerdb.User (
	id INTEGER NOT NULL AUTO_INCREMENT,
	balance DECIMAL(19,2) NOT NULL,
	reg_date BIGINT NOT NULL,
	username VARCHAR(20) NOT NULL check (CHAR_LENGTH(username) <= 20),
	password VARCHAR(64) NOT NULL check (CHAR_LENGTH(password) <= 64),
	primary key (id));
	
CREATE TABLE pokerdb.PTable (
	id INTEGER NOT NULL AUTO_INCREMENT,
	big_blind DECIMAL(19,2) NOT NULL,
	max_Bet DECIMAL(19,2) NOT NULL,
	max_players INTEGER NOT NULL check (2<=max_players AND max_players<=6),
	max_time INTEGER NOT NULL check (5<=max_time AND max_time<=40),
	name VARCHAR(30) NOT NULL check (CHAR_LENGTH(name) <= 30),
	small_blind DECIMAL(19,2) NOT NULL,
	poker_type VARCHAR(10) check (CHAR_LENGTH(poker_type) <= 10),
	PRIMARY KEY (id));
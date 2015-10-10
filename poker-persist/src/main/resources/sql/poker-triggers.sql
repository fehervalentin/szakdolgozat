CREATE TRIGGER pokerdb.create_table_trigger BEFORE INSERT ON pokerdb.poker_tables
	FOR EACH ROW
	BEGIN
		IF (!(5 <= NEW.max_time && NEW.max_time <= 40)) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'A gondolkodasi ido nem esik bele a [5-40] intervallumba!';
		end IF;
	
		IF (!(2 <= NEW.max_players && NEW.max_players <= 6)) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'A jatekosok szama nem esik bele a [2-6] intervallumba!';
		end IF;
		
		IF (NEW.default_pot > NEW.max_bet) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Az alaptet nem lehet lehet nagyobb a limitnel!';
		end IF;
	END;
/

CREATE TRIGGER pokerdb.create_user_trigger BEFORE INSERT ON pokerdb.users
	FOR EACH ROW
	BEGIN
		SET @c = CHAR_LENGTH(NEW.username);
		IF (!(3 <= @c && @c <= 20)) THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'A felhasznalonev nem esik bele a [3-20] intervallumba!';
		end IF;
	
		SET NEW.reg_date = UNIX_TIMESTAMP();
	END;
/

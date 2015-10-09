insert into pokerdb.users (balance, reg_date, username, password)
	values(
		0,
		1,
		'asd',
		'$2a$10$PVuC8V/XNVdaIcSQxQSBQ.x5DeVSOrql11mbFRUG1wZq2dlplFosq'
	);

insert into pokerdb.poker_tables (name, poker_type, max_time, max_players, small_blind, big_blind, max_Bet)
	values('szerver', 'HOLDEM', 23, 4, 21.0, 42.0, 102);
	
insert into pokerdb.poker_tables (name, poker_type, max_time, max_players, small_blind, big_blind, max_Bet)
	values('szerver2', 'OMAHA', 39, 6, 52.453, 104.906, 150.15);
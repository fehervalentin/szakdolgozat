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

insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, default_pot, max_bet)
	values('szerver1', 1, 10, 5, 100, 100);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, default_pot, max_bet)
	values('#PL_125Q', 2, 39, 5, 200, 200);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, default_pot, max_bet)
	values('Holdem fun', 1, 15, 5, 100, 100);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, default_pot, max_bet)
	values('Classic fun', 2, 20, 5, 20, 20);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, default_pot, max_bet)
	values('?^xW!', 2, 23, 3, 1, 3);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, default_pot, max_bet)
	values('űáűúüüéáűúőöüóÍ', 1, 33, 2, 10, 10);
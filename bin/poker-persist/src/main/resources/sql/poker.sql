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
	values('űáűúüüéáűúőöüóÍ', 1, 33, 2, 10);
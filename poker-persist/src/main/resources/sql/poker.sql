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
	values('szerver', 1, 5, 3, 100, 100);
	
insert into pokerdb.poker_tables (name, poker_type_id, max_time, max_players, default_pot, max_bet)
	values('szerver2', 2, 39, 5, 100, 100);
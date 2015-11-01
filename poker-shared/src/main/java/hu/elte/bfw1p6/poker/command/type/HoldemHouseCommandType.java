package hu.elte.bfw1p6.poker.command.type;

import hu.elte.bfw1p6.poker.command.CommandType;

public enum HoldemHouseCommandType implements CommandType<HoldemHouseCommandType> {
	BLIND, PLAYER, FLOP, TURN, RIVER, WINNER;
	
	public HoldemHouseCommandType getNext() {
		return values()[(ordinal()+1) % values().length];
	}

	@Override
	public HoldemHouseCommandType[] getValues() {
		return values();
	}

	@Override
	public HoldemHouseCommandType getActual() {
		return this;
	}
}

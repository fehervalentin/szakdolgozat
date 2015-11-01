package hu.elte.bfw1p6.poker.command.type;

import hu.elte.bfw1p6.poker.command.CommandType;

public enum ClassicHouseCommandType implements CommandType<ClassicHouseCommandType> {
	BLIND, DEAL, BET, CHANGE, BET2, WINNER;
	
	public ClassicHouseCommandType getNext() {
		return values()[(ordinal()+1) % values().length];
	}

	@Override
	public ClassicHouseCommandType[] getValues() {
		return values();
	}

	@Override
	public ClassicHouseCommandType getActual() {
		return this;
	}
}

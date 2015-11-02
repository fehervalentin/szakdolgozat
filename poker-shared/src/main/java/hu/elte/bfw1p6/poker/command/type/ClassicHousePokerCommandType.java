package hu.elte.bfw1p6.poker.command.type;

import hu.elte.bfw1p6.poker.command.type.api.HousePokerCommandType;

public enum ClassicHousePokerCommandType implements HousePokerCommandType<ClassicHousePokerCommandType> {
	BLIND, DEAL, BET, CHANGE, BET2, WINNER;
	
	@Override
	public ClassicHousePokerCommandType getNext() {
		return values()[(ordinal()+1) % values().length];
	}

	@Override
	public ClassicHousePokerCommandType[] getValues() {
		return values();
	}

	@Override
	public ClassicHousePokerCommandType getActual() {
		return this;
	}

	@Override
	public ClassicHousePokerCommandType getLastValue() {
		return values()[values().length - 1];
	}
}

package hu.elte.bfw1p6.poker.command.type;

import hu.elte.bfw1p6.poker.command.type.api.HousePokerCommandType;

public enum HoldemHousePokerCommandType implements HousePokerCommandType<HoldemHousePokerCommandType> {
	BLIND, PLAYER, FLOP, TURN, RIVER, WINNER;
	
	@Override
	public HoldemHousePokerCommandType getNext() {
		return values()[(ordinal()+1) % values().length];
	}

	@Override
	public HoldemHousePokerCommandType[] getValues() {
		return values();
	}

	@Override
	public HoldemHousePokerCommandType getActual() {
		return this;
	}

	@Override
	public HoldemHousePokerCommandType getLastValue() {
		return values()[values().length - 1];
	}
}

package hu.elte.bfw1p6.poker.command.type;

import hu.elte.bfw1p6.poker.command.type.api.PlayerPokerCommandType;

public enum ClassicHousePokerCommandType implements PlayerPokerCommandType<ClassicHousePokerCommandType> {
	BLIND, DEAL, BET, CHANGE, BET2, WINNER;
	
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
}

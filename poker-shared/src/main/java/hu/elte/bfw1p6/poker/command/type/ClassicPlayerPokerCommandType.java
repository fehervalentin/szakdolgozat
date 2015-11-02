package hu.elte.bfw1p6.poker.command.type;

import hu.elte.bfw1p6.poker.command.type.api.PlayerPokerCommandType;

public enum ClassicPlayerPokerCommandType implements PlayerPokerCommandType<ClassicPlayerPokerCommandType> {
	BLIND, CALL, CHECK, FOLD, RAISE, QUIT, CHANGE;

	@Override
	public ClassicPlayerPokerCommandType getNext() {
		return values()[(ordinal()+1) % values().length];
	}

	@Override
	public ClassicPlayerPokerCommandType[] getValues() {
		return values();
	}

	@Override
	public ClassicPlayerPokerCommandType getActual() {
		return this;
	}
}

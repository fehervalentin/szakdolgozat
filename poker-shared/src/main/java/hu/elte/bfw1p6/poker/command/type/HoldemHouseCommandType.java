package hu.elte.bfw1p6.poker.command.type;

public enum HoldemHouseCommandType {
	BLIND, PLAYER, FLOP, TURN, RIVER, WINNER;
	
	public HoldemHouseCommandType getNext() {
		return values()[(ordinal()+1) % values().length];
	}
}

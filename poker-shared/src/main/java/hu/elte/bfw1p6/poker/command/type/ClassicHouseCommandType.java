package hu.elte.bfw1p6.poker.command.type;

public enum ClassicHouseCommandType {
	BLIND, PLAYER, BET, CHANGE, BET2, WINNER;
	
	public ClassicHouseCommandType getNext() {
		return values()[(ordinal()+1) % values().length];
	}
}

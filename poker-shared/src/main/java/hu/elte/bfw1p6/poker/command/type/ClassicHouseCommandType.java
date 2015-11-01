package hu.elte.bfw1p6.poker.command.type;

public enum ClassicHouseCommandType {
	BLIND, DEAL, BET, CHANGE, BET2, WINNER;
	
	public ClassicHouseCommandType getNext() {
		return values()[(ordinal()+1) % values().length];
	}
}

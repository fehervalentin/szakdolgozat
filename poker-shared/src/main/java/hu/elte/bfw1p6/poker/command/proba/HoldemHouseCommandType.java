package hu.elte.bfw1p6.poker.command.proba;

public class HoldemHouseCommandType extends HouseCommandType {
	
	private static final String[] COMMAND_TYPES = {"BLIND", "DEAL", "FLOP", "TURN", "RIVER", "WINNER"}; 

	public HoldemHouseCommandType() {
		super(COMMAND_TYPES);
	}
}

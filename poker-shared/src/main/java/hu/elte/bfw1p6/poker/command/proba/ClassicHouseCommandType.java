package hu.elte.bfw1p6.poker.command.proba;

public class ClassicHouseCommandType extends HouseCommandType {

	private static final String[] COMMAND_TYPES = {"BLIND", "DEAL", "CHANGE", "DEAL2", "WINNER"}; 

	public ClassicHouseCommandType(String[] commandTypes) {
		super(COMMAND_TYPES);
	}
}

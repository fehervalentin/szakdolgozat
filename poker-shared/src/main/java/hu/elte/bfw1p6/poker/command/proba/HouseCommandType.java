package hu.elte.bfw1p6.poker.command.proba;

import java.util.Arrays;

public class HouseCommandType {

	private int pointer;
	
	protected String[] commandTypes;
	
	public HouseCommandType(String[] commandTypes) {
		resetPointer();
		this.commandTypes = commandTypes;
	}
	
	public void resetPointer() {
		pointer = 0;
	}
	
	public void incrementPointer() {
		++pointer;
		pointer %= commandTypes.length;
	}
	
	public String getActualCommandType() {
		return commandTypes[pointer];
	}
	
	public void setType(String type) {
		pointer = Arrays.asList(commandTypes).indexOf(type);
		if (pointer < 0) {
			throw new IllegalArgumentException();
		}
	}
}

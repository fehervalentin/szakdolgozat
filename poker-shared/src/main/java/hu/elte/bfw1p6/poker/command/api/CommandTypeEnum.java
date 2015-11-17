package hu.elte.bfw1p6.poker.command.api;

public interface CommandTypeEnum<T extends CommandTypeEnum<T>> {

	T getActual();
	
	T getNext();
	
	T[] getValues();
	
	T getLastValue();
}

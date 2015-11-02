package hu.elte.bfw1p6.poker.command.type.api;

public interface PokerCommandType<T extends PokerCommandType<T>> {
	
	T getActual();
	
	T getNext();
	
	T[] getValues();
}

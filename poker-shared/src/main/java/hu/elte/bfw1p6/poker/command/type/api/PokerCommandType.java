package hu.elte.bfw1p6.poker.command.type.api;

public interface PokerCommandType<T extends PokerCommandType<T>> {
	
	public T getActual();
	
	public T getNext();
	
	public T[] getValues();
}

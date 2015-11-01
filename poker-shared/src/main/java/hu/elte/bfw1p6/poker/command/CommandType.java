package hu.elte.bfw1p6.poker.command;

public interface CommandType<T extends CommandType<T>> {
	
	public T getActual();
	
	public T getNext();
	
	public T[] getValues();
}

package hu.elte.bfw1p6.poker.command.type.api;

public interface HousePokerCommandType<T extends Enum<T>> extends PokerCommandType<T> {
	
	T getLastValue();
}

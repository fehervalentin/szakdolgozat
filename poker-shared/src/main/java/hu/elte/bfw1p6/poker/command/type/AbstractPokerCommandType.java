package hu.elte.bfw1p6.poker.command.type;

import hu.elte.bfw1p6.poker.command.type.api.PokerCommandType;

public class AbstractPokerCommandType<T extends AbstractPokerCommandType<T>> implements PokerCommandType {
	
	private T t;
	
//	public AbstractPokerCommandType() {
//		t = (T) new AbstractPokerCommandType<>();
//	}
	
	public void setAkarmi(T t) {
		this.t = t;
	}

//	@Override
//	public T getActual() {
//		return t.getActual();
//	}
//
//	@Override
//	public T getNext() {
//		return t.getNext();
//	}
//
//	@Override
//	public T[] getValues() {
//		return t.getValues();
//	}
//
//	@Override
//	public T getLastValue() {
//		return t.getLastValue();
//	}

}

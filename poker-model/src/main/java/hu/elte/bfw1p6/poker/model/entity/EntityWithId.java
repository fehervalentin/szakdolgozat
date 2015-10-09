package hu.elte.bfw1p6.poker.model.entity;

public interface EntityWithId {
	public Integer getId();
    public Object get(int columnIndex);
    public void set(int columnIndex, Object value);
}

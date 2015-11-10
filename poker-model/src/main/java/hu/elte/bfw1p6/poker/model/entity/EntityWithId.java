package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;

/**
 * A póker entitásokat összefogó interface.
 * @author feher
 *
 */
public interface EntityWithId extends Serializable {
	
	Integer getId();
	
    Object get(int columnIndex);
    
    void set(int columnIndex, Object value);
}

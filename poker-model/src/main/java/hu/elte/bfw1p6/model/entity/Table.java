package hu.elte.bfw1p6.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Table {
	
	@Id
	@GeneratedValue()
	private int id;
	private Mode mode;
}

package hu.elte.bfw1p6.poker.model.entity;

import java.math.BigDecimal;

/**
 * A póker asztalokat reprezentáló entitás.
 * @author feher
 *
 */
public class PokerTable implements EntityWithId {

	private static final long serialVersionUID = -4600266588149030979L;
	
	public static final String TABLE_NAME = "poker_tables";
	
	private Integer id;
	private String name;
	private PokerType pokerType;
	private Integer maxTime;
	private Integer maxPlayers;
	private BigDecimal bigBlind;

	public PokerTable(String name, Integer maxTime, Integer maxPlayers, BigDecimal bigBlind, PokerType pokerType) {
		this.name = name;
		this.maxTime = maxTime;
		this.maxPlayers = maxPlayers;
		this.bigBlind = bigBlind;
		this.pokerType = pokerType;
	}

	public PokerTable() {
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public Integer getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public BigDecimal getBigBlind() {
		return bigBlind;
	}

	public void setBigBlind(BigDecimal bigBlind) {
		this.bigBlind = bigBlind;
	}

	public PokerType getPokerType() {
		return pokerType;
	}

	public void setPokerType(PokerType pokerType) {
		this.pokerType = pokerType;
	}

	@Override
	public Object get(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return name;
		case 1:
			return pokerType.ordinal() + 1;
		case 2:
			return maxTime;
		case 3:
			return maxPlayers;
		case 4:
			return bigBlind;
		default:
			return null;
		}
	}

	@Override
	public void set(int columnIndex, Object value) {
		switch (columnIndex) {
		case 0: {
			setName((String) value);
			break;
		}
		case 1: {
			setPokerType(PokerType.values()[((Integer) value) - 1]);
			break;
		}
		case 2: {
			setMaxTime((Integer) value);
			break;
		}
		case 3: {
			setMaxPlayers((Integer) value);
			break;
		}
		case 4: {
			setBigBlind((BigDecimal) value);
			break;
		}
		}
	}
}
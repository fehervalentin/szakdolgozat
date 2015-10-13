package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PokerTable implements EntityWithId, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Persistence fields
	 */
	private Integer id;
	private String name;
	private PokerType pokerType;
	private Integer maxTime;
	private Integer maxPlayers;
	private BigDecimal defaultPot;
	private BigDecimal maxBet;

	private List<UUID> clients;

	public PokerTable(String name, Integer maxTime, Integer maxPlayers, BigDecimal maxBet, BigDecimal defaultPot, PokerType pokerType) {
		this.name = name;
		this.maxTime = maxTime;
		this.maxPlayers = maxPlayers;
		this.maxBet = maxBet;
		this.defaultPot = defaultPot;
		this.pokerType = pokerType;
	}

	public PokerTable() {
		clients = new ArrayList<>();
	}

	public void connect(UUID uuid) {

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

	public BigDecimal getMaxBet() {
		return maxBet;
	}

	public void setMaxBet(BigDecimal maxBet) {
		this.maxBet = maxBet;
	}

	public BigDecimal getDefaultPot() {
		return defaultPot;
	}

	public void setDefaultPot(BigDecimal defaultPot) {
		this.defaultPot = defaultPot;
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
			return defaultPot;
		case 5:
			return maxBet;
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
			setDefaultPot((BigDecimal) value);
			break;
		}
		case 5: {
			setMaxBet((BigDecimal) value);
			break;
		}
		}
	}
}

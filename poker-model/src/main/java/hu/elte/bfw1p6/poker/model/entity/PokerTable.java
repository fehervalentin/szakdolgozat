package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class PokerTable implements EntityWithId, Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private PokerType pokerType;
	private Integer maxTime;
	private Integer maxPlayers;
	private BigDecimal smallBlind;
	private BigDecimal bigBlind;
	private BigDecimal maxBet;

	public PokerTable(String name, Integer maxTime, Integer maxPlayers, BigDecimal maxBet, BigDecimal smallBlind, BigDecimal bigBlind, PokerType pokerType) {
		this.name = name;
		this.maxTime = maxTime;
		this.maxPlayers = maxPlayers;
		this.maxBet = maxBet;
		this.smallBlind = smallBlind;
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

	public BigDecimal getMaxBet() {
		return maxBet;
	}

	public void setMaxBet(BigDecimal maxBet) {
		this.maxBet = maxBet;
	}

	public BigDecimal getSmallBlind() {
		return smallBlind;
	}

	public void setSmallBlind(BigDecimal smallBlind) {
		this.smallBlind = smallBlind;
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
			return pokerType;
		case 2:
			return maxTime;
		case 3:
			return maxPlayers;
		case 4:
			return smallBlind;
		case 5:
			return bigBlind;
		case 6:
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
            setPokerType(PokerType.valueOf((String) value));
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
            setSmallBlind((BigDecimal) value);
            break;
        }
        case 5: {
        	setBigBlind((BigDecimal) value);
            break;
        }
        case 6: {
            setMaxBet((BigDecimal) value);
            break;
        }
    }

	}
}

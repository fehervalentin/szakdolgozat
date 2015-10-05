package hu.elte.bfw1p6.poker.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class PTable implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private int id;
	
	@NotNull
	@Size.List({
		@Size(min=3, message="A szerver neve legalább {min} karakterből állhat!"),
		@Size(max=30, message="A szerver neve maximum {max} karakterből állhat!")
	})
	@Column(name = "name")
	private String name;
	
	@NotNull
	@Min(value = 5, message="A körönkénti gondolkodási idő minimum {value} másodperc!")
	@Max(value = 40, message="A körönkénti gondolkodási idő maximum {value} másodperc!")
	@Column(name = "max_time")
	private int maxTime;
	
	@NotNull
	@Min(value = 2)
	@Max(value = 6)
	@Column(name = "max_players")
	private int maxPlayers;
	
	@NotNull
	@Column(name = "max_Bet")
	private BigDecimal maxBet;
	
	@NotNull
	@Column(name = "small_blind")
	private BigDecimal smallBlind;
	
	@NotNull
	@Column(name = "big_blind")
	private BigDecimal bigBlind;

	@Enumerated(EnumType.STRING)
	private Type type;
	
	public PTable(String name, int maxTime, int maxPlayers, BigDecimal maxBet, BigDecimal smallBlind, BigDecimal bigBlind) {
		this.name = name;
		this.maxTime = maxTime;
		this.maxPlayers = maxPlayers;
		this.maxBet = maxBet;
		this.smallBlind = smallBlind;
		this.bigBlind = bigBlind;
		//this.type = type;
	}

	public int getId() {
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

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public int getMaxPlayers() {
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}

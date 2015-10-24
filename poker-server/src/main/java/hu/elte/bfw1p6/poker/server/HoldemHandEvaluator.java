package hu.elte.bfw1p6.poker.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.IPlayer;
import com.cantero.games.poker.texasholdem.RankingUtil;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.model.entity.PokerPlayer;

public class HoldemHandEvaluator {
	
	private static HoldemHandEvaluator instance = null;
	
	private HoldemHandEvaluator() {}
	
	public synchronized static HoldemHandEvaluator getInstance() {
		if (instance == null) {
			instance = new HoldemHandEvaluator();
		}
		return instance;
	}
	
	private void checkPlayersRanking(List<RemoteObserver> clients, List<Card> houseCards, List<PokerPlayer> players, HashMap<RemoteObserver, List<Card>> playersCards) {
		for (RemoteObserver client : clients) {
			PokerPlayer player = createPlayer(client, playersCards);
			players.add(player);
			RankingUtil.checkRanking(player, houseCards);
		}
	}
	
	private PokerPlayer createPlayer(RemoteObserver client, HashMap<RemoteObserver, List<Card>> playersCards) {
		PokerPlayer player = new PokerPlayer();
		List<Card> cards = playersCards.get(client);
		player.setCards(cards.toArray(new Card[0]));
		return player;
	}
	
	public List<IPlayer> getWinner(List<RemoteObserver> clients, List<Card> houseCards, List<PokerPlayer> players, HashMap<RemoteObserver, List<Card>> playersCards) throws RemoteException {
		checkPlayersRanking(clients, houseCards, players, playersCards);
		List<IPlayer> winnerList = new ArrayList<IPlayer>();
		IPlayer winner = players.get(0);
		Integer winnerRank = RankingUtil.getRankingToInt(winner);
		winnerList.add(winner);
		for (int i = 1; i < clients.size(); i++) {
			IPlayer player = players.get(i);
			Integer playerRank = RankingUtil.getRankingToInt(player);
			//Draw game
			if (winnerRank == playerRank) {
				IPlayer highHandPlayer = checkHighSequence(winner, player);
				//Draw checkHighSequence
				if (highHandPlayer == null) {
					highHandPlayer = checkHighCardWinner(winner, player);
				}
				//Not draw in checkHighSequence or checkHighCardWinner
				if (highHandPlayer != null && !winner.equals(highHandPlayer)) {
					winner = highHandPlayer;
					winnerList.clear();
					winnerList.add(winner);
				} else if (highHandPlayer == null) {
					//Draw in checkHighSequence and checkHighCardWinner
					winnerList.add(winner);
				}
			} else if (winnerRank < playerRank) {
				winner = player;
				winnerList.clear();
				winnerList.add(winner);
			}
			winnerRank = RankingUtil.getRankingToInt(winner);
		}

		return winnerList;
	}
	
	private IPlayer checkHighSequence(IPlayer player1, IPlayer player2) {
		Integer player1Rank = sumRankingList(player1);
		Integer player2Rank = sumRankingList(player2);
		if (player1Rank > player2Rank) {
			return player1;
		} else if (player1Rank < player2Rank) {
			return player2;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private IPlayer checkHighCardWinner(IPlayer player1, IPlayer player2) {
		IPlayer winner = compareHighCard(player1, player1.getHighCard(),
				player2, player2.getHighCard());
		if (winner == null) {
			Card player1Card = RankingUtil.getHighCard(player1,
					Collections.EMPTY_LIST);
			Card player2Card = RankingUtil.getHighCard(player2,
					Collections.EMPTY_LIST);
			winner = compareHighCard(player1, player1Card, player2, player2Card);
			if (winner != null) {
				player1.setHighCard(player1Card);
				player2.setHighCard(player2Card);
			} else if (winner == null) {
				player1Card = getSecondHighCard(player1, player1Card);
				player2Card = getSecondHighCard(player2, player2Card);
				winner = compareHighCard(player1, player1Card, player2,
						player2Card);
				if (winner != null) {
					player1.setHighCard(player1Card);
					player2.setHighCard(player2Card);
				}
			}
		}
		return winner;
	}
	
	private IPlayer compareHighCard(IPlayer player1, Card player1HighCard,
			IPlayer player2, Card player2HighCard) {
		if (player1HighCard.getRankToInt() > player2HighCard.getRankToInt()) {
			return player1;
		} else if (player1HighCard.getRankToInt() < player2HighCard
				.getRankToInt()) {
			return player2;
		}
		return null;
	}

	/*
	 * TODO This method must be moved to RankingUtil
	 */
	private Card getSecondHighCard(IPlayer player, Card card) {
		if (player.getCards()[0].equals(card)) {
			return player.getCards()[1];
		}
		return player.getCards()[0];
	}

	/*
	 * TODO This method must be moved to RankingUtil
	 */
	private Integer sumRankingList(IPlayer player) {
		Integer sum = 0;
		for (Card card : player.getRankingList()) {
			sum += card.getRankToInt();
		}
		return sum;
	}
}

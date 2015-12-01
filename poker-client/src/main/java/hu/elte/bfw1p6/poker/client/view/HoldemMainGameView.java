package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.defaultvalues.HoldemDefaultValues;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.model.entity.PokerType;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * A kliens játék közbeni holdem játékstílus megjelenítéséért felelős osztály.
 * @author feher
 *
 */
public class HoldemMainGameView extends AbstractMainGameView {

	/**
	 * A ház lapjai.
	 */
	private List<ImageView> houseCards;

	public HoldemMainGameView(AnchorPane mainGamePane) {
		super(mainGamePane, HoldemDefaultValues.getInstance());
		setHouseCards();
	}

	/**
	 * A ház lapjait állítja be.
	 */
	private void setHouseCards() {
		this.houseCards = new ArrayList<>();
		for (int i = 0; i < PokerType.HOLDEM.getCardsToHouse(); i++) {
			ImageView card = new ImageView();
			card.setLayoutX(defaultValues.MIDDLE_CARD_POINT[i * 2]);
			card.setLayoutY(defaultValues.MIDDLE_CARD_POINT[i * 2 + 1]);
			card.setVisible(false);
			houseCards.add(card);
			mainGamePane.getChildren().add(card);
		}
	}

	@Override
	public void hideHouseCards() {
		houseCards.forEach(card -> card.setVisible(false));
	}

	/**
	 * A szerver FLOP típusú utasítás küldött.
	 * @param holdemHouseCommand az utasítás
	 */
	public void receivedFlopHouseCommand(HoldemHouseCommand holdemHouseCommand) {
		revealCard(holdemHouseCommand.getCards(), 0);
		revealCard(holdemHouseCommand.getCards(), 1);
		revealCard(holdemHouseCommand.getCards(), 2);
		colorNextPlayer(holdemHouseCommand);
	}

	/**
	 * A szerver TURN típusú utasítás küldött.
	 * @param holdemHouseCommand az utasítás
	 */
	public void receivedTurnHouseCommand(HoldemHouseCommand holdemHouseCommand) {
		revealCard(holdemHouseCommand.getCards(), 3);
		colorNextPlayer(holdemHouseCommand);
	}

	/**
	 * A szerver RIVER típusú utasítás küldött.
	 * @param holdemHouseCommand az utasítás
	 */
	public void receivedRiverHouseCommand(HoldemHouseCommand holdemHouseCommand) {
		revealCard(holdemHouseCommand.getCards(), 4);
		colorNextPlayer(holdemHouseCommand);
	}

	/**
	 * Megjeleníti a ház i. kártyalapját.
	 * @param holdemHouseCommand az utasítás
	 * @param i a megjelenítendő kártya sorszáma
	 */
	private void revealCard(Card[] cards, int i) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Card card = null;
				if (i == 0 || i == 3 || i == 4) {
					card = cards[0];
				} else if (i == 1) {
					card = cards[1];
				} else if (i == 2) {
					card = cards[2];
				} else {
					throw new IllegalArgumentException();
				}
				houseCards.get(i).setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + mapCard(card) + defaultValues.PICTURE_EXTENSION));
				houseCards.get(i).setVisible(true);
			}
		});
	}
}
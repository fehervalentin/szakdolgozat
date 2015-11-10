package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.defaultvalues.HoldemDefaultValues;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * A kliens játék közbeni holdem játékstílus megjelenítéséért felelős osztály.
 * @author feher
 *
 */
public class HoldemMainView extends AbstractMainView {

	/**
	 * A ház lapjai.
	 */
	private List<ImageView> houseCards;

	public HoldemMainView(AnchorPane mainGamePane) {
		super(mainGamePane, HoldemDefaultValues.getInstance());
		setHouseCards();
	}

	/**
	 * A ház lapjait állítja be.
	 */
	private void setHouseCards() {
		int gap = 5;
		this.houseCards = new ArrayList<>();
		ImageView previous = new ImageView(new Image(defaultValues.DECK_IMAGE_URL));
		previous.setLayoutX(defaultValues.DECK_POINT[0] + 20);
		previous.setLayoutY(defaultValues.DECK_POINT[1]);
		for (int i = 0; i < defaultValues.PROFILE_COUNT; i++) {
			ImageView card = new ImageView();
			card.setLayoutX(previous.getLayoutX() + defaultValues.CARD_WIDTH + gap);
			card.setLayoutY(previous.getLayoutY());
			card.setVisible(false);
			houseCards.add(card);
			mainGamePane.getChildren().add(card);
			previous = card;
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
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(holdemHouseCommand);
				revealCard(holdemHouseCommand, 0);
				revealCard(holdemHouseCommand, 1);
				revealCard(holdemHouseCommand, 2);
			}
		});
	}

	/**
	 * A szerver TURN típusú utasítás küldött.
	 * @param holdemHouseCommand az utasítás
	 */
	public void receivedTurnHouseCommand(HoldemHouseCommand holdemHouseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(holdemHouseCommand);
				revealCard(holdemHouseCommand, 3);
			}
		});
	}

	/**
	 * A szerver RIVER típusú utasítás küldött.
	 * @param holdemHouseCommand az utasítás
	 */
	public void receivedRiverHouseCommand(HoldemHouseCommand holdemHouseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(holdemHouseCommand);
				revealCard(holdemHouseCommand, 4);
			}
		});
	}

	/**
	 * Megjeleníti az i. ház kártyalapját.
	 * @param holdemHouseCommand az utasítás
	 * @param i a megjelenítendő kártya sorszáma
	 */
	private void revealCard(HoldemHouseCommand holdemHouseCommand, int i) {
		Card[] cards = holdemHouseCommand.getCards();
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
		int value = mapCard(card);
		houseCards.get(i).setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + value + ".png"));
		houseCards.get(i).setVisible(true);
	}
}

package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.defaultvalues.HoldemDefaultValues;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class HoldemMainView extends AbstractMainView {

	/**
	 * A ház lapjai.
	 */
	private List<ImageView> houseCards;

	public HoldemMainView(AnchorPane mainGamePane) {
		super(mainGamePane, HoldemDefaultValues.getInstance());
		
		this.houseCards = new ArrayList<>();
		setHouseCards();
	}

	private void setHouseCards() {
		int gap = 5;
		ImageView previous = new ImageView(new Image(defaultValues.DECK_IMAGE_URL));
		previous.setLayoutX(defaultValues.DECK_POINT[0] + 20);
		previous.setLayoutY(defaultValues.DECK_POINT[1]);
		for (int i = 0; i < defaultValues.PROFILE_COUNT; i++) {
			ImageView card = new ImageView(new Image(defaultValues.CARD_IMAGE_PREFIX + "1.png"));
			card.setLayoutX(previous.getLayoutX() + defaultValues.CARD_WIDTH + gap);
			card.setLayoutY(previous.getLayoutY());
			card.setVisible(false);
			houseCards.add(card);
			mainGamePane.getChildren().add(card);
			previous = card;
		}
	}

	public void hideHouseCards() {
		for (int i = 0; i < houseCards.size(); i++) {
			houseCards.get(i).setVisible(false);
		}
	}

	public void flop(HoldemHouseCommand holemHouseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(holemHouseCommand);
//				addNextEffect(nextPlayer);
				//colorSmallBlind();
				revealCard(holemHouseCommand, 0);
				revealCard(holemHouseCommand, 1);
				revealCard(holemHouseCommand, 2);
			}
		});
	}

	public void turn(HoldemHouseCommand houseHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(houseHoldemCommand);
//				colorSmallBlind();
				revealCard(houseHoldemCommand, 3);
			}
		});
	}

	public void river(HoldemHouseCommand houseHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(houseHoldemCommand);
				revealCard(houseHoldemCommand, 4);
			}
		});
	}

	private void revealCard(HouseCommand houseCommand, int i) {
		Card[] cards = houseCommand.getCards();
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

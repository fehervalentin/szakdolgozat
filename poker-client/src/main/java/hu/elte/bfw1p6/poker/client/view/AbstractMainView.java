package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.CardSuitEnum;

import hu.elte.bfw1p6.poker.client.controller.AbstractDefaultValues;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public abstract class AbstractMainView {

	protected AbstractDefaultValues defaultValues;

	protected List<ImageView> myCards;
	protected List<ImageView> winnerCards;
	protected List<ImageView> profileImages;
	protected List<ImageView> opponentsCards;
	protected List<ImageView> opponentsCardSides;
	protected List<ImageView> chips;

	protected List<Label> userNameLabels;

	protected ImageView dealerButtonImageView;

	protected AnchorPane mainGamePane;

	protected int clientsCount;

	protected Random random;

	protected int DEALER_BUTTON_POSITION;

	protected int coloredPlayer = -1;

	protected int nextPlayer = -1;

	protected int youAreNth;

	public AbstractMainView(AnchorPane mainGamePane, AbstractDefaultValues defaultValues) {
		this.mainGamePane = mainGamePane;
		this.defaultValues = defaultValues;
		this.myCards = new ArrayList<>();
		this.winnerCards = new ArrayList<>();
		this.profileImages = new ArrayList<>();
		this.opponentsCards = new ArrayList<>();
		this.opponentsCardSides = new ArrayList<>();
		this.chips = new ArrayList<>();
		this.userNameLabels = new ArrayList<>();
		this.random = new Random();

		setDealerButton();
		setProfileImages();
		setDeck();
		setCards();
		setLabels();
		hideAllProfiles();
	}

	protected void setDealerButton() {
		dealerButtonImageView = new ImageView(new Image(defaultValues.DEALER_BUTTON_IMAGE_URL));
		dealerButtonImageView.setFitHeight(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setFitWidth(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setVisible(false);
		mainGamePane.getChildren().add(dealerButtonImageView);
	}

	protected void setProfileImages() {
		Image profileImage = new Image(defaultValues.PROFILE_IMAGE_URL);
		// kettesével kell menni (x,y) párok miatt
		for (int i = 0; i < defaultValues.PROFILE_COUNT * 2; i+=2) {
			ImageView iv = new ImageView(profileImage);
			iv.setLayoutX(defaultValues.PROFILE_POINTS[i]);
			iv.setLayoutY(defaultValues.PROFILE_POINTS[i+1]);
			iv.fitHeightProperty().set(defaultValues.PROFILE_SIZE);
			iv.fitWidthProperty().set(defaultValues.PROFILE_SIZE);
			profileImages.add(iv);
			mainGamePane.getChildren().add(iv);
		}
	}

	protected void setDeck() {
		ImageView deck = new ImageView(new Image(defaultValues.DECK_IMAGE_URL));
		deck.setLayoutX(defaultValues.DECK_POINT[0]);
		deck.setLayoutY(defaultValues.DECK_POINT[1]);
		mainGamePane.getChildren().add(deck);
	}

	protected void setCards() {
		// a saját kártyáim nem idemennek
		// a 0. helyen az első hely lapjai vannak
		// kettessével kell menni (x,y) párok miatt
		//opponents cards
		for (int i = 0; i < (defaultValues.PROFILE_COUNT) * 2; i+=2) {
			for (int j = 0; j < defaultValues.MY_CARDS_COUNT - 1; j++) {
				ImageView cardSide = new ImageView(new Image(defaultValues.CARD_SIDE_IMAGE_URL));
				cardSide.setLayoutX(defaultValues.CARD_B1FV_POINTS[i] + j * defaultValues.CARD_SIDE_WIDTH);
				cardSide.setLayoutY(defaultValues.CARD_B1FV_POINTS[i+1]);
				cardSide.fitHeightProperty().set(defaultValues.CARD_HEIGHT);
				cardSide.fitWidthProperty().set(defaultValues.CARD_SIDE_WIDTH);
				opponentsCardSides.add(cardSide);
				mainGamePane.getChildren().add(cardSide);
			}
			ImageView backCard = new ImageView(new Image(defaultValues.CARD_BACKFACE_IMAGE));
			backCard.setLayoutX(defaultValues.CARD_B1FV_POINTS[i] + (defaultValues.MY_CARDS_COUNT - 1) * defaultValues.CARD_SIDE_WIDTH);
			backCard.setLayoutY(defaultValues.CARD_B1FV_POINTS[i+1]);
			backCard.fitHeightProperty().set(defaultValues.CARD_HEIGHT);
			backCard.fitWidthProperty().set(defaultValues.CARD_WIDTH);
			opponentsCards.add(backCard);
			mainGamePane.getChildren().add(backCard);
		}
		
		//winner cards
		for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
			ImageView imageView = new ImageView();
			winnerCards.add(imageView);
			mainGamePane.getChildren().add(imageView);
		}
		
		//mycards
		for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
			ImageView myCard = new ImageView();
			int gap = 5;
			myCard.setLayoutX(defaultValues.MY_CARDS_POSITION[0]);
			myCard.setLayoutY(defaultValues.MY_CARDS_POSITION[1]);
			myCard.setLayoutX(defaultValues.MY_CARDS_POSITION[0] + i * defaultValues.CARD_WIDTH + gap);
			myCard.setLayoutY(defaultValues.MY_CARDS_POSITION[1]);

			myCards.add(myCard);
			mainGamePane.getChildren().add(myCard);
		}
	}

	protected void setLabels() {
		// kettesével kell menni (x,y) párok miatt
		for (int i = 0; i < defaultValues.PROFILE_COUNT * 2; i+=2) {
			Label label = new Label();
			label.setLayoutX(defaultValues.PROFILE_POINTS[i]);
			label.setLayoutY(defaultValues.PROFILE_POINTS[i+1]);
			//			iv.fitHeightProperty().set(defaultValues.PROFILE_SIZE);
			//			iv.fitWidthProperty().set(defaultValues.PROFILE_SIZE);
			userNameLabels.add(label);
			mainGamePane.getChildren().add(label);
		}
	}

	protected void setLabelUserNames(List<String> userNames) {
		for (int i = 0; i < userNames.size(); i++) {
			int value = ultimateFormula(i);
			userNameLabels.get(value).setText(userNames.get(i));
			userNameLabels.get(value).setVisible(true);
		}
	}

	protected int mapCard(Card card) {
		return 52 - (card.getRankToInt() * CardSuitEnum.values().length) - (CardSuitEnum.values().length - card.getSuit().ordinal() - 1);
	}

	protected void hideAllProfiles() {
		for (ImageView imageView : opponentsCards) {
			imageView.setVisible(false);
		}
		for (ImageView imageView : opponentsCardSides) {
			imageView.setVisible(false);
		}
		for (int i = 1; i < profileImages.size(); i++) {
			profileImages.get(i).setVisible(false);
			userNameLabels.get(i).setVisible(false);
		}
	}

	/**
	 * Kitörlöm a chipeket.
	 */
	protected void clearChips() {
		for (ImageView imageView : chips) {
			mainGamePane.getChildren().remove(imageView);
		}
		chips.clear();
	}

	protected void colorNextPlayer(PokerCommand pokerCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				nextPlayer = ultimateFormula(pokerCommand.getWhosOn());
				if (coloredPlayer >= 0) {
					profileImages.get(coloredPlayer).getStyleClass().remove("glow");
				}
				if (nextPlayer >= 0) {
					profileImages.get(nextPlayer).getStyleClass().add("glow");
				}
				coloredPlayer = nextPlayer;
				nextPlayer = pokerCommand.getWhosOn();
			}
		});
	}

	protected int ultimateFormula(int whosOn) {
		int value = (whosOn - youAreNth) % clientsCount;
		value += value < 0 ? clientsCount : 0;
		return value;
	}

	public void receivedBlindHouseCommand(HouseCommand houseCommand) {
		clientsCount = houseCommand.getPlayers();
		youAreNth = houseCommand.getNthPlayer();
		DEALER_BUTTON_POSITION = (clientsCount + houseCommand.getDealer() - youAreNth) % clientsCount;
		hideAllProfiles();
		Platform.runLater(
				new Runnable() {

					@Override
					public void run() {
						setLabelUserNames(houseCommand.getPlayersNames());
						resetOpacity();
						clearChips();
						for (int i = 0; i < houseCommand.getPlayers(); i++) {
							userNameLabels.get(i).setVisible(true);
							profileImages.get(i).setVisible(true);
							opponentsCards.get(i).setVisible(true);
							for (int j = i; j < defaultValues.MY_CARDS_COUNT - 1; j++) {
								opponentsCardSides.get(j).setVisible(true);
							}
						}
						// ugye el van csúszva, mert a profilképeknél én is szerepel, de a kártyáknál nem
						opponentsCards.get(houseCommand.getPlayers() - 1).setVisible(false);
//						opponentsCardSides.get(houseCommand.getPlayers() - 1).setVisible(false);


						dealerButtonImageView.setLayoutX(defaultValues.DEALER_BUTTON_POSITIONS[DEALER_BUTTON_POSITION * 2]);
						dealerButtonImageView.setLayoutY(defaultValues.DEALER_BUTTON_POSITIONS[DEALER_BUTTON_POSITION * 2 + 1]);
						dealerButtonImageView.setVisible(true);
					}
				});
	}

	public void receivedDealHouseCommand(HouseCommand houseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				hideHouseCards();
				loadMyCards(houseCommand);
				//KIKOVETKEZIK = (HOLVANADEALERGOMB + clientsCount + 1) % clientsCount;
				nextPlayer = ultimateFormula(houseCommand.getWhosOn());
				//				NEXT_PLAYER = (DEALER_BUTTON_POSITION + 3) % clientsCount;
				System.out.println("Dealer gomb helye: " + DEALER_BUTTON_POSITION);
				System.out.println("Hanyan vagyunk: " + clientsCount);
				System.out.println("Ki a következő játékos (ki lett beszinezve): " + nextPlayer);
				colorNextPlayer(houseCommand);
			}
		});
	}
	
	public void loadMyCards(HouseCommand houseCommand) {
		Card[] cards = houseCommand.getCards();
		for (int j = 0; j < defaultValues.MY_CARDS_COUNT; j++) {
			int value = mapCard(cards[j]);
			myCards.get(j).setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + value + ".png"));
		}
	}

	public void receivedRaisePlayerCommand(PlayerCommand playerCommand) {
		colorNextPlayer(playerCommand);
		addChip();
		addChip();
	}

	public void receivedBlindPlayerCommand(PlayerCommand playerCommand) {
		addChip();
	}

	public void receivedCallPlayerCommand(PlayerCommand playerCommand) {
		colorNextPlayer(playerCommand);
		addChip();
	}

	public void receivedCheckPlayerCommand(PlayerCommand playerCommand) {
		colorNextPlayer(playerCommand);
	}

	public void receivedFoldPlayerCommand(PlayerCommand playerCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				double opacity = 0.4;
				//				nextPlayer = ultimateFormula(playerHoldemCommand.getWhosOn());
				int whosQuit = ultimateFormula(playerCommand.getWhosQuit() - 1);
				System.out.println("NextPlayer foldban: " + whosQuit);
				if (whosQuit == 0) {
					for (ImageView imageView : myCards) {
						imageView.setOpacity(opacity);
					}
				} else {
					// ugye el van csúszva! (direkt!!!)
					opponentsCards.get(whosQuit).setOpacity(opacity);
					opponentsCardSides.get(whosQuit).setOpacity(opacity);
				}
				//				whosOut[NEXT_PLAYER] = true;
				/*if (youAreNth > playerHoldemCommand.getWhosQuit()) {
					--youAreNth;
				}*/
				colorNextPlayer(playerCommand);
			}
		});
	}

	public void winner(HouseCommand houseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Card[] cards = houseCommand.getCards();
				for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
					int value = mapCard(cards[i]);
					winnerCards.get(i).setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + value + ".png"));
				}
				int j = ultimateFormula(houseCommand.getWinner());// houseHoldemCommand.getWinner() + youWereNth;
				System.out.println("You are nth: " + youAreNth);
				//				int j = (houseHoldemCommand.getWinner() + youAreNth) % clientsCount;
				System.out.println("Ki nyer: " + houseCommand.getWinner());
				System.out.println("A j erteke mindenek elott: " + j);
				// én nyertem...
				if (j == youAreNth) {

				} else {
					--j;
					//					j+=youAreNth;
					//					j %= clientsCount;
					System.out.println("A j erteke: " + j);
					opponentsCards.get(j).setVisible(false);
					opponentsCardSides.get(j).setVisible(false);
					int gap = 5;
					for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
						winnerCards.get(i).setLayoutX(defaultValues.CARD_B1FV_POINTS[j * 2] + i * defaultValues.CARD_WIDTH);
						winnerCards.get(i).setLayoutY(defaultValues.CARD_B1FV_POINTS[j * 2 + 1]);
						winnerCards.get(i).setVisible(true);
					}

				}
			}
		});
	}

	protected void resetOpacity() {
		for (ImageView imageView : myCards) {
			imageView.setOpacity(1);
		}
		for (ImageView imageView : opponentsCards) {
			imageView.setOpacity(1);
		}
		for (ImageView imageView : opponentsCardSides) {
			imageView.setOpacity(1);
		}
		for (ImageView imageView : winnerCards) {
			imageView.setOpacity(0);
		}
	}

	public void fold() {
		youAreNth = -1;
	}

	/**
	 * Random helyezek el chipeket az asztalon.
	 */
	protected void addChip() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ImageView chip = new ImageView(new Image(defaultValues.CHIP_IMAGE_PREFIX + "black.png"));
				int max = defaultValues.CHIPS_POINT[0] + 20;
				int min = defaultValues.CHIPS_POINT[1] - 20;
				chip.setLayoutX(random.nextInt(max - min) + min);
				chip.setLayoutY(random.nextInt(max - min) + min);
				chip.setFitHeight(defaultValues.CHIPS_SIZE);
				chip.setFitWidth(defaultValues.CHIPS_SIZE);
				chips.add(chip);
				mainGamePane.getChildren().add(chip);
			}
		});
	}

	public void receivedQuitPlayerCommand(PlayerCommand playerCommand) {
		// TODO Auto-generated method stub

	}

	protected abstract void hideHouseCards();
}

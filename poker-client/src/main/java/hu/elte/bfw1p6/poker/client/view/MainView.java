package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.CardSuitEnum;

import hu.elte.bfw1p6.poker.client.controller.PokerHoldemDefaultValues;
import hu.elte.bfw1p6.poker.command.PokerCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemHouseCommand;
import hu.elte.bfw1p6.poker.command.holdem.HoldemPlayerCommand;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MainView {

	/**
	 * Mindenféle konstans érték.
	 */
	private PokerHoldemDefaultValues defaultValues;

	private List<ImageView> profileImages;
	private List<ImageView> opponentsCards;
	private List<ImageView> opponentsCardSides;
	private List<ImageView> houseCards;
	private List<ImageView> chips;
	private List<Label> userNameLabels;

	private ImageView myCard1;
	private ImageView myCard2;

	private ImageView dealerButtonImageView;

	private AnchorPane mainGamePane;

	private int clientsCount;

	private Random random;

	private int DEALER_BUTTON_POSITION;

	private ImageView winnerCard1;
	private ImageView winnerCard2;

	private int coloredPlayer = -1;
	private int nextPlayer = -1;

	private int youAreNth;

	private int youWereNth;

	public MainView(AnchorPane mainGamePane) {
		this.defaultValues = PokerHoldemDefaultValues.getInstance();
		this.mainGamePane = mainGamePane;
		this.profileImages = new ArrayList<>();
		this.opponentsCards = new ArrayList<>();
		this.opponentsCardSides = new ArrayList<>();
		this.houseCards = new ArrayList<>();
		this.chips = new ArrayList<>();
		this.userNameLabels = new ArrayList<>();
		this.random = new Random();
		
		this.winnerCard1 = new ImageView();
		this.winnerCard2 = new ImageView();
		mainGamePane.getChildren().add(winnerCard1);
		mainGamePane.getChildren().add(winnerCard2);

		this.myCard1 = new ImageView();
		this.myCard2 = new ImageView();
		mainGamePane.getChildren().addAll(myCard1, myCard2);

		int gap = 5;
		myCard1.setLayoutX(defaultValues.MY_CARDS_POSITION[0]);
		myCard1.setLayoutY(defaultValues.MY_CARDS_POSITION[1]);
		myCard2.setLayoutX(defaultValues.MY_CARDS_POSITION[0] + defaultValues.CARD_WIDTH + gap);
		myCard2.setLayoutY(defaultValues.MY_CARDS_POSITION[1]);

		setDealerButton();
		setProfileImages();
		setDeck();
		setCards();
		setHouseCards();
		setLabels();
		hideAllProfiles();
	}

	private void setDealerButton() {
		dealerButtonImageView = new ImageView(new Image(defaultValues.DEALER_BUTTON_IMAGE_URL));
		dealerButtonImageView.setFitHeight(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setFitWidth(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setVisible(false);
		mainGamePane.getChildren().add(dealerButtonImageView);
	}

	private void setLabelUserNames(List<String> userNames) {
		for (int i = 0; i < userNames.size(); i++) {
			String username = userNames.get(i);
			int value = (i - youAreNth) % clientsCount;
			if (value < 0) {
				value += clientsCount;
			}
			userNameLabels.get(value).setVisible(true);
			userNameLabels.get(value).setText(username);
		}
	}

	private void setProfileImages() {
		// a saját profilképem is idemegy
		// és mi van ha mindenkinek saját profile képet akarok betölteni...?
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

	private void setLabels() {
		// kettesével kell menni (x,y) párok miatt
		for (int i = 0; i < defaultValues.PROFILE_COUNT * 2; i+=2) {
			Label label = new Label();
			userNameLabels.add(label);
			label.setLayoutX(defaultValues.PROFILE_POINTS[i]);
			label.setLayoutY(defaultValues.PROFILE_POINTS[i+1]);
			//			iv.fitHeightProperty().set(defaultValues.PROFILE_SIZE);
			//			iv.fitWidthProperty().set(defaultValues.PROFILE_SIZE);
			mainGamePane.getChildren().add(label);
		}
	}

	private void setCards() {
		// a saját kártyáim nem idemennek
		// a 0. helyen az első hely lapjai vannak
		// kettessével kell menni (x,y) párok miatt
		for (int i = 0; i < defaultValues.PROFILE_COUNT * 2 - 2; i+=2) {
			ImageView card = new ImageView(new Image(defaultValues.CARD_BACKFACE_IMAGE));
			card.setLayoutX(defaultValues.CARD_B1FV_POINTS[i]);
			card.setLayoutY(defaultValues.CARD_B1FV_POINTS[i+1]);
			card.fitHeightProperty().set(defaultValues.CARD_HEIGHT);
			card.fitWidthProperty().set(defaultValues.CARD_WIDTH);

			ImageView cardSide = new ImageView(new Image(defaultValues.CARD_SIDE_IMAGE_URL));
			cardSide.setLayoutX(defaultValues.CARD_B1FV_POINTS[i] - defaultValues.CARD_SIDE_WIDTH);
			cardSide.setLayoutY(defaultValues.CARD_B1FV_POINTS[i+1]);
			cardSide.fitHeightProperty().set(defaultValues.CARD_HEIGHT);
			cardSide.fitWidthProperty().set(defaultValues.CARD_SIDE_WIDTH);

			opponentsCards.add(card);
			opponentsCardSides.add(cardSide);
			mainGamePane.getChildren().add(card);
			mainGamePane.getChildren().add(cardSide);
		}
	}

	private void setDeck() {
		ImageView deck = new ImageView(new Image(defaultValues.DECK_IMAGE_URL));
		deck.setLayoutX(defaultValues.DECK_POINT[0]);
		deck.setLayoutY(defaultValues.DECK_POINT[1]);
		mainGamePane.getChildren().add(deck);
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

	private int mapCard(Card card) {
		return 52 - (card.getRankToInt() * CardSuitEnum.values().length) - (CardSuitEnum.values().length - card.getSuit().ordinal() - 1);
	}

	private void hideAllProfiles() {
		for (int i = 0; i < opponentsCards.size(); i++) {
			profileImages.get(i).setVisible(false);
			opponentsCards.get(i).setVisible(false);
			opponentsCardSides.get(i).setVisible(false);
			userNameLabels.get(i).setVisible(false);
		}
		// el van csúszva: a 0. én vagyok, az utolsót nem érinti a ciklus
		profileImages.get(0).setVisible(true);
		profileImages.get(opponentsCards.size()).setVisible(false);
		userNameLabels.get(0).setVisible(true);
	}

	public void hideHouseCards() {
		for (int i = 0; i < houseCards.size(); i++) {
			houseCards.get(i).setVisible(false);
		}
	}

	private void resetOpacity() {
		myCard1.setOpacity(1);
		myCard2.setOpacity(1);
		for (int i = 0; i < opponentsCards.size(); i++) {
			opponentsCards.get(i).setOpacity(1);
			opponentsCardSides.get(i).setOpacity(1);
		}
	}

	public void receivedBlindHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		hideAllProfiles();
		clientsCount = houseHoldemCommand.getPlayers();
		youAreNth = houseHoldemCommand.getNthPlayer();
		DEALER_BUTTON_POSITION = (clientsCount + houseHoldemCommand.getDealer() - youAreNth) % clientsCount;
		Platform.runLater(
				new Runnable() {

					@Override
					public void run() {
						winnerCard1.setVisible(false);
						winnerCard2.setVisible(false);
						setLabelUserNames(houseHoldemCommand.getPlayersNames());
						resetOpacity();
						clearChips();
						hideHouseCards();
						for (int i = 0; i < houseHoldemCommand.getPlayers(); i++) {
							userNameLabels.get(i).setVisible(true);
							profileImages.get(i).setVisible(true);
							opponentsCards.get(i).setVisible(true);
							opponentsCardSides.get(i).setVisible(true);
						}
						// ugye el van csúszva, mert a profilképeknél én is szerepel, de a kártyáknál nem
						opponentsCards.get(houseHoldemCommand.getPlayers() - 1).setVisible(false);
						opponentsCardSides.get(houseHoldemCommand.getPlayers() - 1).setVisible(false);


						dealerButtonImageView.setLayoutX(defaultValues.DEALER_BUTTON_POSITIONS[DEALER_BUTTON_POSITION * 2]);
						dealerButtonImageView.setLayoutY(defaultValues.DEALER_BUTTON_POSITIONS[DEALER_BUTTON_POSITION * 2 + 1]);
						dealerButtonImageView.setVisible(true);
					}
				});
	}

	public void receivedPlayerHouseCommand(HoldemHouseCommand houseHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Card[] cards = houseHoldemCommand.getCards();
				int value = mapCard(cards[0]);
				int value2 = mapCard(cards[1]);
				myCard1.setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + value + ".png"));
				myCard2.setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + value2 + ".png"));

				//				KIKOVETKEZIK = (HOLVANADEALERGOMB + clientsCount + 1) % clientsCount;
				// a dealertől balra ülő harmadik játékos kezdi a preflopot
				nextPlayer = ultimateFormula(houseHoldemCommand.getWhosOn());
//				NEXT_PLAYER = (DEALER_BUTTON_POSITION + 3) % clientsCount;
				System.out.println("Dealer gomb helye: " + DEALER_BUTTON_POSITION);
				System.out.println("Hanyan vagyunk: " + clientsCount);
				System.out.println("Ki a következő játékos (ki lett beszinezve): " + nextPlayer);
				colorNextPlayer(houseHoldemCommand);
			}
		});
	}
	
	private int ultimateFormula(int whosOn) {
		int value = (whosOn - youAreNth) % clientsCount;
		if (value < 0) {
			value += clientsCount;
		}
		return value;
	}

	public void flop(HoldemHouseCommand houseHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(houseHoldemCommand);
//				addNextEffect(nextPlayer);
				//colorSmallBlind();
				revealCard(houseHoldemCommand, 0);
				revealCard(houseHoldemCommand, 1);
				revealCard(houseHoldemCommand, 2);
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

	public void winner(HoldemHouseCommand houseHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Card[] cards = houseHoldemCommand.getCards();
				int value = mapCard(cards[0]);
				int value2 = mapCard(cards[1]);
				winnerCard1.setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + value + ".png"));
				winnerCard2.setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + value2 + ".png"));
				int j = ultimateFormula(houseHoldemCommand.getWinner() - 1);// houseHoldemCommand.getWinner() + youWereNth;
				System.out.println("You are nth: " + youAreNth);
//				int j = (houseHoldemCommand.getWinner() + youAreNth) % clientsCount;
				System.out.println("Ki nyer: " + houseHoldemCommand.getWinner());
				System.out.println("A j erteke mindenek elott: " + j);
				// én nyertem...
				if (j == youAreNth) {
					
				} else {
//					--j;
//					j+=youAreNth;
//					j %= clientsCount;
					System.out.println("A j erteke: " + j);
					opponentsCards.get(j).setVisible(false);
					opponentsCardSides.get(j).setVisible(false);
					int gap = 5;
					winnerCard1.setLayoutX(defaultValues.CARD_B1FV_POINTS[j * 2]);
					winnerCard1.setLayoutY(defaultValues.CARD_B1FV_POINTS[j * 2 + 1]);
					winnerCard2.setLayoutX(defaultValues.CARD_B1FV_POINTS[j * 2] + defaultValues.CARD_WIDTH + gap);
					winnerCard2.setLayoutY(defaultValues.CARD_B1FV_POINTS[j * 2 + 1]);
					
					winnerCard1.setVisible(true);
					winnerCard2.setVisible(true);
				}
			}
		});
	}

	private void revealCard(HoldemHouseCommand houseHoldemCommand, int i) {
		Card[] cards = houseHoldemCommand.getCards();
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

	public void receivedBlindPlayerCommand(HoldemPlayerCommand playerHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				addChip();
			}
		});
	}

	public void receivedCallPlayerCommand(HoldemPlayerCommand playerHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(playerHoldemCommand);
				addChip();
			}
		});

	}

	public void receivedCheckPlayerCommand(HoldemPlayerCommand playerHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(playerHoldemCommand);
			}
		});
	}

	public void receivedFoldPlayerCommand(HoldemPlayerCommand playerHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				double opacity = 0.4;
//				nextPlayer = ultimateFormula(playerHoldemCommand.getWhosOn());
				int whosQuit = ultimateFormula(playerHoldemCommand.getWhosQuit() - 1);
				System.out.println("NextPlayer foldban: " + whosQuit);
				if (whosQuit == 0) {
					myCard1.setOpacity(opacity);
					myCard2.setOpacity(opacity);
				} else {
					// ugye el van csúszva! (direkt!!!)
					opponentsCards.get(whosQuit).setOpacity(opacity);
					opponentsCardSides.get(whosQuit).setOpacity(opacity);
				}
//				whosOut[NEXT_PLAYER] = true;
				/*if (youAreNth > playerHoldemCommand.getWhosQuit()) {
					--youAreNth;
				}*/
				colorNextPlayer(playerHoldemCommand);
			}
		});
	}

	public void receivedRaisePlayerCommand(HoldemPlayerCommand playerHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(playerHoldemCommand);
				addChip();
				addChip();
			}
		});
	}

	public void receivedQuitPlayerCommand(HoldemPlayerCommand playerHoldemCommand) {
		// TODO Auto-generated method stub

	}
	
	private void colorNextPlayer(PokerCommand pokerCommand) {
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

	/**
	 * Random helyezek el chipeket az asztalon.
	 */
	private void addChip() {
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

	/**
	 * Kitörlöm a chipeket.
	 */
	private void clearChips() {
		for (ImageView imageView : chips) {
			mainGamePane.getChildren().remove(imageView);
		}
		chips.clear();
	}

	public void fold() {
		youWereNth = youAreNth;
		youAreNth = -1;
	}
}

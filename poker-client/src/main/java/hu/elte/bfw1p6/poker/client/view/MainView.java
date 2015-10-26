package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.CardSuitEnum;

import hu.elte.bfw1p6.poker.client.controller.PokerHoldemDefaultValues;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MainView {

	private final String IMAGE_PREFIX = "/images/cards/";
	private final String CHIP_PREFIX = "/images/chips/";

	private PokerHoldemDefaultValues defaultValues;

	private List<ImageView> profileImages;
	private List<ImageView> opponentsCards;
	private List<ImageView> opponentsCardSides;
	private List<ImageView> houseCards;
	private List<ImageView> chips;

	private ImageView myCard1;
	private ImageView myCard2;

	private ImageView dealerButtonImageView;

	private AnchorPane mainGamePane;

	private int youAreNth;
	private int dealer;
	private int clientsCount;

	private Random random;

	private int HOLVANADEALERGOMB;

	private int KIKOVETKEZIK = -1;

	public MainView(AnchorPane mainGamePane) {
		this.defaultValues = PokerHoldemDefaultValues.getInstance();
		this.mainGamePane = mainGamePane;
		this.profileImages = new ArrayList<>();
		this.opponentsCards = new ArrayList<>();
		this.opponentsCardSides = new ArrayList<>();
		this.houseCards = new ArrayList<>();
		this.chips = new ArrayList<>();
		this.random = new Random();

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
		hideAllProfiles();
	}

	private void setDealerButton() {
		dealerButtonImageView = new ImageView(new Image(defaultValues.DEALER_BUTTON_IMAGE_URL));
		dealerButtonImageView.setFitHeight(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setFitWidth(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setVisible(false);
		mainGamePane.getChildren().add(dealerButtonImageView);
	}

	private void setProfileImages() {
		// a saját profilképem is idemegy
		for (int i = 0; i < defaultValues.PROFILE_COUNT * 2; i+=2) {
			ImageView iv = new ImageView(new Image(defaultValues.PROFILE_IMAGE_URL));
			iv.setLayoutX(defaultValues.PROFILE_POINTS[i]);
			iv.setLayoutY(defaultValues.PROFILE_POINTS[i+1]);
			iv.fitHeightProperty().set(defaultValues.PROFILE_SIZE);
			iv.fitWidthProperty().set(defaultValues.PROFILE_SIZE);
			profileImages.add(iv);
			mainGamePane.getChildren().add(iv);
		}
	}

	private void setCards() {
		// a saját kártyáim nem idemennek
		// a 0. helyen az első hely lapjai vannak
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
			ImageView card = new ImageView(new Image(IMAGE_PREFIX + "1.png"));
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
		}
		// el van csúszva: a 0. én vagyok, az utolsót nem érinti a ciklus
		profileImages.get(0).setVisible(true);
		profileImages.get(opponentsCards.size()).setVisible(false);
	}

	public void hideHouseCards() {
		for (int i = 0; i < houseCards.size(); i++) {
			houseCards.get(i).setVisible(false);
		}
	}

	public void receivedBlindHouseCommand(HouseHoldemCommand houseHoldemCommand) {
		dealer = houseHoldemCommand.getDealer();
		youAreNth = houseHoldemCommand.getNthPlayer();
		clientsCount = houseHoldemCommand.getPlayers();
		System.out.println("Beallitom a clientsCount erteket: " + clientsCount);
		HOLVANADEALERGOMB = (clientsCount + dealer - youAreNth) % clientsCount;
		Platform.runLater(
				new Runnable() {

					@Override
					public void run() {
						clearChips();
						hideHouseCards();
						for (int i = 0; i < houseHoldemCommand.getPlayers(); i++) {
							profileImages.get(i).setVisible(true);
							// TODO: ITT EL VAN CSÚSZVA
							opponentsCards.get(i).setVisible(true);
							opponentsCardSides.get(i).setVisible(true);
						}

						opponentsCards.get(houseHoldemCommand.getPlayers() - 1).setVisible(false);
						opponentsCardSides.get(houseHoldemCommand.getPlayers() - 1).setVisible(false);


						dealerButtonImageView.setLayoutX(defaultValues.DEALER_BUTTON_POSITIONS[HOLVANADEALERGOMB * 2]);
						dealerButtonImageView.setLayoutY(defaultValues.DEALER_BUTTON_POSITIONS[HOLVANADEALERGOMB * 2 + 1]);
						dealerButtonImageView.setVisible(true);
					}
				});
	}

	public void player(HouseHoldemCommand houseHoldemCommand) {
		int value = mapCard(houseHoldemCommand.getCard1());
		int value2 = mapCard(houseHoldemCommand.getCard2());
		//		System.out.println("Egyik lapom: " + value);
		//		System.out.println("Masik lapom: " + value2);
		myCard1.setImage(new Image(IMAGE_PREFIX + value + ".png"));
		myCard2.setImage(new Image(IMAGE_PREFIX + value2 + ".png"));

		if (KIKOVETKEZIK != -1) {
			profileImages.get(KIKOVETKEZIK).getStyleClass().remove("glow");
		}
		KIKOVETKEZIK = (HOLVANADEALERGOMB + 3) % 2;
		profileImages.get(KIKOVETKEZIK).getStyleClass().add("glow");
	}

	public void flop(HouseHoldemCommand houseHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorSmallBlind();
				revealCard(houseHoldemCommand, 0);
				revealCard(houseHoldemCommand, 1);
				revealCard(houseHoldemCommand, 2);
			}
		});
	}

	public void turn(HouseHoldemCommand houseHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorSmallBlind();
				revealCard(houseHoldemCommand, 3);
			}
		});
	}

	public void river(HouseHoldemCommand houseHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorSmallBlind();
				revealCard(houseHoldemCommand, 4);
			}
		});
	}

	private void revealCard(HouseHoldemCommand houseHoldemCommand, int i) {
		int value = mapCard((i == 1) ? houseHoldemCommand.getCard1() : (i == 2) ? houseHoldemCommand.getCard3() : houseHoldemCommand.getCard1());
		houseCards.get(i).setImage(new Image(IMAGE_PREFIX + value + ".png"));
		houseCards.get(i).setVisible(true);
	}

	public void colorNextPlayer() {
		profileImages.get(KIKOVETKEZIK).getStyleClass().remove("glow");
		++KIKOVETKEZIK;
		KIKOVETKEZIK %= clientsCount;
		profileImages.get(KIKOVETKEZIK).getStyleClass().add("glow");
	}

	public void colorSmallBlind() {
		profileImages.get(KIKOVETKEZIK).getStyleClass().remove("glow");
		KIKOVETKEZIK = HOLVANADEALERGOMB + 1;
		KIKOVETKEZIK %= clientsCount;
		profileImages.get(KIKOVETKEZIK).getStyleClass().add("glow");
	}

	public void receivedBlindPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer();
				addChip();
			}
		});
	}

	public void receivedCallPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer();
				addChip();
			}
		});

	}

	public void receivedCheckPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		colorNextPlayer();
	}

	public void receivedFoldPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		// TODO Auto-generated method stub

	}

	public void receivedRaisePlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer();
				addChip();
				addChip();
			}
		});
	}

	public void receivedQuitPlayerCommand(PlayerHoldemCommand playerHoldemCommand) {
		// TODO Auto-generated method stub

	}

	public void addChip() {
		ImageView chip = new ImageView(new Image(CHIP_PREFIX + "black.png"));
		int max = defaultValues.CHIPS_POINT[0] + 20;
		int min = defaultValues.CHIPS_POINT[1] - 20;
		chip.setLayoutX(random.nextInt(max - min) + min);
		chip.setLayoutY(random.nextInt(max - min) + min);
		chip.setFitHeight(defaultValues.CHIPS_SIZE);
		chip.setFitWidth(defaultValues.CHIPS_SIZE);
		chips.add(chip);
		mainGamePane.getChildren().add(chip);
	}

	private void clearChips() {
		for (ImageView imageView : chips) {
			mainGamePane.getChildren().remove(imageView);
		}
		chips.clear();
	}
}

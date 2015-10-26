package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;
import com.cantero.games.poker.texasholdem.CardSuitEnum;

import hu.elte.bfw1p6.poker.client.controller.PokerHoldemDefaultValues;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MainView {

	private final String IMAGE_PREFIX = "/images/cards/";

	private PokerHoldemDefaultValues defaultValues;

	private List<ImageView> profileImages;
	private List<ImageView> opponentsCards;
	private List<ImageView> opponentsCardSides;
	private List<ImageView> houseCards;
	
	private ImageView myCard1;
	private ImageView myCard2;

	private ImageView dealerButtonImageView;

	private AnchorPane mainGamePane;
	
	private int youAreNth;
	private int dealer;
	private int clientsCount;
	
	private int HOLVANADEALERGOMB;

	public MainView(AnchorPane mainGamePane) {
		this.defaultValues = PokerHoldemDefaultValues.getInstance();
		this.mainGamePane = mainGamePane;
		this.profileImages = new ArrayList<>();
		this.opponentsCards = new ArrayList<>();
		this.opponentsCardSides = new ArrayList<>();
		this.houseCards = new ArrayList<>();
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

	public void blind(HouseHoldemCommand houseHoldemCommand) {
		System.out.println("blind...................");
		hideHouseCards();
		for (int i = 0; i < houseHoldemCommand.getPlayers(); i++) {
			profileImages.get(i).setVisible(true);
			// TODO: ITT EL VAN CSÚSZVA
			opponentsCards.get(i).setVisible(true);
			opponentsCardSides.get(i).setVisible(true);
		}
		
		opponentsCards.get(houseHoldemCommand.getPlayers() - 1).setVisible(false);
		opponentsCardSides.get(houseHoldemCommand.getPlayers() - 1).setVisible(false);

		dealer = houseHoldemCommand.getDealer();
		youAreNth = houseHoldemCommand.getNthPlayer();
		clientsCount = houseHoldemCommand.getPlayers();
		System.out.println("BLIND MOCSKOSUL NEM NULLA TE FÉREGFASZ: " + clientsCount);
		System.out.println("K: " + dealer);
		System.out.println("NthPlayer: " + youAreNth);
//		int value = (s - Math.abs(d - nTh)) % s;
		HOLVANADEALERGOMB = (clientsCount + dealer - youAreNth) % clientsCount;
		System.out.println("Hol van a vak: " + HOLVANADEALERGOMB);
		dealerButtonImageView.setLayoutX(defaultValues.DEALER_BUTTON_POSITIONS[HOLVANADEALERGOMB * 2]);
		dealerButtonImageView.setLayoutY(defaultValues.DEALER_BUTTON_POSITIONS[HOLVANADEALERGOMB * 2 + 1]);
		dealerButtonImageView.setVisible(true);
	}
	
	public void player(HouseHoldemCommand houseHoldemCommand) {
		System.out.println("show my card.....................");
		int gap = 5;
		int value = mapCard(houseHoldemCommand.getCard1());
		int value2 = mapCard(houseHoldemCommand.getCard2());
		System.out.println("Egyik lapom: " + value);
		System.out.println("Masik lapom: " + value2);
		myCard1 = new ImageView(new Image(IMAGE_PREFIX + value + ".png"));
		myCard2 = new ImageView(new Image(IMAGE_PREFIX + value2 + ".png"));
		myCard1.setLayoutX(defaultValues.MY_CARDS_POSITION[0]);
		myCard1.setLayoutY(defaultValues.MY_CARDS_POSITION[1]);
		myCard2.setLayoutX(defaultValues.MY_CARDS_POSITION[0] + defaultValues.CARD_WIDTH + gap);
		myCard2.setLayoutY(defaultValues.MY_CARDS_POSITION[1]);
//		hideAllProfiles();
		
//		Platform.runLater(new Runnable(){
//
//			@Override
//			public void run() {
//				profileImages.get(houseHoldemCommand.getWhosOn()).setVisible(false);
//				dealerButtonImageView.setVisible(false);
//		System.out.println("eleje: " + (clientsCount + dealer - youAreNth));
		System.out.println("PLAYER 2. MOCSKOSUL NEM NULLA TE FÉREGFASZ: " + clientsCount);
		System.out.println("ELŐTTE " + clientsCount);
		int next = (HOLVANADEALERGOMB + 3) % 2;
		System.out.println("NEXT BAZDMEG: " + next);
//		int eleje = clientsCount + houseHoldemCommand.getWhosOn() - youAreNth;
//		if (eleje > clientsCount) {
//			eleje %= clientsCount;
//		}
//				int value_ = (clientsCount + dealer - youAreNth) % clientsCount;
//				System.out.println("glow........................" + value_);
//				asd();
				profileImages.get(next).getStyleClass().add("glow");
//				profileImages.get(next).setFitHeight(20);
//				profileImages.get(next).setFitWidth(20);
				mainGamePane.getChildren().addAll(myCard1, myCard2);
//			}
//		});
	}

	public void flop(HouseHoldemCommand houseHoldemCommand) {
		revealCard(houseHoldemCommand, 0);
		revealCard(houseHoldemCommand, 1);
		revealCard(houseHoldemCommand, 2);
	}

	public void turn(HouseHoldemCommand houseHoldemCommand) {
		revealCard(houseHoldemCommand, 3);
	}

	public void river(HouseHoldemCommand houseHoldemCommand) {
		revealCard(houseHoldemCommand, 4);
	}

	private void revealCard(HouseHoldemCommand houseHoldemCommand, int i) {
		int value = mapCard((i == 1) ? houseHoldemCommand.getCard1() : (i == 2) ? houseHoldemCommand.getCard3() : houseHoldemCommand.getCard1());
		houseCards.get(i).setImage(new Image(IMAGE_PREFIX + value + ".png"));
		houseCards.get(i).setVisible(true);
	}
}

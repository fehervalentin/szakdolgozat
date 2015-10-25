package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;

import com.cantero.games.poker.texasholdem.Card;

import hu.elte.bfw1p6.poker.client.controller.PokerHoldemDefaultValues;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MainView {
	
	private PokerHoldemDefaultValues defaultValues;

	private List<ImageView> profileImages;
	private List<ImageView> opponentsCards;
	private List<ImageView> opponentsCardSides;
	private List<ImageView> houseCards;
	
	private ImageView dealerButtonImageView;
	
	private AnchorPane mainGamePane;
	
	public MainView(AnchorPane mainGamePane) {
		defaultValues = PokerHoldemDefaultValues.getInstance();
		this.mainGamePane = mainGamePane;
		this.profileImages = new ArrayList<>();
		this.opponentsCards = new ArrayList<>();
		this.opponentsCardSides = new ArrayList<>();
		this.houseCards = new ArrayList<>();
		setDealerButton();
		setMyProfile();
		setProfileImages();
		setDeck();
		setCards();
		setHouseCards();
		hideAllProfiles();
	}
	
	private void setDealerButton() {
		dealerButtonImageView = new ImageView(new Image(defaultValues.DEALER_BUTTON_IMAGE_URL));
	}
	
	private void setMyProfile() {
		ImageView iv = new ImageView(new Image(defaultValues.PROFILE_IMAGE_URL));
		iv.setLayoutX(defaultValues.MY_PROFILE_POINT[0]);
		iv.setLayoutY(defaultValues.MY_PROFILE_POINT[1]);
		iv.fitHeightProperty().set(defaultValues.PROFILE_SIZE);
		iv.fitWidthProperty().set(defaultValues.PROFILE_SIZE);
		mainGamePane.getChildren().add(iv);
	}
	
	private void setProfileImages() {
		for (int i = 0; i < defaultValues.PROFILE_COUNT * 2 - 2; i+=2) {
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
		int gap = 20;
		ImageView previous = new ImageView(new Image(defaultValues.DECK_IMAGE_URL));
		previous.setLayoutX(defaultValues.DECK_POINT[0] + gap);
		previous.setLayoutY(defaultValues.DECK_POINT[1]);
		for (int i = 0; i < defaultValues.PROFILE_COUNT; i++) {
			ImageView card = new ImageView(new Image("/images/cards/1.png"));
			card.setLayoutX(previous.getLayoutX() + defaultValues.CARD_WIDTH + 5);
			card.setLayoutY(previous.getLayoutY());
			card.setVisible(false);
			houseCards.add(card);
			mainGamePane.getChildren().add(card);
			previous = card;
		}
	}

	public void showMyCards(HouseHoldemCommand houseHoldemCommand) {
		int value = mapCard(houseHoldemCommand.getCard1());
		int value2 = mapCard(houseHoldemCommand.getCard2());
		System.out.println("Egyi lapom: " + value);
		System.out.println("Masik lapom: " + value2);
		ImageView card1 = new ImageView(new Image("/images/cards/" + value + ".png"));
		ImageView card2 = new ImageView(new Image("/images/cards/" + value2 + ".png"));
		card1.setLayoutX(600);
		card1.setLayoutY(430);
		card2.setLayoutX(670);
		card2.setLayoutY(430);
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				mainGamePane.getChildren().addAll(card1, card2);
			}
			});
	}
	
	private int mapCard(Card card) {
		return 52 - (card.getRankToInt() * 4) - (4 - card.getSuit().ordinal() - 1);
	}
	
	private void hideAllProfiles() {
		for (int i = 0; i < profileImages.size(); i++) {
			profileImages.get(i).setVisible(false);
			opponentsCards.get(i).setVisible(false);
			opponentsCardSides.get(i).setVisible(false);

		}
	}
	
	public void hideHouseCards() {
		for (int i = 0; i < houseCards.size(); i++) {
			houseCards.get(i).setVisible(false);
		}
	}

	public void blind(HouseHoldemCommand houseHoldemCommand) {
		hideHouseCards();
		int n = houseHoldemCommand.getPlayers();
		for (int i = 0; i < n - 1; i++) {
			profileImages.get(i).setVisible(true);
			opponentsCards.get(i).setVisible(true);
			opponentsCardSides.get(i).setVisible(true);
		}
		
		int k = houseHoldemCommand.getDealer();
		dealerButtonImageView.setLayoutX(defaultValues.DEALER_BUTTON_POSITIONS[k * 2]);
		dealerButtonImageView.setLayoutY(defaultValues.DEALER_BUTTON_POSITIONS[k * 2 + 1]);
		dealerButtonImageView.setVisible(true);
	}

	public void flop(HouseHoldemCommand houseHoldemCommand) {
		int value = mapCard(houseHoldemCommand.getCard1());
		int value2 = mapCard(houseHoldemCommand.getCard2());
		int value3= mapCard(houseHoldemCommand.getCard3());
		houseCards.get(0).setImage(new Image("/images/cards/" + value + ".png"));
		houseCards.get(1).setImage(new Image("/images/cards/" + value2 + ".png"));
		houseCards.get(2).setImage(new Image("/images/cards/" + value3 + ".png"));
		
		houseCards.get(0).setVisible(true);
		houseCards.get(1).setVisible(true);
		houseCards.get(2).setVisible(true);
	}
	
	public void turn(HouseHoldemCommand houseHoldemCommand) {
		int value = mapCard(houseHoldemCommand.getCard1());
		houseCards.get(3).setImage(new Image("/images/cards/" + value + ".png"));
		houseCards.get(3).setVisible(true);
	}
	
	public void river(HouseHoldemCommand houseHoldemCommand) {
		int value = mapCard(houseHoldemCommand.getCard1());
		houseCards.get(4).setImage(new Image("/images/cards/" + value + ".png"));
		houseCards.get(4).setVisible(true);
	}
}

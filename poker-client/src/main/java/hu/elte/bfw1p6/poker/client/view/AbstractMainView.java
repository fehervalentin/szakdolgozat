package hu.elte.bfw1p6.poker.client.view;

import java.math.BigDecimal;
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

	protected Label myBalance;

	protected ImageView dealerButtonImageView;

	protected AnchorPane mainGamePane;

	protected int clientsCount;

	protected Random random;

	protected int DEALER_BUTTON_POSITION;

	protected int coloredPlayer = -1;

	protected int nextPlayer = -1;

	protected int youAreNth;

	protected int fixSitPosition;

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
		this.myBalance = new Label();
		this.random = new Random();

		setDealerButton();
		setProfileImages();
		setDeck();
		setCards();
		setLabels();
		hideAllProfiles();
	}

	/**
	 * Felhelyezi az asztalra a dealer gombot.
	 */
	protected void setDealerButton() {
		dealerButtonImageView = new ImageView(new Image(defaultValues.DEALER_BUTTON_IMAGE_URL));
		dealerButtonImageView.setFitHeight(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setFitWidth(defaultValues.DEALER_BUTTON_SIZE);
		dealerButtonImageView.setVisible(false);
		mainGamePane.getChildren().add(dealerButtonImageView);
	}

	/**
	 * Legenerálja a profilképeket.
	 */
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

	/**
	 * Megjeleníti a kártyapaklit a GUI-n.
	 */
	protected void setDeck() {
		ImageView deck = new ImageView(new Image(defaultValues.DECK_IMAGE_URL));
		deck.setLayoutX(defaultValues.DECK_POINT[0]);
		deck.setLayoutY(defaultValues.DECK_POINT[1]);
		mainGamePane.getChildren().add(deck);
	}

	/**
	 * A GUI-n megtalálható összes kártyalapot legenerálja.
	 */
	protected void setCards() {
		// opponents cards
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

	/**
	 * A userneveket címkéket generálja le.
	 */
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
		myBalance.setLayoutX(defaultValues.PROFILE_POINTS[0]);
		myBalance.setLayoutY(defaultValues.PROFILE_POINTS[1]+20);
		mainGamePane.getChildren().add(myBalance);
	}

	/**
	 * Megfelelő helyen jeleníti meg a beérkező userneveket.
	 * @param userNames a megjelenítendő usernevek
	 */
	protected void setLabelUserNames(List<String> userNames) {
		for (int i = fixSitPosition, counter = 0; counter < clientsCount; counter++,i++) {
			userNameLabels.get(counter).setText(userNames.get(i % clientsCount));
		}
	}

	/**
	 * Egy kártyát feleltet meg egy képnek.
	 * @param card a kártyalap
	 * @return a megfeleltetett érték
	 */
	protected int mapCard(Card card) {
		return 52 - (card.getRankToInt() * CardSuitEnum.values().length) - (CardSuitEnum.values().length - card.getSuit().ordinal() - 1);
	}

	/**
	 * Elrejti az összes ellenfél profilképet és lefordított kártyalapokat.
	 */
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

	/**
	 * A következő játékos profilját jelöli meg a GUI-n.
	 * @param pokerCommand az utasítás
	 */
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

	/**
	 * A paramétert konvertálja a megfelelő játékos adott táblanézet szerint. (Mindegyik játékos máshigy látja a táblát.)
	 * @param whosOn a paraméter
	 * @return
	 */
	protected int ultimateFormula(int whosOn) {
		int value = (whosOn - fixSitPosition) % clientsCount;
		value += value < 0 ? clientsCount : 0;
		return value;
	}

	/**
	 * A szerver BLIND utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	public void receivedBlindHouseCommand(HouseCommand houseCommand) {
		clientsCount = houseCommand.getPlayers();
		youAreNth = houseCommand.getNthPlayer();
		fixSitPosition = houseCommand.getFixSitPosition();
		DEALER_BUTTON_POSITION = (clientsCount + houseCommand.getDealer() - youAreNth) % clientsCount;
		Platform.runLater(
				new Runnable() {

					@Override
					public void run() {
						hideAllProfiles();
						setLabelUserNames(houseCommand.getPlayersNames());
						resetOpacity();
						clearChips();
						for (int i = 0; i < houseCommand.getPlayers(); i++) {
							userNameLabels.get(i).setVisible(true);
							profileImages.get(i).setVisible(true);

						}
						for (int i = 0; i < houseCommand.getPlayers() - 1; i++) {
							opponentsCards.get(i).setVisible(true);
						}
						for (int j = 0; j < (defaultValues.MY_CARDS_COUNT - 1) * (clientsCount - 1); j++) {
							opponentsCardSides.get(j).setVisible(true);
						}
						dealerButtonImageView.setLayoutX(defaultValues.DEALER_BUTTON_POSITIONS[DEALER_BUTTON_POSITION * 2]);
						dealerButtonImageView.setLayoutY(defaultValues.DEALER_BUTTON_POSITIONS[DEALER_BUTTON_POSITION * 2 + 1]);
						dealerButtonImageView.setVisible(true);
					}
				});
	}

	/**
	 * A szerver DEAL utasítást küldött.
	 * @param houseCommand az utasítás
	 */
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

	/**
	 * Megjeleníti a saját kártyalapjaimat.
	 * @param houseCommand az utasítás
	 */
	public void loadMyCards(HouseCommand houseCommand) {
		Card[] cards = houseCommand.getCards();
		for (int j = 0; j < defaultValues.MY_CARDS_COUNT; j++) {
			int value = mapCard(cards[j]);
			myCards.get(j).setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + value + ".png"));
		}
	}

	/**
	 * Egy kliens RAISE utasítást küldött.
	 * @param playerCommand az utasítés
	 */
	public void receivedRaisePlayerCommand(PlayerCommand playerCommand) {
		colorNextPlayer(playerCommand);
		addChip();
		addChip();
	}

	/**
	 * Egy kliens BLIND utasítást küldött.
	 * @param playerCommand az utasítás
	 */
	public void receivedBlindPlayerCommand(PlayerCommand playerCommand) {
		addChip();
	}

	/**
	 * Egy kliens CALL utasítást küldött.
	 * @param playerCommand az utasítás
	 */
	public void receivedCallPlayerCommand(PlayerCommand playerCommand) {
		colorNextPlayer(playerCommand);
		addChip();
	}

	/**
	 * Egy kliens CHECK utasítást küldött.
	 * @param playerCommand az utasítás
	 */
	public void receivedCheckPlayerCommand(PlayerCommand playerCommand) {
		colorNextPlayer(playerCommand);
	}

	/**
	 * Egy kliens FOLD utasítást küldött.
	 * @param playerCommand az utasítás
	 */
	public void receivedFoldPlayerCommand(PlayerCommand playerCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				double opacity = 0.4;
				int convertedWhoFold = ultimateFormula(playerCommand.getWhosQuit());
				if (convertedWhoFold == 0) {
					hideMyCards(opacity);
				} else {
					setNthPlayersCardsOpacity(opacity, convertedWhoFold);
				}
				colorNextPlayer(playerCommand);
			}
		});
	}

	/**
	 * A szerver WINNER utasítást küldött.
	 * @param houseCommand az utasítás
	 */
	public void winner(HouseCommand houseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				
				int convertedWinnerIndex = ultimateFormula(houseCommand.getWinner());
				System.out.println("CSESZETTÜL ITT: " + convertedWinnerIndex);
				System.out.println("FixsitPos: " + fixSitPosition);
				// ha nem én nyertem...
				if (convertedWinnerIndex != 0) {
					System.out.println("Nem én nyertem :(");
					System.out.println("ConvertedIndex :" + convertedWinnerIndex);
					Card[] cards = houseCommand.getCards();
					for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
						winnerCards.get(i).setImage(new Image(defaultValues.CARD_IMAGE_PREFIX + mapCard(cards[i]) + ".png"));
					}
					setNthPlayersCardsOpacity(0, convertedWinnerIndex);
					int gap = 5;
					for (int i = 0; i < defaultValues.MY_CARDS_COUNT; i++) {
						winnerCards.get(i).setLayoutX(defaultValues.CARD_B1FV_POINTS[(convertedWinnerIndex-1) * 2] + i * defaultValues.CARD_WIDTH);
						winnerCards.get(i).setLayoutY(defaultValues.CARD_B1FV_POINTS[(convertedWinnerIndex-1) * 2 + 1]);
						winnerCards.get(i).setVisible(true);
						winnerCards.get(i).setOpacity(1);
					}
				}
			}
		});
	}
	
	/**
	 * i. ellenfél kártyalapjainak az áttetszőgését módosítja.
	 * @param opacity az áttettszőség mértéke
	 * @param convertedValue az i. ellenfél
	 */
	private void setNthPlayersCardsOpacity(double opacity, int convertedValue) {
		opponentsCards.get(convertedValue - 1).setVisible(false);
		for (int i = (convertedValue - 1) * (defaultValues.MY_CARDS_COUNT - 1), counter = 0; counter < defaultValues.MY_CARDS_COUNT - 1; i++, counter++) {
			opponentsCardSides.get(i).setVisible(false);
		}
	}

	/**
	 * Visszaállítja az áttettszőség értékeket.
	 */
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

	/**
	 * Ha egy felhasználó kilépett.
	 * @param playerCommand az utasítás
	 */
	public void receivedQuitPlayerCommand(PlayerCommand playerCommand) {
		// TODO Auto-generated method stub

	}

	/**
	 * A ház lapjait rejti el a GUI-ról.
	 */
	protected abstract void hideHouseCards();

	/**
	 * Frissíti a GUI-n lévő egyenleget.
	 * @param balance az új egyenleg
	 */
	public void setBalance(BigDecimal balance) {
		Platform.runLater(new Runnable() {
			public void run() {
				myBalance.setText(balance.toString());
			}
		});
	}

	/**
	 * Elrejti a saját kártyáimat a GUI-ról.
	 * @param opacity
	 */
	protected void hideMyCards(double opacity) {
		for (ImageView imageView : myCards) {
			imageView.setOpacity(opacity);
		}
	}
}

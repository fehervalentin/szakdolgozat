package hu.elte.bfw1p6.poker.client.view;

import java.util.ArrayList;
import java.util.List;

import hu.elte.bfw1p6.poker.client.defaultvalues.ClassicDefaultValues;
import hu.elte.bfw1p6.poker.command.HouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import hu.elte.bfw1p6.poker.command.classic.ClassicPlayerCommand;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * A kliens játék közbeni classic játékstílus megjelenítéséért felelős osztály.
 * @author feher
 *
 */
public class ClassicMainView extends AbstractMainView {

	public ClassicMainView(AnchorPane mainGamePane) {
		super(mainGamePane, ClassicDefaultValues.getInstance());
	}

	@Override
	protected void hideHouseCards() {
	}
	
	/**
	 * CHANGE típusú utasítás érkezett egy játékostól.
	 * @param classicHouseCommand az utasítás
	 */
	public void receivedChangeHouseCommand(ClassicHouseCommand classicHouseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(classicHouseCommand);
				for (ImageView imageView : myCards) {
					imageView.setOnMouseClicked(new EventHandler<Event>() {

						@Override
						public void handle(Event event) {
							if (imageView.getStyleClass().contains("glow")) {
								imageView.getStyleClass().remove("glow");
							} else {
								imageView.getStyleClass().add("glow");
							}
						}
					});
				}
			}
		});
	}
	
	/**
	 * DEAL2 típusú utasítás érkezett a szervertől.
	 * @param houseCommand az utasítás
	 */
	public void receivedDeal2HouseCommand(HouseCommand houseCommand) {
		System.out.println("lol");
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				loadMyCards(houseCommand);
				colorNextPlayer(houseCommand);
			}
		});
	}
	
	/**
	 * CHANGE típusú utasítás érkezett egy játékostól.
	 * @param classicPlayerCommand az utasítás
	 */
	public void receivedChangePlayerCommand(ClassicPlayerCommand classicPlayerCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(classicPlayerCommand);
			}
		});
	}

	/**
	 * Összegyűjti a cserélendő lapokat.
	 * @return a cserélendő lapok sorszámai
	 */
	public List<Integer> getMarkedCards() {
		List<Integer> markedCards = new ArrayList<>();
		for (int i = 0; i < myCards.size(); i++) {
			if (myCards.get(i).getStyleClass().contains("glow")) {
				markedCards.add(i);
				myCards.get(i).getStyleClass().remove("glow");
			}
		}
		return markedCards;
	}
}
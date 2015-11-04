package hu.elte.bfw1p6.poker.client.view;

import hu.elte.bfw1p6.poker.client.controller.ClassicDefaultValues;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ClassicMainView extends AbstractMainView {

	public ClassicMainView(AnchorPane mainGamePane) {
		super(mainGamePane, ClassicDefaultValues.getInstance());
	}

	@Override
	protected void hideHouseCards() {
	}
	
	public void receivedBetHouseCommand(ClassicHouseCommand classicHouseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(classicHouseCommand);
			}
		});
	}
	
	public void receivedChangeHouseCommand(ClassicHouseCommand classicHouseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(classicHouseCommand);
			}
		});
	}
	
	public void receivedDeal2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				//TODO: ki kell cserélnem a kártyáimat!
				colorNextPlayer(classicHouseCommand);
			}
		});
	}
	
	public void receivedBet2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				colorNextPlayer(classicHouseCommand);
			}
		});
	}

}

package hu.elte.bfw1p6.poker.client.view;

import hu.elte.bfw1p6.poker.client.controller.ClassicDefaultValues;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class ClassicMainView extends AbstractMainView {

	public ClassicMainView(AnchorPane mainGamePane) {
		super(mainGamePane, ClassicDefaultValues.getInstance());
	}

	@Override
	protected void hideHouseCards() {
	}
	
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
				//TODO: ráaggatni a listenereket a kártyalapokra
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
}

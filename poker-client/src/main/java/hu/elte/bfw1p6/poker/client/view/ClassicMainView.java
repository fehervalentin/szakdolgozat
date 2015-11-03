package hu.elte.bfw1p6.poker.client.view;

import hu.elte.bfw1p6.poker.client.controller.ClassicDefaultValues;
import hu.elte.bfw1p6.poker.command.classic.ClassicHouseCommand;
import javafx.scene.layout.AnchorPane;

public class ClassicMainView extends AbstractMainView {

	public ClassicMainView(AnchorPane mainGamePane) {
		super(mainGamePane, ClassicDefaultValues.getInstance());
	}

	@Override
	protected void hideHouseCards() {
	}
	
	public void receivedBetHouseCommand(ClassicHouseCommand classicHouseCommand) {
		
	}
	
	public void receivedChangeHouseCommand(ClassicHouseCommand classicHouseCommand) {
		
	}
	
	public void receivedDeal2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		
	}
	
	public void receivedBet2HouseCommand(ClassicHouseCommand classicHouseCommand) {
		
	}

}

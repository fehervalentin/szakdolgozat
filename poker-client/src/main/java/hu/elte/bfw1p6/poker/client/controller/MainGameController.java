package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.CommunicatorController;
import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerObserverController;
import hu.elte.bfw1p6.poker.client.model.MainGameModel;
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.command.holdem.HouseHoldemCommand;
import hu.elte.bfw1p6.poker.command.type.HoldemHouseCommandType;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import javafx.fxml.Initializable;

public class MainGameController implements Initializable, PokerClientController, PokerObserverController {
	
	private MainGameModel model;
	
	private FrameController frameController;
	
	private CommunicatorController commController;
	
	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			commController = new CommunicatorController(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		model = new MainGameModel(RMIRepository.getInstance().getSessionId(), RMIRepository.getInstance().getPokerRemote());
		PokerTable table = ConnectTableHelper.getInstance().getPokerTable();
		try {
			model.connectToTable(table, commController);
		} catch (RemoteException | PokerTooMuchPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateMe(Object updateMsg) {
		if (updateMsg instanceof HouseHoldemCommand) {
			HouseHoldemCommand houseHoldemCommand = (HouseHoldemCommand)updateMsg;
			if (houseHoldemCommand.getHouseCommandType() == HoldemHouseCommandType.PLAYER) {
				System.out.println(houseHoldemCommand.getHouseCommandType());
				System.out.println(houseHoldemCommand.getCard1());
				System.out.println(houseHoldemCommand.getCard2());
			}
		}
	}
}

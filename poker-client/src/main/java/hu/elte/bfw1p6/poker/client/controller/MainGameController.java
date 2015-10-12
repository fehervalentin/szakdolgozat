package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.MainGameModel;
import hu.elte.bfw1p6.poker.client.model.helper.ConnectTableHelper;
import hu.elte.bfw1p6.poker.client.repository.RMIRepository;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import javafx.fxml.Initializable;

public class MainGameController implements Initializable, PokerClientController {
	
	private MainGameModel model;

	@Override
	public void setDelegateController(FrameController frameController) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model = new MainGameModel(RMIRepository.getInstance().getSessionId(), RMIRepository.getInstance().getPokerRemote());
		PokerTable table = ConnectTableHelper.getInstance().getPokerTable();
		System.out.println("hello itt vagyok");
	}

	@Override
	public void valamivan(Object asd) {
		// TODO Auto-generated method stub
		
	}
}

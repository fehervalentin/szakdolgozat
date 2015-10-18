package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.controller.main.PokerClientController;
import hu.elte.bfw1p6.poker.client.model.Model;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ProfileManagerController implements Initializable, PokerClientController {

	private final String DIFF_PASS_MSG = "A két jelszó nem egyezik!";
	private final String CHANGED_PW_MSG = "Sikeresen megváltoztattad a jelszavadat!";

	private FrameController frameController;

	@FXML private Label usernameLabel;
	@FXML private Label regDateLabel;
	@FXML private Label changePasswordLabel;

	@FXML private PasswordField oldPasswordField;
	@FXML private PasswordField newPasswordField;
	@FXML private PasswordField rePasswordField;

	@FXML private Button modifyButton;
	@FXML private Button backButton;

	private Model model;

	private Alert alert;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.model = Model.getInstance();
		alert = new Alert(null);
		usernameLabel.setText(model.getPlayer().getUserName() + " profilja");
		Date date = new Date(model.getPlayer().getRegDate() * 1000);
	    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
		regDateLabel.setText(format.format(date).toString());
		System.out.println(format.format(date).toString());
		System.out.println(model.getPlayer().getRegDate() + "");
	}

	@Override
	public void setDelegateController(FrameController frameController) {
		this.frameController = frameController;
	}

	@FXML protected void handleModify(ActionEvent event) {
		String newPassword = newPasswordField.getText();
		String rePassword = rePasswordField.getText();
		if (!newPassword.equals(rePassword) ||
				newPassword == null ||
				rePassword == null ||
				newPassword.equals("") ||
				rePassword.equals("")) {
			alert.setAlertType(AlertType.ERROR);
			alert.setContentText(DIFF_PASS_MSG);
			alert.showAndWait();
		} else {
			try {
				model.modifyPassword(oldPasswordField.getText(), newPasswordField.getText());
				alert.setAlertType(AlertType.INFORMATION);
				alert.setContentText(CHANGED_PW_MSG);
				alert.showAndWait();
			} catch (RemoteException | PokerDataBaseException | PokerInvalidPassword
					| PokerUnauthenticatedException e) {
				alert.setAlertType(AlertType.ERROR);
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
		}
	}

	@FXML protected void handleBack(ActionEvent event) {
		frameController.setTableListerFXML();
	}

}

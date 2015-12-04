package hu.elte.bfw1p6.poker.client.controller;

import java.net.URL;
import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Felhasználói fiók menedzselő controller.
 * @author feher
 *
 */
public class ProfileManagerController extends AbstractPokerClientController {

	private final String ERR_DIFF_PW_MSG = "A két jelszó nem egyezik!";
	private final String OK_CHANGED_PW_MSG = "Sikeresen megváltoztattad a jelszavadat!";
	private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@FXML private Label usernameLabel;
	@FXML private Label regDateLabel;
	@FXML private Label balanceLabel;
	@FXML private Label changePasswordLabel;
	@FXML private Label oldPasswordLabel;
	@FXML private Label newPasswordLabel;
	@FXML private Label reNewPasswordLabel;

	
	@FXML private TextField userNameField;
	@FXML private TextField regDateField;
	@FXML private TextField balanceField;
	@FXML private PasswordField oldPasswordField;
	@FXML private PasswordField newPasswordField;
	@FXML private PasswordField rePasswordField;

	@FXML private CheckBox adminCheckBox;
	
	@FXML private Button modifyButton;
	@FXML private Button backButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		userNameField.setText(model.getUser().getUserName());
		balanceField.setText(model.getUser().getBalance().toPlainString());
		adminCheckBox.setSelected(model.getUser().getAdmin());
		
		Date date = new Date(model.getUser().getRegDate() * 1000);
	    Format format = new SimpleDateFormat(DATE_FORMAT);
		regDateField.setText(format.format(date).toString());
	}

	/**
	 * A MODIFY gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleModify(ActionEvent event) {
		String newPassword = newPasswordField.getText();
		String rePassword = rePasswordField.getText();
		if (!newPassword.equals(rePassword) ||
				newPassword == null ||
				rePassword == null ||
				newPassword.equals("") ||
				rePassword.equals("")) {
			showErrorAlert(ERR_DIFF_PW_MSG);
		} else {
			try {
				model.modifyPassword(oldPasswordField.getText(), newPasswordField.getText());
				showSuccessAlert(OK_CHANGED_PW_MSG);
				oldPasswordField.setText("");
				newPasswordField.setText("");
				rePasswordField.setText("");
			} catch (PokerDataBaseException | PokerInvalidPassword e) {
				showErrorAlert(e.getMessage());
			} catch (RemoteException e) {
				remoteExceptionHandler();
			}
		}
	}

	/**
	 * A BACK gomb click handlerje.
	 * @param event az esemény
	 */
	@FXML protected void handleBack(ActionEvent event) {
		frameController.setTableListerFXML();
	}

	@Override
	public void update(Object msg) {
		// TODO Auto-generated method stub
		
	}
}
package hu.elte.bfw1p6.poker.client.view;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class FieldWithLabel extends GridPane {

	private Label label;
	private TextField textField;
	
	public FieldWithLabel() {
		label = new Label();
		textField = new TextField();
		this.add(label, 0, 0);
		this.add(textField, 0, 1);
	}
	
	public void setLabelText(String text) {
		label.setText(text);
	}
	
	public String getText() {
		return textField.getText();
	}
	
	public void setValami(String style) {
		label.getStyleClass().add(style);
	}
}

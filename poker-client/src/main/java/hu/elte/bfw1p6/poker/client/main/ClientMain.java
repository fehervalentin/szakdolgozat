package hu.elte.bfw1p6.poker.client.main;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import hu.elte.bfw1p6.poker.client.defaultvalues.HoldemDefaultValues;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * A kliens elindításáért felelős osztály.
 * @author feher
 *
 */
public class ClientMain extends Application {

	private Scene scene;
	private FrameController frameController;
	
    @Override
    public void start(Stage stage) throws Exception {
        frameController = new FrameController(stage);
        scene = frameController.getScene();
        scene.getStylesheets().add(HoldemDefaultValues.getInstance().CSS_PATH);
        
        stage.setTitle(HoldemDefaultValues.getInstance().APP_NAME);
        stage.setScene(scene);
        stage.setOnCloseRequest(getFormCloseEvent());
        stage.setResizable(false);
        stage.show();
    }

    /**
     * A kliens belépési pontja.
     * @param args parancssori paraméterek
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * A teljes alkalmazás bezárásakor lefutó eseményt létrehozó eljárást.
     * @return a létrehozott esemény
     */
    private EventHandler<WindowEvent> getFormCloseEvent() {
    	EventHandler<WindowEvent> closeEvent = new EventHandler<WindowEvent>() {

    		@Override
			public void handle(WindowEvent event) {
    			System.exit(0);
			}
    	};
    	return closeEvent;
    }
}
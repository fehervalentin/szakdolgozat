package hu.elte.bfw1p6.poker.client.main;

import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import javafx.application.Application;
import javafx.application.Platform;
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

	private final String TITLE = "Poker";
	private final String CSS_PATH = "/styles/login.css";
	
	private Scene scene;
	private FrameController frameController;
	
    @Override
    public void start(Stage stage) throws Exception {
        frameController = new FrameController(stage);
        scene = frameController.getScene();
        scene.getStylesheets().add(CSS_PATH);
        
        stage.setTitle(TITLE);
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
    
    private EventHandler<WindowEvent> getFormCloseEvent() {
    	EventHandler<WindowEvent> closeEvent = new EventHandler<WindowEvent>() {

    		@Override
			public void handle(WindowEvent event) {
    			try {
					UnicastRemoteObject.unexportObject(frameController, true);
				} catch (NoSuchObjectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Platform.exit();
			}
    	};
    	return closeEvent;
    }
}
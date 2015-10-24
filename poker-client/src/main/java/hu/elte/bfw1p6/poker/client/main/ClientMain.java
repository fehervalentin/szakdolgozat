package hu.elte.bfw1p6.poker.client.main;

import hu.elte.bfw1p6.poker.client.controller.main.FrameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
//        stage.setWidth(900);
//        stage.setHeight(500);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
//    	System.setProperty("java.rmi.server.codebase", "file:///" + System.getProperty("user.dir") + "src/main/java/hu/elte/bfw1p6/poker/server/");
//    	System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        launch(args);
    }
    
    private static EventHandler<WindowEvent> getFormCloseEvent() {
    	EventHandler<WindowEvent> closeEvent = new EventHandler<WindowEvent>() {

    		@Override
			public void handle(WindowEvent event) {
				Platform.exit();
			}
    		
    	};
    	return closeEvent;
    }

}

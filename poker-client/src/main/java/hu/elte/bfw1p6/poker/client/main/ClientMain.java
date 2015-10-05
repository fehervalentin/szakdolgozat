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
	private Scene scene;
	private FrameController frameController;
	
    @Override
    public void start(Stage stage) throws Exception {
    	//scene = new Scene(null);
        frameController = new FrameController(scene);
        scene = frameController.getScene();
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle(TITLE);
        stage.setScene(scene);
       // addMenu();
        stage.setOnCloseRequest(getFormCloseEvent());
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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

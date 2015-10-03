package hu.elte.bfw1p6.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class ClientMain extends Application {

	private final String TITLE = "Poker";
	private Scene scene;
	private Parent loginFXML;
	private Parent gameFXML;
	
    @Override
    public void start(Stage stage) throws Exception {
        loadResources();
        
        scene = new Scene(loginFXML);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setOnCloseRequest(getFormCloseEvent());
        stage.show();
    }
    
    public void setGameFXML() {
    	scene.setRoot(gameFXML);
    }
    
    private void loadResources() {
    	try {
			loginFXML = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
			//gameFXML = FXMLLoader.load(getClass().getResource("/fxml/Game.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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

package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class GUI extends Application {
	

	
	/**
	 * Starts the application
	 */
	@Override
	public void start(Stage stage) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI.fxml"));
			Parent root = (Parent)loader.load();
			GUIController controller = (GUIController)loader.getController();
			controller.setStage(stage); 
	        Scene scene = new Scene(root, 1200, 800);
	        //scene.getStylesheets().add("GUI/style.css");
	        stage.setTitle("ActiVCS");
	        stage.setScene(scene);
	        stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}

package frontend;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Starts the GUI as a JavaFX Application.
 * @author Nathaniel
 * @version 06-08-2018
 */
public class GUIStarter extends Application {

	/**
	 * Starts the GUI.
	 * @param args Command line arguments.
	 */
	public static void runGUI(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new MainGUI(primaryStage);
	}

}

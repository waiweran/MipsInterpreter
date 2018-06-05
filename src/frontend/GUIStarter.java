package frontend;

import javafx.application.Application;
import javafx.stage.Stage;

public class GUIStarter extends Application {

	public static void runGUI(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new MainGUI(primaryStage);
	}

}

import frontend.MainGUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainGraphics extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		new MainGUI(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}

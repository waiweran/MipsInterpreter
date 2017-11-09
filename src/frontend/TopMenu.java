package frontend;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class TopMenu implements ScreenObject {
	
	private MenuBar topBar;
	
	private Menu file;
	private Menu display;

	public TopMenu(MainGUI gui) {
		topBar = new MenuBar();
		file = new Menu("File");
		MenuItem open = new MenuItem("Open");
		open.setOnAction(e -> {
			gui.openFile();
			gui.loadProgram();
		});
		file.getItems().add(open);
		display = new Menu("Display");
		topBar.getMenus().addAll(file, display);
	}

	@Override
	public Node getGraphics() {
		return topBar;
	}

	@Override
	public void update() {
		// do nothing
	}

}

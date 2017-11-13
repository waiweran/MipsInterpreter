package frontend;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

public class TopMenu implements ScreenObject {
	
	private MenuBar topBar;
	private MainGUI gui;
	
	private Menu file;
	private Menu display;

	public TopMenu(MainGUI mainGui) {
		gui = mainGui;
		topBar = new MenuBar();
		file = new Menu("File");
		MenuItem open = new MenuItem("Open");
		open.setOnAction(e -> {
			gui.openFile();
			gui.loadProgram();
		});
		file.getItems().add(open);
		display = new Menu("Display");
		makeDisplayOptions();
		topBar.getMenus().addAll(file, display);
	}
	
	private void makeDisplayOptions() {
		Menu dataDisp = new Menu("Register & Memory Data");
		ToggleGroup toggle = new ToggleGroup();
		RadioMenuItem auto = new RadioMenuItem("Auto");
		auto.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.AUTO);
			gui.getRegisters().setDisplayType(DataDisplay.AUTO);
		});
		auto.setToggleGroup(toggle);
		RadioMenuItem hex = new RadioMenuItem("Hexadecimal");
		hex.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.HEX);
			gui.getRegisters().setDisplayType(DataDisplay.HEX);
		});		
		hex.setToggleGroup(toggle);
		RadioMenuItem dec = new RadioMenuItem("Decimal");
		dec.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.DECIMAL);
			gui.getRegisters().setDisplayType(DataDisplay.DECIMAL);
		});	
		dec.setToggleGroup(toggle);
		RadioMenuItem str = new RadioMenuItem("String");
		str.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.STRING);
			gui.getRegisters().setDisplayType(DataDisplay.STRING);
		});
		str.setToggleGroup(toggle);
		dataDisp.getItems().addAll(auto, hex, dec, str);
		display.getItems().add(dataDisp);
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

package frontend;

import backend.program.Program;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Displays the contents of memory for debugging.
 * @author Nathaniel
 * @version 11-08-2017
 */
public class MemoryDisplay implements ScreenObject {

	private VBox memoryArea;
	private Program prog;
	private DataDisplay displayType;
	
	/**
	 * Initializes the MemoryDisplay.
	 * @param program the program whose virtual memory is to be displayed.
	 */
	public MemoryDisplay(Program program) {
		prog = program;
		memoryArea = new VBox();
		displayType = DataDisplay.AUTO;
		initialize();
	}
	
	/**
	 * Iniitalizes the memory display.
	 */
	private void initialize() {
		MainGUI.setBackground(memoryArea, Color.WHITE);
		memoryArea.getChildren().add(new Text(memoryToString()));
		memoryArea.setPadding(new Insets(10, 10, 10, 10));
	}

	@Override
	public Node getGraphics() {
		memoryArea.setMinWidth(MainGUI.SCREEN_WIDTH/5);
		memoryArea.setMinHeight(MainGUI.SCREEN_HEIGHT*2/3 - 2);
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(memoryArea);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setMinViewportWidth(MainGUI.SCREEN_WIDTH/5);
		scroll.setMinHeight(MainGUI.SCREEN_HEIGHT*2/3);
		scroll.setMaxHeight(MainGUI.SCREEN_HEIGHT*2/3);
		return scroll;
	}

	@Override
	public void update() {
		((Text)memoryArea.getChildren().get(0)).setText(memoryToString());
	}
	
	/**
	 * @return The text to display to represent the memory contents onscreen.
	 */
	private String memoryToString() {
		if(displayType.equals(DataDisplay.AUTO)) return prog.getMem().toString();
		if(displayType.equals(DataDisplay.HEX)) return prog.getMem().toHexString();
		if(displayType.equals(DataDisplay.DECIMAL)) return prog.getMem().toDecimalString();
		if(displayType.equals(DataDisplay.FLOAT)) return prog.getMem().toFloatString();
		return prog.getMem().toCharString();
	}
	
	/**
	 * Sets the format in which the memory contents are displayed.
	 * @param type the new display format.
	 */
	public void setDisplayType(DataDisplay type) {
		displayType = type;
		update();
	}

}

package frontend;

import backend.program.Program;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MemoryDisplay implements ScreenObject {

	private VBox memoryArea;
	private Program prog;
	private DataDisplay displayType;
	
	public MemoryDisplay(Program program) {
		prog = program;
		memoryArea = new VBox();
		displayType = DataDisplay.AUTO;
		initialize();
	}
	
	private void initialize() {
		MainGUI.setBackground(memoryArea, Color.WHITE);
		memoryArea.getChildren().add(new Text(memoryToString()));
		memoryArea.setPadding(new Insets(10, 10, 10, 10));
	}

	@Override
	public Node getGraphics() {
		memoryArea.setMinWidth(MainGUI.SCREEN_WIDTH/5);
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
	
	
	private String memoryToString() {
		if(displayType.equals(DataDisplay.AUTO)) return prog.getMem().toString();
		if(displayType.equals(DataDisplay.HEX)) return prog.getMem().toHexString();
		if(displayType.equals(DataDisplay.DECIMAL)) return prog.getMem().toDecimalString();
		return prog.getMem().toCharString();
	}
	
	public void setDisplayType(DataDisplay type) {
		displayType = type;
		update();
	}

}

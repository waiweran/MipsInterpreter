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
	
	public MemoryDisplay(Program program) {
		prog = program;
		memoryArea = new VBox();
		initialize();
	}
	
	private void initialize() {
		MainGUI.setBackground(memoryArea, Color.WHITE);
		memoryArea.getChildren().add(new Text(prog.getMem().toString()));
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
		((Text)memoryArea.getChildren().get(0)).setText(prog.getMem().toString());
	}

}

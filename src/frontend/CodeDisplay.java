package frontend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.Line;
import backend.Program;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CodeDisplay implements ScreenObject {
	
	private VBox progDisp;
	private Map<Line, VBox> lineDisps;
	private VBox currentHighlight;
	
	public CodeDisplay(Program program) {
		progDisp = new VBox(2);
		MainGUI.setBackground(progDisp, Color.WHITE);
		lineDisps = new HashMap<>();
		initialize(program.getProgramLines());
	}
	
	private void initialize(List<Line> lines) {
		for(Line l : lines) {
			VBox line = new VBox();
			line.setPadding(new Insets(0, 0, 0, 10));
			Text text = new Text(l.getText());
			text.setFont(Font.font("Courier", 12));
			line.getChildren().add(text);
			MainGUI.setBackground(line, Color.WHITE);
			lineDisps.put(l, line);
			progDisp.getChildren().add(line);
		}
		currentHighlight = lineDisps.get(lines.get(0));
	}
	
	public void executionHighlight(Line line) {
		MainGUI.setBackground(currentHighlight, Color.WHITE);
		currentHighlight = lineDisps.get(line);
		MainGUI.setBackground(currentHighlight, Color.GREENYELLOW);
	}
	
	public void errorHighlight(Line line) {
		MainGUI.setBackground(currentHighlight, Color.WHITE);
		currentHighlight = lineDisps.get(line);
		MainGUI.setBackground(currentHighlight, Color.ORANGERED);
	}

	@Override
	public Node getGraphics() {
		progDisp.setMinWidth(MainGUI.SCREEN_WIDTH/3);
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(progDisp);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setMinViewportWidth(MainGUI.SCREEN_WIDTH/3);
		scroll.setMinHeight(MainGUI.SCREEN_HEIGHT/2);
		return scroll;
	}

	@Override
	public void update() {
		// Do nothing
	}

}

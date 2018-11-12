package frontend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.program.Line;
import backend.program.Program;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Holds the area that displays the MIPS Code.
 * @author Nathaniel
 * @version 11-08-2017
 */
public class CodeDisplay implements ScreenObject {
	
	private VBox progDisp;
	private Map<Line, VBox> lineDisps;
	private VBox currentHighlight;
	
	/**
	 * Initializes the CodeDisplay.
	 * @param program the program to display.
	 */
	public CodeDisplay(Program program) {
		progDisp = new VBox(2);
		progDisp.setPadding(new Insets(10, 10, 0, 2));
		MainGUI.setBackground(progDisp, Color.WHITE);
		lineDisps = new HashMap<>();
		initialize(program.getProgramLines());
	}
	
	/**
	 * Initializes the display of MIPS code.
	 * @param lines the program lines to display.
	 */
	private void initialize(List<Line> lines) {
		for(int i = 0; i < lines.size(); i++) {
			VBox line = new VBox();
			Text text = new Text("   ".substring((i + "").length()) + i + ": " 
					+ lines.get(i).getHex() + "\t"
					+ lines.get(i).getText());
			text.setFont(Font.font("Courier", 12));
			line.getChildren().add(text);
			MainGUI.setBackground(line, Color.WHITE);
			lineDisps.put(lines.get(i), line);
			if(i > 0 && i < lines.size() - 2) progDisp.getChildren().add(line);
		}
	}
	
	/**
	 * Highlights a given line as currently executing.
	 * @param line the line to highlight.
	 */
	public void executionHighlight(Line line) {
		if(currentHighlight != null) 
			MainGUI.setBackground(currentHighlight, Color.WHITE);
		currentHighlight = lineDisps.get(line);
		MainGUI.setBackground(currentHighlight, Color.GREENYELLOW);
	}
	
	/**
	 * Highlights a given line as having an error.
	 * @param line the line to highlight.
	 */
	public void errorHighlight(Line line) {
		if(currentHighlight != null) 
			MainGUI.setBackground(currentHighlight, Color.WHITE);
		currentHighlight = lineDisps.get(line);
		MainGUI.setBackground(currentHighlight, Color.ORANGERED);
	}

	@Override
	public Node getGraphics() {
		progDisp.setMinWidth(MainGUI.SCREEN_WIDTH/2);
		progDisp.setMinHeight(MainGUI.SCREEN_HEIGHT*2/3 - 2);
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(progDisp);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setMinViewportWidth(MainGUI.SCREEN_WIDTH/2);
		scroll.setMaxWidth(MainGUI.SCREEN_WIDTH/2);
		scroll.setMinHeight(MainGUI.SCREEN_HEIGHT*2/3);
		scroll.setMaxHeight(MainGUI.SCREEN_HEIGHT*2/3);
		return scroll;
	}

	@Override
	public void update() {
		// Do nothing
	}

}

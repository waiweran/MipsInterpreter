package frontend;

import backend.Program;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class MemoryDisplay implements ScreenObject {

	private VBox memoryArea;
	
	public MemoryDisplay(Program program) {
		memoryArea = new VBox();
	}

	@Override
	public Node getGraphics() {
		return memoryArea;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}

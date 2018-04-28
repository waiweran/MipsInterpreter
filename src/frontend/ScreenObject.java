package frontend;

import javafx.scene.Node;

/**
 * Interface for components to display on the screen.
 * @author Nathaniel
 * @version 11-12-2017
 */
public interface ScreenObject {

	/**
	 * @return the graphics to display for this object.
	 */
	public Node getGraphics();
	
	/**
	 * Updates the contents of this object onscreen.
	 */
	public void update();
	
}

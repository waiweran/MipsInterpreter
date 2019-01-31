package backend.log;

/**
 * Entry in the log of changes to the program state
 * @author Nathaniel
 * @version 01-24-2019
 */
public abstract class LogEntry {
	
	/**
	 * Reverts the change stored by this log entry.
	 */
	public abstract void undo();

}

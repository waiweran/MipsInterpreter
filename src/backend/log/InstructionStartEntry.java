package backend.log;

import backend.program.Line;

public class InstructionStartEntry extends LogEntry {
	
	private Line newLine;

	public InstructionStartEntry(Line line) {
		newLine = line;
	}
	
	public Line getLine() {
		return newLine;
	}

	@Override
	public void undo() {
		// Do nothing
	}
	
	@Override
	public String toString() {
		return "InstructionStart: " + newLine;
	}

}

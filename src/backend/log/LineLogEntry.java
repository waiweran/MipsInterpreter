package backend.log;

import backend.program.Program;

public class LineLogEntry extends LogEntry {
	
	private Program prog;
	private int oldLine;

	public LineLogEntry(Program program, int oldLineIndex) {
		prog = program;
		oldLine = oldLineIndex;
	}

	@Override
	public void undo() {
		prog.setLineNum(oldLine);
	}
	@Override
	public String toString() {
		return "LineLog: " + oldLine;
	}

}

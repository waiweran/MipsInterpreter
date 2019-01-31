package backend.log;

import java.util.Stack;

import backend.program.Line;
import exceptions.ExecutionException;

/**
 * Logs changes to the register files, PC, and memory.
 * @author Nathaniel
 * @version 01-24-2019
 */
public class Log {
	
	private Stack<LogEntry> log;

	public Log() {
		log = new Stack<>();
	}
	
	public void start() {
		log.clear();
	}
	
	public void addEntry(LogEntry entry) {
		log.push(entry);
	}
	
	public Line undo() {
		System.out.println(log);
		try {
			while(true) {
				LogEntry entry = log.peek();
				entry.undo();
				log.pop();
				if(entry instanceof InstructionStartEntry) break;
			}
			for(int i = log.size(); true; i--) {
				LogEntry entry = log.get(i - 1);
				if(entry instanceof InstructionStartEntry) {
					return ((InstructionStartEntry) entry).getLine();
				}
			}
		}
		catch (IndexOutOfBoundsException e) {
			throw new ExecutionException("Cannot undo past start of program", e);
		}
	}
	
	public void setRerun() {
		while(true) {
			LogEntry entry = log.pop();
			if(entry instanceof InstructionStartEntry) break;
		}
	}

}

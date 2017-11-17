package backend.program;

/**
 * Stores a single line of the MIPS Program.
 * May contain an instruction, comment, jump target, etc.
 * @author Nathaniel
 * @version 11-05-2017
 */
public class Line {
	
	private Instruction insn;
	private String text;
	
	/**
	 * Initializes a new line in the program, with an instruction.
	 * @param lineText the String representation of the line.
	 * @param instruction the Instruction held in the line.
	 */
	public Line(String lineText, Instruction instruction) {
		text = lineText;
		insn = instruction;
	}
	
	/**
	 * Initializes a new line of the program, without an instruction.
	 * @param lineText the String representation of the line.
	 */
	public Line(String lineText) {
		text = lineText;
		insn = null;
	}
	
	/**
	 * @return true if the line contains an executable instruction, false if not.
	 */
	public boolean isExecutable() {
		return insn != null;
	}
	
	/**
	 * @return the String representation of the line.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * @return the Instruction the line represents.
	 */
	public Instruction getInstruction() {
		return insn;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	@Override
	public int hashCode() {
		return text.hashCode();
	}
	

}

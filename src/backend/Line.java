package backend;

public class Line {
	
	private Instruction insn;
	private String text;
	
	public Line(String lineText, Instruction instruction) {
		text = lineText;
		insn = instruction;
	}
	
	public Line(String lineText) {
		text = lineText;
		insn = null;
	}
	
	public boolean isExecutable() {
		return insn != null;
	}
	
	public String getText() {
		return text;
	}
	
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

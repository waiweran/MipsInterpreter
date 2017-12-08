package backend.debugger;

import java.util.ResourceBundle;

import backend.program.Instruction;
import backend.program.opcode.Opcode;

public class InsnFormatChecker {
	
	public static final ResourceBundle insnFormats = 
			ResourceBundle.getBundle("backend.debugger.OpcodeFormats");

	public InsnFormatChecker() {
		
	}
	
	public boolean checkFormat(String format, Instruction insn) {
		Opcode op = insn.getOpcode();
		String formatRef = insnFormats.getString(op.getName());
		return format.equals(formatRef);
	}

}

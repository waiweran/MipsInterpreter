package backend.debugger;

import java.util.ResourceBundle;

import backend.program.Instruction;
import backend.program.opcode.Opcode;
import exceptions.InstructionFormatException;

public class InsnFormatChecker {
	
	private static final ResourceBundle INSN_FORMATS = 
			ResourceBundle.getBundle("backend.debugger.OpcodeFormats");
	
	public void checkFormat(String format, Instruction insn) throws InstructionFormatException {
		Opcode op = insn.getOpcode();
		String formatRef = INSN_FORMATS.getString(op.getName());
		if(format.equals(formatRef))
			throw new InstructionFormatException("Improperly formatted instruction");
	}

}

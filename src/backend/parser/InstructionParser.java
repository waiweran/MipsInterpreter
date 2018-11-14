package backend.parser;

import java.util.Arrays;
import java.util.List;

import backend.program.FPRegister;
import backend.program.Instruction;
import backend.program.Line;
import backend.program.Program;
import backend.program.Register;
import backend.program.opcode.Opcode;
import backend.program.opcode.OpcodeFactory;
import exceptions.InstructionFormatException;
import exceptions.RegisterFormatException;

/**
 * Parses MIPS instructions.
 * @author Nathaniel
 * @version 06-08-2018
 */
public class InstructionParser {
	
	private Program prog;

	/**
	 * Initializes the InstructionParser.
	 * @param program to save parsed instructions to.
	 */
	public InstructionParser(Program program) {
		prog = program;
	}
	
	/**
	 * Converts a line of the file to an instruction.
	 * @param line the line to convert.
	 * @throws InstructionFormatException 
	 */
	public void makeInstruction(String line) throws InstructionFormatException {
		OpcodeFactory opFactory = new OpcodeFactory();
		String text = line;
		if(line.indexOf('#') >= 0) text = text.substring(0,  line.indexOf('#')); // Remove Comments
		if(text.startsWith(".")) return; // Skip assembler commands
		text = text.replaceAll("[,()]", " "); // Remove unnecessary characters
		text = text.trim(); // Remove leading and trailing whitespace
		if(text.length() == 0) {
			prog.addLine(new Line(line));
			return;
		}
		List<String> insnSplit = Arrays.asList(text.split("\\s+")); // Split around remaining whitespace
		String reference = "";
		String label = null;
		Opcode opcode = null;
		int regNum = 0;
		Register[] regs = new Register[3];
		int fpRegNum = 0;
		FPRegister[] fpRegs = new FPRegister[3];
		Integer immed = null;
		boolean immedUsed = false;
		for(String comp : insnSplit) {
			// If it's a line memory address reference
			if(comp.endsWith(":")) {
				if(reference.isEmpty()) {
					reference = comp.substring(0, comp.length() - 1);
				}
				else {
					Line l = new Line(line);
					prog.addLine(l);
					throw new InstructionFormatException("Two references detected in one line", l);
				}
			}
			// If it's a register name
			else if(comp.startsWith("$")) {
				try {
					regs[regNum] = Register.findRegister(comp);
					regNum++;
				}
				catch(IndexOutOfBoundsException e) {
					Line l = new Line(line);
					prog.addLine(l);
					throw new InstructionFormatException("Too many registers specified", e, l);
				}
				catch(RegisterFormatException e1) {
					try {
						fpRegs[fpRegNum] = FPRegister.findRegister(comp);
						fpRegNum++;
					}
					catch(RegisterFormatException e2) {
						Line l = new Line(line);
						prog.addLine(l);
						throw new InstructionFormatException("Invalid or reserved register", e2, l);
					}
					catch(IndexOutOfBoundsException e3) {
						Line l = new Line(line);
						prog.addLine(l);
						throw new InstructionFormatException("Too many registers specified", e3, l);
					}
				}
			}
			// If it's an opcode
			else if(opFactory.isOpcode(comp)) {
				if(opcode != null) {
					Line l = new Line(line);
					prog.addLine(l);
					throw new InstructionFormatException("Two opcodes detected in one line", l);
				}
				opcode = opFactory.findOpcode(comp);
			}		
			else {
				// If it's an integer immediate
				try {
					immed = Integer.parseInt(comp);
					if(immedUsed) {
						Line l = new Line(line);
						prog.addLine(l);
						throw new InstructionFormatException("Only one immediate allowed", l);
					}
					immedUsed = true;
				}
				catch(NumberFormatException e) {	
					
					// If it's a float immediate
					try {
						immed = Float.floatToIntBits(Float.parseFloat(comp));
						if(immedUsed) {
							Line l = new Line(line);
							prog.addLine(l);
							throw new InstructionFormatException("Only one immediate allowed", l);
						}
						immedUsed = true;
					}
					catch(NumberFormatException e2) {	

						// Only thing left is address label
						if(label == null) {
							label = comp;
						}
						else {
							Line l = new Line(line);
							prog.addLine(l);
							throw new InstructionFormatException(
									"Unrecognized Instruction Component: " + comp, l);
						}
					}
				}
			}	
		}
		if(opcode == null) {
			if(!reference.isEmpty() && regNum == 0  && fpRegNum == 0
					&& !immedUsed && label == null) {
				Line madeLine = new Line(line);
				prog.addLabel(reference, prog.getProgramLines().size());
				prog.addLine(madeLine);
			}
			else {
				Line l = new Line(line);
				prog.addLine(l);
				throw new InstructionFormatException("No Opcode Found in Line", l);
			}
		}
		else {
			Instruction madeInsn = new Instruction(opcode, regs[0], 
					regs[1], regs[2], fpRegs[0], fpRegs[1], fpRegs[2], immed, label);
			Line madeLine = new Line(line, madeInsn);
			if(!reference.isEmpty()) prog.addLabel(reference, prog.getProgramLines().size());
			prog.addLine(madeLine);
		}
	}


}

package backend.assembler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ResourceBundle;

import backend.program.Instruction;
import backend.program.Line;
import backend.program.Program;

public class Assembler {
	
	private static final ResourceBundle ASSEMBLE_FORMATS = 
			ResourceBundle.getBundle("backend.assembler.AssembledFormats");
	
	private Program prog;
	private int insnNum;

	public Assembler(Program program) {
		prog = program;
	}
	
	public void assembleProgram(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		PrintWriter pw = new PrintWriter(fw);
		insnNum = 0;
		for(Line l : prog.getProgramLines()) {
			if(l.isExecutable()) {
				pw.println(assemble(l.getInstruction()));
				insnNum++;
			}
		}
		pw.flush();
		pw.close();
		fw.flush();
		fw.close();
	}
	
	public String assemble(Instruction insn) {
		StringBuilder output = new StringBuilder();
		String formatRef = ASSEMBLE_FORMATS.getString(insn.getOpcode().getName());
		String[] pieces = formatRef.split(" ");
		for(String s : pieces) {
			if(s.equals("reg1")) {
				output.append(makeBinaryValue(insn.getR1().getRegisterNumber(), 5));
			}
			else if(s.equals("reg2")) {
				output.append(makeBinaryValue(insn.getR2().getRegisterNumber(), 5));
			}
			else if(s.equals("reg3")) {
				output.append(makeBinaryValue(insn.getR3().getRegisterNumber(), 5));
			}
			else if(s.startsWith("immediate")) {
				int size = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf("]")));
				output.append(makeBinaryValue(insn.getImmed(), size));
			}
			else if(s.startsWith("target_absolute")) {
				int size = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf("]")));
				int targetPC = prog.getJumpPC(insn.getTarget());
				output.append(makeBinaryValue(targetPC, size));
			}
			else if(s.startsWith("target_relative")) {
				int size = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf("]")));
				int targetPC = prog.getJumpPC(insn.getTarget());
				output.append(makeBinaryValue(targetPC - insnNum + 1, size));
			}
			else {
				output.append(s);
			}
		}

		return output.toString();
	}
	
	private String makeBinaryValue(int val, int length) {
		StringBuilder output = new StringBuilder();
		String binaryVal = Integer.toBinaryString(val);
		for(int i = 0; i < length - binaryVal.length(); i++) {
			output.append("0");
		}
		if(binaryVal.length() > length) {
			output.append(binaryVal.substring(binaryVal.length() - length));
		}
		else {
			output.append(binaryVal);
		}
		return output.toString();
	}

}

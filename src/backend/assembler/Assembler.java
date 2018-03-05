package backend.assembler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import backend.program.Instruction;
import backend.program.Line;
import backend.program.Program;
import exceptions.UnsupportedOpcodeException;

public class Assembler {

	private static final ResourceBundle ASSEMBLE_FORMATS = 
			ResourceBundle.getBundle("backend.assembler.AssembledFormats");

	private Program prog;
	private Map<Integer, Integer> targets;

	public Assembler(Program program) {
		prog = program;
		targets = new HashMap<>();
		mapTargets();
	}

	public void assembleProgram(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		PrintWriter pw = new PrintWriter(fw);
		int insnNum = 0;
		for(Line l : prog.getProgramLines()) {
			if(l.isExecutable()) {
				pw.println(assemble(l.getInstruction(), insnNum));
				insnNum++;
			}
		}
		pw.flush();
		fw.flush();
		pw.close();
		fw.close();
	}

	public String assemble(Instruction insn, int insnNum) {
		try {
			StringBuilder output = new StringBuilder();
			String formatRef = ASSEMBLE_FORMATS.getString(insn.getOpcode().getName());
			String[] pieces = formatRef.split(" ");
			for(String s : pieces) {
				if(s.equals("reg1")) {
					output.append(makeBinaryValue(insn.getR1().getRegisterNumber(), 5));
				}
				else if(s.equals("reg2")) {
					try {
						output.append(makeBinaryValue(insn.getR2().getRegisterNumber(), 5));
					} catch(NullPointerException e) {
						if(insn.getOpcode().getName().equals("sw") ||
								insn.getOpcode().getName().equals("lw")) {
							output.append("00000");
						}
						else {
							output.append(makeBinaryValue(insn.getR1().getRegisterNumber(), 5));
						}
					}
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
					int targetPC = targets.get(prog.getInsnRefs().get(insn.getTarget()));
					output.append(makeBinaryValue(targetPC, size));
				}
				else if(s.startsWith("target_relative")) {
					int size = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf("]")));
					int targetPC = targets.get(prog.getInsnRefs().get(insn.getTarget()));
					output.append(makeBinaryValue(targetPC - insnNum + 1, size));
				}
				else {
					output.append(s);
				}
			}
			return output.toString();
		}
		catch(MissingResourceException e) {
			throw new UnsupportedOpcodeException(insn.getOpcode().getName() + " is not supported for export", e);
		}
	}


	private void mapTargets() {
		int insnNum = 0;
		int lineNum = 0;
		for(Line l : prog.getProgramLines()) {
			if(l.isExecutable()) {
				insnNum++;
			}
			if(prog.getInsnRefs().containsValue(lineNum)) {
				targets.put(lineNum, insnNum);
			}
			lineNum++;
		}
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

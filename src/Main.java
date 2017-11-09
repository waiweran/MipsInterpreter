import java.io.File;

import backend.Instruction;
import backend.Line;
import backend.Opcode;
import backend.Program;
import backend.Register;
import backend.TextParser;

public class Main {

	public static void main(String[] args) {
		Program prog = new Program(System.in, System.out);
		TextParser parser = new TextParser(new File(args[0]), prog);
		prog = parser.getProgram();
		setupProgramClose(prog);
		new Instruction(Opcode.Jump, null, null, null, 0, "main").execute(prog);
		while(!prog.isDone()) {
			Line currentLine = prog.getNextLine();
			//System.out.println(currentLine);
			if(currentLine.isExecutable()) {
				try {
					currentLine.getInstruction().execute(prog);
				}
				catch(Exception e) {
					throw new RuntimeException("Program line " + currentLine + 
							" caused exception", e);
				}
			}
		}
	}

	private static void setupProgramClose(Program prog) {
		prog.getRegFile().write(Register.ra, prog.getProgramLines().size());
		prog.getProgramLines().add(new Line("", new Instruction(Opcode.LoadImmediate, 
				Register.v0, null, null, 10, "")));
		prog.getProgramLines().add(new Line("", new Instruction(Opcode.Syscall, 
				null, null, null, 0, "")));
	}

}

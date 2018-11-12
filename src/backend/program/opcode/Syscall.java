package backend.program.opcode;

import backend.program.FPRegister;
import backend.program.Instruction;
import backend.program.Program;
import backend.program.Register;
import backend.state.Data;
import exceptions.ExecutionException;

public class Syscall extends Opcode {

	public Syscall(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		int type = prog.getRegFile().read(Register.v0).getValue();
		// print int
		if(type == 1) {
			prog.getOutput().print(prog.getRegFile().read(Register.a0));
		}
		// print float
		else if(type == 2) {
			prog.getOutput().print(prog.getFPRegFile().read(FPRegister.f0));
		}
		// print double
		else if(type == 3) {
			prog.getOutput().print(prog.getFPRegFile().readDouble(FPRegister.f0));
		}
		// print string
		else if(type == 4) {
			int address = prog.getRegFile().read(Register.a0).getValue();
			String out = prog.getMem().loadString(address);
			prog.getOutput().print(out);
		}
		// read int
		else if(type == 5) {
			if(prog.inputAvailable()) {
				prog.getRegFile().write(Register.v0, 
						new Data(Integer.parseInt(prog.getInput().trim()), Data.DataType.Integer));
			}
			// If input not ready, stall
			else {
				prog.setPC(prog.getPC() - 1);
			}
		}
		// read float
		else if(type == 6) {
			if(prog.inputAvailable()) {
				prog.getFPRegFile().write(FPRegister.f0, 
						new Data(Float.floatToIntBits(Float.parseFloat(prog.getInput().trim())),
						Data.DataType.Float));
			}
			// If input not ready, stall
			else {
				prog.setPC(prog.getPC() - 1);
			}
		}
		// read double
		else if(type == 7) {
			if(prog.inputAvailable()) {
				prog.getFPRegFile().writeDouble(FPRegister.f0, 
						Double.parseDouble(prog.getInput().trim()));
			}
			// If input not ready, stall
			else {
				prog.setPC(prog.getPC() - 1);
			}
		}
		// read string
		else if(type == 8) {
			if(prog.inputAvailable()) {
				int address = prog.getRegFile().read(Register.a0).getValue();
				int length = prog.getRegFile().read(Register.a1).getValue();
				String inputString = prog.getInput();
				if(inputString.length() >= length) {
					inputString = inputString.substring(0, length);
				}
				prog.getMem().storeString(address, inputString);
			}
			// If input not ready, stall
			else {
				prog.setPC(prog.getPC() - 1);
			}
		}
		// sbrk
		else if(type == 9) {
			prog.getRegFile().write(Register.v0, 
					new Data(prog.getMem().allocateHeap(
					prog.getRegFile().read(Register.a0).getValue()), Data.DataType.Address));
		}
		// exit
		else if(type == 10) {
			prog.done();
		}
		else {
			throw new ExecutionException("Invalid Syscall: " + type);
		}
	}

}

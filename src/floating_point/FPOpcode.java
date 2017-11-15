package floating_point;
import java.util.function.BiConsumer;

import backend.program.Program;
import backend.program.Register;

public enum FPOpcode {

	// Specially Added Commands
//	LoadImmediate ("li", (insn, prog) -> {
//		prog.getRegFile().write(insn.getR1(), 
//				new Data(insn.getImmed(), Data.DataType.Integer));
//	}),
//	LoadUpperImmediate ("lui", (insn, prog) -> {
//	prog.getRegFile().write(insn.getR1(), 
//			new Data(insn.getImmed() << 16));
//}),
//	AddImmediate ("addi", (insn, prog) -> {
//	prog.getRegFile().write(insn.getR1(), 
//			new Data(prog.getRegFile().read(insn.getR2() == null? 
//					insn.getR1() : insn.getR2()).getValue() + 
//			insn.getImmed()));
//}),
//	Move ("move", (insn, prog) -> {
//		prog.getRegFile().write(insn.getR1(), 
//			prog.getRegFile().read(insn.getR2()));
//	}),
	
	// Normal MIPS Commands
	Add ("add", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getR1(), 
				new FPData(prog.getFPRegFile().read(insn.getR2()).getValue() + 
				prog.getFPRegFile().read(insn.getR3()).getValue()));
	}),
	Subtract ("sub", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getR1(), 
				new FPData(prog.getFPRegFile().read(insn.getR2()).getValue() - 
				prog.getFPRegFile().read(insn.getR3()).getValue()));
	}),
	Multiply ("mult", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getR1(), 
				new FPData(prog.getFPRegFile().read(insn.getR2()).getValue() * 
				prog.getFPRegFile().read(insn.getR3()).getValue()));
	}),	
	Divide ("div", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getR1(), 
				new FPData(prog.getFPRegFile().read(insn.getR2()).getValue() / 
				prog.getFPRegFile().read(insn.getR3()).getValue()));

	}),	
	LoadWord ("lw", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getR1(), 
				prog.getMem().loadWord(insn.getImmed() + 
				((insn.getR2() == null)? 0 : 
				prog.getFPRegFile().read(insn.getR2()).getValue())));
	}),	
	StoreWord ("sw", (insn, prog) -> {
		prog.getMem().storeWord(prog.getFPRegFile().read(insn.getR1()), 
				insn.getImmed() + ((insn.getR2() == null)? 
				0 : prog.getFPRegFile().read(insn.getR2()).getValue()));
	}),	
	
	// System Calls
	Syscall ("syscall", (insn, prog) -> {
		int type = prog.getRegFile().read(Register.v0).getValue();
		// print float
		if(type == 2) {

		}
		// print double
		else if(type == 3) {
			
		}
		// read float
		else if(type == 6) {
			
		}
		// read double
		else if(type == 7) {
			
		}
		else {
			throw new RuntimeException("Invalid Syscall: " + type);
		}
	});
	
	private String opName;
	private BiConsumer<FPInstruction, Program> opAction;
	
	private FPOpcode(String name, BiConsumer<FPInstruction, Program> action) {
		opName = name;
		opAction = action;
	}
	
	public static FPOpcode findOpcode(String name) {
		for(FPOpcode op : FPOpcode.values()) {
			if(op.opName.equalsIgnoreCase(name)) return op;
		}
		throw new RuntimeException("Invalid Opcode, Name: " + name);
	}
	
	public static boolean isOpcode(String name) {
		for(FPOpcode op : FPOpcode.values()) {
			if(op.opName.equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public String getName() {
		return opName;
	}
	
	public BiConsumer<FPInstruction, Program> getAction() {
		return opAction;
	}
	
}

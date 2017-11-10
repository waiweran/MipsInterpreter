package backend;
import java.util.List;
import java.util.function.BiConsumer;

public enum Opcode {

	// Specially Added Commands
	BranchEqualZero ("beqz", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() == 0) {
			prog.jump(insn.getTarget());
		}
	}),
	BranchLessEquals ("ble", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue()
				<= prog.getRegFile().read(insn.getR2()).getValue()) {
			prog.jump(insn.getTarget());
		}
	}),
	BranchNotEqualZero ("bnez", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() != 0) {
			prog.jump(insn.getTarget());
		}
	}),
	LoadAddress ("la", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(insn.getImmed(), Data.DataType.Address));
	}),
	LoadImmediate ("li", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(insn.getImmed(), Data.DataType.Integer));
	}),
	Move ("move", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
			prog.getRegFile().read(insn.getR2()));
	}),
	SetEqual ("seq", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data((prog.getRegFile().read(insn.getR2()).getValue() == 
				prog.getRegFile().read(insn.getR3()).getValue())?
				1 : 0, Data.DataType.Integer));
	}),
	
	// Normal MIPS Commands
	Add ("add", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2()).getValue() + 
				prog.getRegFile().read(insn.getR3()).getValue()));
	}),
	AddImmediate ("addi", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2() == null? 
						insn.getR1() : insn.getR2()).getValue() + 
				insn.getImmed()));
	}),
	And ("and", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2()).getValue() & 
				prog.getRegFile().read(insn.getR3()).getValue()));
	}),
	AndImmediate ("andi", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2() == null? 
						insn.getR1() : insn.getR2()).getValue() + 
				insn.getImmed()));
	}),
	BranchEquals ("beq", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() == 
				prog.getRegFile().read(insn.getR2()).getValue()) {
			prog.jump(insn.getTarget());
		}
	}),
	BranchGreaterEqualsZero ("bgez", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() >= 0) {
			prog.jump(insn.getTarget());
		}
	}),
	BranchGreaterEqualsZeroAndLink ("bgezal", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() >= 0) {
			prog.getRegFile().write(Register.ra, new Data(prog.getPC(), Data.DataType.J_Target));
			prog.jump(insn.getTarget());
		}
	}),
	BranchGreaterZero ("bgtz", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() > 0) {
			prog.jump(insn.getTarget());
		}
	}),
	BranchLessEqualsZero ("blez", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() <= 0) {
			prog.jump(insn.getTarget());
		}
	}),	
	BranchLessZero ("bltz", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() < 0) {
			prog.jump(insn.getTarget());
		}
	}),
	BranchLessZeroAndLink ("bltzal", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() < 0) {
			prog.getRegFile().write(Register.ra, new Data(prog.getPC(), Data.DataType.J_Target));
			prog.jump(insn.getTarget());
		}
	}),
	BranchNotEquals ("bne", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() != 
				prog.getRegFile().read(insn.getR2()).getValue()) {
			prog.jump(insn.getTarget());
		}
	}),	
	Divide ("div", (insn, prog) -> {
		prog.getRegFile().writeLO(new Data(
				prog.getRegFile().read(insn.getR1()).getValue() / 
				prog.getRegFile().read(insn.getR2()).getValue()));
		prog.getRegFile().writeHI(new Data(
				prog.getRegFile().read(insn.getR1()).getValue() % 
				prog.getRegFile().read(insn.getR2()).getValue()));
	}),	
	Jump ("j", (insn, prog) -> {
		prog.jump(insn.getTarget());
	}),
	JumpAndLink ("jal", (insn, prog) -> {
		prog.getRegFile().write(Register.ra, new Data(prog.getPC(), Data.DataType.J_Target));
		prog.jump(insn.getTarget());
	}),
	JumpAndLinkRegister ("jalr", (insn, prog) -> {
		prog.getRegFile().write(Register.ra, new Data(prog.getPC(), Data.DataType.J_Target));
		prog.setPC(prog.getRegFile().read(insn.getR1()).getValue());
	}),	
	JumpRegister ("jr", (insn, prog) -> {
		prog.setPC(prog.getRegFile().read(insn.getR1()).getValue());
	}),
	LoadByte ("lb", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				prog.getMem().loadByte(insn.getImmed() + 
				((insn.getR2() == null)? 0 : 
				prog.getRegFile().read(insn.getR2()).getValue())));
	}),	
	LoadUpperImmediate ("lui", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(insn.getImmed() << 16));
	}),
	LoadWord ("lw", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				prog.getMem().loadWord(insn.getImmed() + 
				((insn.getR2() == null)? 0 : 
				prog.getRegFile().read(insn.getR2()).getValue())));
	}),	
	MoveFromHI ("mfhi", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				prog.getRegFile().readHI());
	}),
	MoveFromLO ("mflo", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				prog.getRegFile().readLO());
	}),	
	Multiply ("mult", (insn, prog) -> {
		long output = (long)prog.getRegFile().read(insn.getR1()).getValue() * 
				(long)prog.getRegFile().read(insn.getR2()).getValue();
		long outLO = (output << 16) >>> 16;
		long outHI = output >>> 16;
		prog.getRegFile().writeLO(new Data((int) outLO));
		prog.getRegFile().writeHI(new Data((int) outHI));
	}),	
	NoOperation ("noop", (insn, prog) -> {
		// do nothing
	}),
	Nor ("nor", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(~(prog.getRegFile().read(insn.getR2()).getValue() | 
				prog.getRegFile().read(insn.getR3()).getValue())));
	}),
	Or ("or", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2()).getValue() | 
				prog.getRegFile().read(insn.getR3()).getValue()));
	}),
	OrImmediate ("ori", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2() == null? 
						insn.getR1() : insn.getR2()).getValue() | 
				insn.getImmed()));
	}),
	StoreByte ("sb", (insn, prog) -> {
		prog.getMem().storeByte(prog.getRegFile().read(insn.getR1()), 
				insn.getImmed() + ((insn.getR2() == null)? 
				0 : prog.getRegFile().read(insn.getR2()).getValue()));
	}),
	ShiftLeft ("sll", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2() == null? 
						insn.getR1() : insn.getR2()).getValue() << 
				insn.getImmed()));
	}),
	ShiftLeftVariable ("sllv", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2()).getValue() << 
				prog.getRegFile().read(insn.getR3()).getValue()));
	}),
	SetLessThan ("slt", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data((prog.getRegFile().read(insn.getR2()).getValue() < 
				prog.getRegFile().read(insn.getR3()).getValue())? 1 : 0));
	}),
	SetLessThanImmediate ("slti", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data((prog.getRegFile().read(insn.getR2()).getValue() < 
				insn.getImmed())? 1 : 0));
	}),
	ShiftRightArithmetic ("sra", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2() == null? 
						insn.getR1() : insn.getR2()).getValue() >> 
				insn.getImmed()));
	}),
	ShiftRightArithmeticVariable ("srav", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2()).getValue() >> 
				prog.getRegFile().read(insn.getR3()).getValue()));
	}),
	ShiftRightLogical ("srl", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2() == null? 
						insn.getR1() : insn.getR2()).getValue() >>> 
				insn.getImmed()));
	}),
	ShiftRightLogicalVariable ("srlv", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2()).getValue() >>> 
				prog.getRegFile().read(insn.getR3()).getValue()));
	}),
	Subtract ("sub", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2()).getValue() - 
				prog.getRegFile().read(insn.getR3()).getValue()));
	}),
	StoreWord ("sw", (insn, prog) -> {
		prog.getMem().storeWord(prog.getRegFile().read(insn.getR1()), 
				insn.getImmed() + ((insn.getR2() == null)? 
				0 : prog.getRegFile().read(insn.getR2()).getValue()));
	}),	
	ExclusiveOr ("or", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2()).getValue() ^ 
				prog.getRegFile().read(insn.getR3()).getValue()));
	}),
	ExclusiveOrImmediate ("ori", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2() == null? 
						insn.getR1() : insn.getR2()).getValue() ^ 
				insn.getImmed()));
	}),	
	
	// System Calls
	Syscall ("syscall", (insn, prog) -> {
		int type = prog.getRegFile().read(Register.v0).getValue();
		// print int
		if(type == 1) {
			prog.getOutput().print(prog.getRegFile().read(Register.a0));
		}
		// print string
		else if(type == 4) {
			int address = prog.getRegFile().read(Register.a0).getValue();
			String out = TextParser.dataArrayToString(prog.getMem().loadArray(address));
			prog.getOutput().print(out);
		}
		// read int
		else if(type == 5) {
			if(prog.inputAvailable()) {
				prog.getRegFile().write(Register.v0, 
						new Data(Integer.parseInt(prog.getInput()), Data.DataType.Integer));
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
				String inputString = prog.getInput() + "\n";
				if(inputString.length() >= length) {
					inputString = inputString.substring(0, length);
				}
				List<Data> in = TextParser.stringToDataArray(inputString);
				prog.getMem().storeArray(address, in);
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
			throw new RuntimeException("Invalid Syscall: " + type);
		}
	});
	
	private String opName;
	private BiConsumer<Instruction, Program> opAction;
	
	private Opcode(String name, BiConsumer<Instruction, Program> action) {
		opName = name;
		opAction = action;
	}
	
	public static Opcode findOpcode(String name) {
		for(Opcode op : Opcode.values()) {
			if(op.opName.equalsIgnoreCase(name)) return op;
		}
		throw new RuntimeException("Invalid Opcode, Name: " + name);
	}
	
	public static boolean isOpcode(String name) {
		for(Opcode op : Opcode.values()) {
			if(op.opName.equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public String getName() {
		return opName;
	}
	
	public BiConsumer<Instruction, Program> getAction() {
		return opAction;
	}
	
}

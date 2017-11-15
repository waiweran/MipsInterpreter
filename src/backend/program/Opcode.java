package backend.program;

import java.util.List;
import java.util.function.BiConsumer;

import backend.TextParser;
import backend.state.Data;
import backend.state.FPRegister;

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
		long outLO = Long.rotateLeft(output, 32) >>> 32;
		long outHI = output >>> 32;
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
	
	// Single Precision Floating Point Commands
	AddFloat ("add.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), convertToData(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())) + 
				convertToFloat(prog.getFPRegFile().read(insn.getFPR3()))));
	}),
	SubtractFloat ("sub.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), convertToData(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())) - 
				convertToFloat(prog.getFPRegFile().read(insn.getFPR3()))));
	}),
	MultiplyFloat ("mult.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), convertToData(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())) * 
				convertToFloat(prog.getFPRegFile().read(insn.getFPR3()))));
	}),	
	DivideFloat ("div.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), convertToData(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())) / 
				convertToFloat(prog.getFPRegFile().read(insn.getFPR3()))));
	}),	
	
	// Double Precision Floating Point Commands
	AddDouble ("add.d", (insn, prog) -> {
		prog.getFPRegFile().writeDouble(insn.getFPR1(),
				prog.getFPRegFile().readDouble(insn.getFPR2()) + 
				prog.getFPRegFile().readDouble(insn.getFPR3()));
	}),
	SubtractDouble ("sub.d", (insn, prog) -> {
		prog.getFPRegFile().writeDouble(insn.getFPR1(),
				prog.getFPRegFile().readDouble(insn.getFPR2()) - 
				prog.getFPRegFile().readDouble(insn.getFPR3()));
	}),
	MultiplyDouble ("mult.d", (insn, prog) -> {
		prog.getFPRegFile().writeDouble(insn.getFPR1(),
				prog.getFPRegFile().readDouble(insn.getFPR2()) *
				prog.getFPRegFile().readDouble(insn.getFPR3()));
	}),	
	DivideDouble ("div.d", (insn, prog) -> {
		prog.getFPRegFile().writeDouble(insn.getFPR1(),
				prog.getFPRegFile().readDouble(insn.getFPR2()) / 
				prog.getFPRegFile().readDouble(insn.getFPR3()));
	}),	
	
	// Single Precision Comparisons
	CompareEqual ("c.eq.s", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) ==
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}),
	CompareNotEqual ("c.ne.s", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) !=
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}),
	CompareLessThan ("c.lt.s", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) <
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}),
	CompareLessEquals ("c.le.s", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) <=
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}),
	CompareGreaterThan ("c.gt.s", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) >
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}),
	CompareGreaterEquals ("c.ge.s", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) >=
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}),
	
	// Double Precision Comparisons
	CompareEqualDouble ("c.eq.d", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				prog.getFPRegFile().readDouble(insn.getFPR1()) ==
				prog.getFPRegFile().readDouble(insn.getFPR2()));
	}),
	CompareNotEqualDouble ("c.ne.d", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				prog.getFPRegFile().readDouble(insn.getFPR1()) !=
				prog.getFPRegFile().readDouble(insn.getFPR2()));
	}),
	CompareLessThanDouble ("c.lt.d", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				prog.getFPRegFile().readDouble(insn.getFPR1()) <
				prog.getFPRegFile().readDouble(insn.getFPR2()));
	}),
	CompareLessEqualsDouble ("c.le.d", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				prog.getFPRegFile().readDouble(insn.getFPR1()) <=
				prog.getFPRegFile().readDouble(insn.getFPR2()));
	}),
	CompareGreaterThanDouble ("c.gt.d", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				prog.getFPRegFile().readDouble(insn.getFPR1()) >
				prog.getFPRegFile().readDouble(insn.getFPR2()));
	}),
	CompareGreaterEqualsDouble ("c.ge.d", (insn, prog) -> {
		prog.getFPRegFile().writeCond(
				prog.getFPRegFile().readDouble(insn.getFPR1()) >=
				prog.getFPRegFile().readDouble(insn.getFPR2()));
	}),
	
	// Branch Commands
	BranchTrue ("bc1t", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() == 0) {
			prog.jump(insn.getTarget());
		}
	}),
	BranchFalse ("bc1f", (insn, prog) -> {
		if(prog.getRegFile().read(insn.getR1()).getValue() == 0) {
			prog.jump(insn.getTarget());
		}
	}),
	
	// Movement Commands
	MoveFloat ("mov.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), 
				prog.getFPRegFile().read(insn.getFPR2()));
	}),
	MoveDouble ("mov.d", (insn, prog) -> {
		prog.getFPRegFile().writeDouble(insn.getFPR1(), 
				prog.getFPRegFile().readDouble(insn.getFPR2()));
	}),
	MoveIntToFloat ("mtc1", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), 
				prog.getRegFile().read(insn.getR1()));
	}),
	MoveFloatToInt ("mfc1", (insn, prog) -> {
		prog.getRegFile().write(insn.getR1(), 
				prog.getFPRegFile().read(insn.getFPR2()));
	}),
	
	// Conversion Commands
	ConvertFloatToInt ("cvt.w.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), new Data((int)(
				(float)prog.getFPRegFile().read(insn.getFPR2()).getValue())));
	}),
	ConvertIntToFloat ("cvt.s.w", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), new Data(Float.floatToIntBits(
				(float)prog.getFPRegFile().read(insn.getFPR2()).getValue()), 
				Data.DataType.Float));
	}),
	ConvertDoubleToInt ("cvt.w.d", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), new Data((int)
				prog.getFPRegFile().readDouble(insn.getFPR2())));
	}),
	ConvertIntToDouble ("cvt.d.w", (insn, prog) -> {
		prog.getFPRegFile().writeDouble(insn.getFPR1(), 
				(double)prog.getFPRegFile().read(insn.getFPR2()).getValue());
	}),
	
	ConvertDoubleToFloat ("cvt.s.d", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), new Data(Float.floatToIntBits(
				new Double(prog.getFPRegFile().readDouble(insn.getFPR2())).floatValue()), 
				Data.DataType.Float));
	}),
	ConvertFloatToDouble ("cvt.d.s", (insn, prog) -> {
		prog.getFPRegFile().writeDouble(insn.getFPR1(), 
				new Float(Float.intBitsToFloat(prog.getFPRegFile().read(
						insn.getFPR2()).getValue())).doubleValue());
	}),
	
	// Load and Store Commands
	LoadFloat ("l.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), 
				prog.getMem().loadWord(insn.getImmed() + 
				insn.getImmed() + ((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue())));
	}),	
	StoreFloat ("s.s", (insn, prog) -> {
		prog.getMem().storeWord(prog.getFPRegFile().read(insn.getFPR1()), 
				insn.getImmed() + ((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue()));
	}),
	LoadDouble ("l.d", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), 
				prog.getMem().loadWord(insn.getImmed() + 
				insn.getImmed() + ((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue())));
		prog.getFPRegFile().write(insn.getFPR1().getDoubleUpper(), 
				prog.getMem().loadWord(4 + insn.getImmed() + 
				insn.getImmed() + ((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue())));
	}),	
	StoreDouble ("s.d", (insn, prog) -> {
		prog.getMem().storeWord(prog.getFPRegFile().read(insn.getFPR1()), 
				insn.getImmed() + ((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue()));
		prog.getMem().storeWord(prog.getFPRegFile().read(insn.getFPR1().getDoubleUpper()), 
				4 + insn.getImmed() + ((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue()));
	}),
	
	// System Calls
	Syscall ("syscall", (insn, prog) -> {
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
			String out = TextParser.dataArrayToString(prog.getMem().loadArray(address));
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
	
	private static Data convertToData(float value) {
		return new Data(Float.floatToIntBits(value), Data.DataType.Float);
	}
	
	private static float convertToFloat(Data value) {
		if(!value.getDataType().equals(Data.DataType.Float))
			throw new RuntimeException("Data Not a Float: "
					+ value.getDataType() + " " + value.toString());
		return Float.intBitsToFloat(value.getValue());
	}
	
}

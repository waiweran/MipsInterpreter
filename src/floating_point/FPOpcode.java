package floating_point;
import java.util.function.BiConsumer;

import backend.program.Program;
import backend.state.Data;

public enum FPOpcode {
	
	// Single Precision Floating Point Commands
	Add ("add.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), convertToData(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())) + 
				convertToFloat(prog.getFPRegFile().read(insn.getFPR3()))));
	}),
	Subtract ("sub.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), convertToData(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())) - 
				convertToFloat(prog.getFPRegFile().read(insn.getFPR3()))));
	}),
	Multiply ("mult.s", (insn, prog) -> {
		prog.getFPRegFile().write(insn.getFPR1(), convertToData(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())) * 
				convertToFloat(prog.getFPRegFile().read(insn.getFPR3()))));
	}),	
	Divide ("div.s", (insn, prog) -> {
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

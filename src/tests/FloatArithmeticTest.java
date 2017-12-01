package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import backend.program.FPRegister;
import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.float_s.AddFloat;
import backend.program.opcode.float_s.CompareEqual;
import backend.program.opcode.float_s.CompareGreaterEquals;
import backend.program.opcode.float_s.CompareGreaterThan;
import backend.program.opcode.float_s.CompareLessEquals;
import backend.program.opcode.float_s.CompareLessThan;
import backend.program.opcode.float_s.CompareNotEqual;
import backend.program.opcode.float_s.DivideFloat;
import backend.program.opcode.float_s.MoveFloat;
import backend.program.opcode.float_s.MultiplyFloat;
import backend.program.opcode.float_s.SubtractFloat;
import backend.state.Data;

public class FloatArithmeticTest {
	
	private Program program;
	
	@Before
	public void setUp() {
		program = new Program(System.in, System.out);
	}

	@Test
	public void testAdd() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		preloadReg(FPRegister.f1, (float)6.6332E9);
		Instruction insn = new Instruction(new AddFloat(), null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f1, 0, "");
		insn.execute(program);
		assertEquals((float)(1.123E8+6.6332E9), checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testSub() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		preloadReg(FPRegister.f1, (float)6.6332E9);
		Instruction insn = new Instruction(new SubtractFloat(), null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f1, 0, "");
		insn.execute(program);
		assertEquals((float)(1.123E8-6.6332E9), checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testMult() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		preloadReg(FPRegister.f1, (float)6.6332E9);
		Instruction insn = new Instruction(new MultiplyFloat(), null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f1, 0, "");
		insn.execute(program);
		assertEquals((float)(1.123E8)*(float)(6.6332E9), checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testDiv() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		preloadReg(FPRegister.f1, (float)6.6332E9);
		Instruction insn = new Instruction(new DivideFloat(), null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f1, 0, "");
		insn.execute(program);
		assertEquals((float)(1.123E8/6.6332E9), checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testComp() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		preloadReg(FPRegister.f1, (float)6.6332E9);
		Instruction insn = new Instruction(new CompareEqual(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareNotEqual(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessThan(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessEquals(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterThan(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterEquals(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		preloadReg(FPRegister.f0, (float)1.123E-3);
		preloadReg(FPRegister.f1, (float)1.123E-3);
		insn = new Instruction(new CompareEqual(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareNotEqual(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessThan(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessEquals(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterThan(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterEquals(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		preloadReg(FPRegister.f0, (float)1.248E-3);
		preloadReg(FPRegister.f1, (float)1.123E-3);
		insn = new Instruction(new CompareEqual(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareNotEqual(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessThan(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessEquals(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterThan(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterEquals(), null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
	}

	@Test
	public void testMove() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		Instruction insn = new Instruction(new MoveFloat(), null, null, null, FPRegister.f2, FPRegister.f0, null, 0, "");
		insn.execute(program);
		assertEquals((float)(1.123E8), checkReg(FPRegister.f2), 0);
	}
	
	private void preloadReg(FPRegister reg, float val) {
		program.getFPRegFile().write(reg, new Data(Float.floatToIntBits(val), Data.DataType.Float));
	}
	
	private float checkReg(FPRegister reg) {
		return Float.intBitsToFloat(program.getFPRegFile().read(reg).getValue());
	}
	

}

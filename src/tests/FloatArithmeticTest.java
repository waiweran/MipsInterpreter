package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import backend.program.FPRegister;
import backend.program.Instruction;
import backend.program.Opcode;
import backend.program.Program;
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
		Instruction insn = new Instruction(Opcode.AddFloat, null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f1, 0, "");
		insn.execute(program);
		assertEquals((float)(1.123E8+6.6332E9), checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testSub() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		preloadReg(FPRegister.f1, (float)6.6332E9);
		Instruction insn = new Instruction(Opcode.SubtractFloat, null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f1, 0, "");
		insn.execute(program);
		assertEquals((float)(1.123E8-6.6332E9), checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testMult() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		preloadReg(FPRegister.f1, (float)6.6332E9);
		Instruction insn = new Instruction(Opcode.MultiplyFloat, null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f1, 0, "");
		insn.execute(program);
		assertEquals((float)(1.123E8)*(float)(6.6332E9), checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testDiv() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		preloadReg(FPRegister.f1, (float)6.6332E9);
		Instruction insn = new Instruction(Opcode.DivideFloat, null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f1, 0, "");
		insn.execute(program);
		assertEquals((float)(1.123E8/6.6332E9), checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testComp() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		preloadReg(FPRegister.f1, (float)6.6332E9);
		Instruction insn = new Instruction(Opcode.CompareEqual, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareNotEqual, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessThan, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessEquals, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterThan, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterEquals, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		preloadReg(FPRegister.f0, (float)1.123E-3);
		preloadReg(FPRegister.f1, (float)1.123E-3);
		insn = new Instruction(Opcode.CompareEqual, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareNotEqual, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessThan, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessEquals, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterThan, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterEquals, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		preloadReg(FPRegister.f0, (float)1.248E-3);
		preloadReg(FPRegister.f1, (float)1.123E-3);
		insn = new Instruction(Opcode.CompareEqual, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareNotEqual, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessThan, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessEquals, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterThan, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterEquals, null, null, null, FPRegister.f0, FPRegister.f1, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
	}

	@Test
	public void testMove() {
		preloadReg(FPRegister.f0, (float)1.123E8);
		Instruction insn = new Instruction(Opcode.MoveFloat, null, null, null, FPRegister.f2, FPRegister.f0, null, 0, "");
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

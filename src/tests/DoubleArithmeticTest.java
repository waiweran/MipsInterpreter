package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import backend.program.FPRegister;
import backend.program.Instruction;
import backend.program.Opcode;
import backend.program.Program;

public class DoubleArithmeticTest {
	
	private Program program;
	
	@Before
	public void setUp() {
		program = new Program(System.in, System.out);
	}

	@Test
	public void testAdd() {
		preloadReg(FPRegister.f0, 1.123E2);
		preloadReg(FPRegister.f4, 6.6332E2);
		Instruction insn = new Instruction(Opcode.AddDouble, null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f4, 0, "");
		insn.execute(program);
		assertEquals(1.123E2+6.6332E2, checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testSub() {
		preloadReg(FPRegister.f0, 1.123E3);
		preloadReg(FPRegister.f4, 6.6332E4);
		Instruction insn = new Instruction(Opcode.SubtractDouble, null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f4, 0, "");
		insn.execute(program);
		assertEquals(1.123E3-6.6332E4, checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testMult() {
		preloadReg(FPRegister.f0, 1.123E3);
		preloadReg(FPRegister.f4, 6.6332E2);
		Instruction insn = new Instruction(Opcode.MultiplyDouble, null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f4, 0, "");
		insn.execute(program);
		assertEquals(1.123E3*6.6332E2, checkReg(FPRegister.f2), 1);
	}
	
	@Test
	public void testDiv() {
		preloadReg(FPRegister.f0, 1.123E8);
		preloadReg(FPRegister.f4, 6.6332E9);
		Instruction insn = new Instruction(Opcode.DivideDouble, null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f4, 0, "");
		insn.execute(program);
		assertEquals(1.123E8/6.6332E9, checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testComp() {
		preloadReg(FPRegister.f0, 1.123E8);
		preloadReg(FPRegister.f4, 6.6332E9);
		Instruction insn = new Instruction(Opcode.CompareEqualDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareNotEqualDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessThanDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessEqualsDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterThanDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterEqualsDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		preloadReg(FPRegister.f0, 1.123E-3);
		preloadReg(FPRegister.f4, 1.123E-3);
		insn = new Instruction(Opcode.CompareEqualDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareNotEqualDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessThanDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessEqualsDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterThanDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterEqualsDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		preloadReg(FPRegister.f0, 1.248E-3);
		preloadReg(FPRegister.f4, 1.123E-3);
		insn = new Instruction(Opcode.CompareEqualDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareNotEqualDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessThanDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareLessEqualsDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterThanDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(Opcode.CompareGreaterEqualsDouble, null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
	}

	@Test
	public void testMove() {
		preloadReg(FPRegister.f0, 1.123E3);
		Instruction insn = new Instruction(Opcode.MoveDouble, null, null, null, FPRegister.f2, FPRegister.f0, null, 0, "");
		insn.execute(program);
		assertEquals(1.123E3, checkReg(FPRegister.f2), 0);
	}
	
	private void preloadReg(FPRegister reg, double val) {
		program.getFPRegFile().writeDouble(reg, val);
	}
	
	private double checkReg(FPRegister reg) {
		return program.getFPRegFile().readDouble(reg);
	}
	

}

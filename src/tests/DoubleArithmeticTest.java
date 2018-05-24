package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import backend.program.FPRegister;
import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.float_branch.CompareEqualDouble;
import backend.program.opcode.float_branch.CompareGreaterEqualsDouble;
import backend.program.opcode.float_branch.CompareGreaterThanDouble;
import backend.program.opcode.float_branch.CompareLessEqualsDouble;
import backend.program.opcode.float_branch.CompareLessThanDouble;
import backend.program.opcode.float_branch.CompareNotEqualDouble;
import backend.program.opcode.float_d.AddDouble;
import backend.program.opcode.float_d.DivideDouble;
import backend.program.opcode.float_d.MoveDouble;
import backend.program.opcode.float_d.MultiplyDouble;
import backend.program.opcode.float_d.SubtractDouble;

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
		Instruction insn = new Instruction(new AddDouble(), null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f4, 0, "");
		insn.execute(program);
		assertEquals(7.7562E2, checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testSub() {
		preloadReg(FPRegister.f0, 1.123E3);
		preloadReg(FPRegister.f4, 6.6332E4);
		Instruction insn = new Instruction(new SubtractDouble(), null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f4, 0, "");
		insn.execute(program);
		assertEquals(1.123E3-6.6332E4, checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testMult() {
		preloadReg(FPRegister.f0, 1.123E3);
		preloadReg(FPRegister.f4, 6.6332E2);
		Instruction insn = new Instruction(new MultiplyDouble(), null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f4, 0, "");
		insn.execute(program);
		assertEquals(1.123E3*6.6332E2, checkReg(FPRegister.f2), 1);
	}
	
	@Test
	public void testDiv() {
		preloadReg(FPRegister.f0, 1.123E8);
		preloadReg(FPRegister.f4, 6.6332E7);
		Instruction insn = new Instruction(new DivideDouble(), null, null, null, FPRegister.f2, FPRegister.f0, FPRegister.f4, 0, "");
		insn.execute(program);
		assertEquals(1.123E8/6.6332E7, checkReg(FPRegister.f2), 0.01);
	}
	
	@Test
	public void testComp() {
		preloadReg(FPRegister.f0, 1.123E8);
		preloadReg(FPRegister.f4, 6.6332E9);
		Instruction insn = new Instruction(new CompareEqualDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareNotEqualDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessThanDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessEqualsDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterThanDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterEqualsDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		preloadReg(FPRegister.f0, 1.123E-3);
		preloadReg(FPRegister.f4, 1.123E-3);
		insn = new Instruction(new CompareEqualDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareNotEqualDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessThanDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessEqualsDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterThanDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterEqualsDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		preloadReg(FPRegister.f0, 1.248E-3);
		preloadReg(FPRegister.f4, 1.123E-3);
		insn = new Instruction(new CompareEqualDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareNotEqualDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessThanDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareLessEqualsDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertFalse(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterThanDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
		insn = new Instruction(new CompareGreaterEqualsDouble(), null, null, null, FPRegister.f0, FPRegister.f4, null, 0, "");
		insn.execute(program);
		assertTrue(program.getFPRegFile().readCond());
	}

	@Test
	public void testMove() {
		preloadReg(FPRegister.f0, 1.123E3);
		Instruction insn = new Instruction(new MoveDouble(), null, null, null, FPRegister.f2, FPRegister.f0, null, 0, "");
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

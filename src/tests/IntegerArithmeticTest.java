package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import backend.program.Instruction;
import backend.program.Opcode;
import backend.program.Program;
import backend.program.Register;
import backend.state.Data;

public class IntegerArithmeticTest {
	
	private Program program;
	
	@Before
	public void setUp() {
		program = new Program(System.in, System.out);
	}

	@Test
	public void testAdd() {
		preloadReg(Register.a0, 45640);
		preloadReg(Register.a1, 6633224);
		Instruction insn = new Instruction(Opcode.Add, Register.v0, Register.a0, Register.a1, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(6633224+45640, checkReg(Register.v0));
	}
	
	@Test
	public void testAddI() {
		preloadReg(Register.a0, 45640);
		Instruction insn = new Instruction(Opcode.AddImmediate, Register.v0, Register.a0, null, null, null, null, 55343, "");
		insn.execute(program);
		assertEquals(55343+45640, checkReg(Register.v0));
	}
	
	@Test
	public void testAnd() {
		preloadReg(Register.a0, 45640);
		preloadReg(Register.a1, 6633224);
		Instruction insn = new Instruction(Opcode.And, Register.v0, Register.a0, Register.a1, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(6633224&45640, checkReg(Register.v0));
	}
	
	@Test
	public void testAndI() {
		preloadReg(Register.a0, 45640);
		Instruction insn = new Instruction(Opcode.AndImmediate, Register.v0, Register.a0, null, null, null, null, 55343, "");
		insn.execute(program);
		assertEquals(55343&45640, checkReg(Register.v0));
	}
	
	private void preloadReg(Register reg, int val) {
		program.getRegFile().write(reg, new Data(val));
	}
	
	private int checkReg(Register reg) {
		return program.getRegFile().read(reg).getValue();
	}
	

}

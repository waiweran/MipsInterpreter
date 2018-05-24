package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.Register;
import backend.program.opcode.arithmetic.Add;
import backend.program.opcode.arithmetic.AddImmediate;
import backend.program.opcode.arithmetic.LoadUpperImmediate;
import backend.program.opcode.arithmetic.Subtract;
import backend.program.opcode.logic.And;
import backend.program.opcode.logic.AndImmediate;
import backend.program.opcode.logic.ExclusiveOr;
import backend.program.opcode.logic.ExclusiveOrImmediate;
import backend.program.opcode.logic.Nor;
import backend.program.opcode.logic.Or;
import backend.program.opcode.logic.OrImmediate;
import backend.program.opcode.multdiv.Divide;
import backend.program.opcode.multdiv.Multiply;
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
		Instruction insn = new Instruction(new Add(), Register.v0, Register.a0, Register.a1, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(6633224+45640, checkReg(Register.v0));
	}
	
	@Test
	public void testAddI() {
		preloadReg(Register.a0, 45640);
		Instruction insn = new Instruction(new AddImmediate(), Register.v0, Register.a0, null, null, null, null, 55343, "");
		insn.execute(program);
		assertEquals(55343+45640, checkReg(Register.v0));
	}
	
	@Test
	public void testAnd() {
		preloadReg(Register.a0, 45640);
		preloadReg(Register.a1, 6633224);
		Instruction insn = new Instruction(new And(), Register.v0, Register.a0, Register.a1, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(6633224&45640, checkReg(Register.v0));
	}
	
	@Test
	public void testAndI() {
		preloadReg(Register.a0, 45640);
		Instruction insn = new Instruction(new AndImmediate(), Register.v0, Register.a0, null, null, null, null, 55343, "");
		insn.execute(program);
		assertEquals(55343&45640, checkReg(Register.v0));
	}
	
	@Test
	public void testDiv() {
		preloadReg(Register.a0, 5533245);
		preloadReg(Register.a1, 383224);
		Instruction insn = new Instruction(new Divide(), Register.a0, Register.a1, null, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(5533245/383224, program.getRegFile().readLO().getValue());
		assertEquals(5533245%383224, program.getRegFile().readHI().getValue());
	}
	
	@Test
	public void testLui() {
		Instruction insn = new Instruction(new LoadUpperImmediate(), Register.a0, null, null, null, null, null, 5543, "");
		insn.execute(program);
		assertEquals(5543<<16, checkReg(Register.a0));
	}
	
	@Test
	public void testMult() {
		preloadReg(Register.a0, 553245);
		preloadReg(Register.a1, 383224);
		Instruction insn = new Instruction(new Multiply(), Register.a0, Register.a1, null, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(553245*383224, program.getRegFile().readLO().getValue());	
	}
	
	@Test
	public void testNor() {
		preloadReg(Register.a0, 556543);
		preloadReg(Register.a1, 118754);
		Instruction insn = new Instruction(new Nor(), Register.v0, Register.a0, Register.a1, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(~(556543|118754), checkReg(Register.v0));
	}
	
	@Test
	public void testOr() {
		preloadReg(Register.a0, 45640);
		preloadReg(Register.a1, 6633224);
		Instruction insn = new Instruction(new Or(), Register.v0, Register.a0, Register.a1, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(6633224|45640, checkReg(Register.v0));
	}
	
	@Test
	public void testOrI() {
		preloadReg(Register.a0, 45640);
		Instruction insn = new Instruction(new OrImmediate(), Register.v0, Register.a0, null, null, null, null, 55343, "");
		insn.execute(program);
		assertEquals(55343|45640, checkReg(Register.v0));
	}
	
	@Test
	public void testSub() {
		preloadReg(Register.a0, 556543);
		preloadReg(Register.a1, 118754);
		Instruction insn = new Instruction(new Subtract(), Register.v0, Register.a0, Register.a1, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(556543-118754, checkReg(Register.v0));
	}
	
	@Test
	public void testXOr() {
		preloadReg(Register.a0, 45640);
		preloadReg(Register.a1, 6633224);
		Instruction insn = new Instruction(new ExclusiveOr(), Register.v0, Register.a0, Register.a1, null, null, null, 0, "");
		insn.execute(program);
		assertEquals(6633224^45640, checkReg(Register.v0));
	}
	
	@Test
	public void testXOrI() {
		preloadReg(Register.a0, 45640);
		Instruction insn = new Instruction(new ExclusiveOrImmediate(), Register.v0, Register.a0, null, null, null, null, 55343, "");
		insn.execute(program);
		assertEquals(55343^45640, checkReg(Register.v0));
	}
	
	private void preloadReg(Register reg, int val) {
		program.getRegFile().write(reg, new Data(val));
	}
	
	private int checkReg(Register reg) {
		return program.getRegFile().read(reg).getValue();
	}
	

}

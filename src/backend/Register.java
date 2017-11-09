package backend;

public enum Register {
	
	zero (0, "$zero"),
	v0 (2, "$v0"),
	v1 (3, "$v1"),
	a0 (4, "$a0"),
	a1 (5, "$a1"),
	a2 (6, "$a2"),
	a3 (7, "$a3"),
	t0 (8, "$t0"),
	t1 (9, "$t1"),
	t2 (10, "$t2"),
	t3 (11, "$t3"),
	t4 (12, "$t4"),
	t5 (13, "$t5"),
	t6 (14, "$t6"),
	t7 (15, "$t7"),
	s0 (16, "$s0"),
	s1 (17, "$s1"),
	s2 (18, "$s2"),
	s3 (19, "$s3"),
	s4 (20, "$s4"),
	s5 (21, "$s5"),
	s6 (22, "$s6"),
	s7 (23, "$s7"),
	t8 (24, "$t8"),
	t9 (25, "$t9"),
	sp (29, "$sp"),
	ra (31, "$ra");
	
	private int regNum;
	private String regName;
	
	private Register(int num, String name) {
		regNum = num;
		regName = name;
	}
	
	public static Register findRegister(String name) {
		for(Register reg : Register.values()) {
			if(reg.regName.equalsIgnoreCase(name) 
					|| ("$" + reg.regNum).equalsIgnoreCase(name)
					|| ("$r" + reg.regNum).equalsIgnoreCase(name)) return reg;
		}
		throw new RuntimeException("Invalid or Reserved Register, Name: " + name);
	}
	
	public int getRegisterNumber() {
		return regNum;
	}
	
	public String getRegisterName() {
		return regName;
	}
}

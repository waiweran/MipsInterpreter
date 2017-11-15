package backend.state;

public enum FPRegister {
	
	f0 (0),
	f1 (1),
	f2 (2),
	f3 (3),
	f4 (4),
	f5 (5),
	f6 (6),
	f7 (7),
	f8 (8),
	f9 (9),
	f10 (10),
	f11 (11),
	f12 (12),
	f13 (13),
	f14 (14),
	f15 (15),
	f16 (16),
	f17 (17),
	f18 (18),
	f19 (19),
	f20 (20),
	f21 (21),
	f22 (22),
	f23 (23),
	f24 (24),
	f25 (25),
	f26 (26),
	f27 (27),
	f28 (28),
	f29 (29),
	f30 (30),
	f31 (31);
	
	private int regNum;
	
	private FPRegister(int num) {
		regNum = num;
	}
	
	public static FPRegister findRegister(String name) {
		for(FPRegister reg : FPRegister.values()) {
			if(("$f" + reg.regNum).equalsIgnoreCase(name)) return reg;
		}
		throw new RuntimeException("Invalid FP Register, Name: " + name);
	}
	
	public int getRegisterNumber() {
		return regNum;
	}
	
	public String getRegisterName() {
		return "$f" + regNum;
	}
	
	public FPRegister getDoubleUpper() {
		if(regNum % 2 != 0) 
			throw new RuntimeException("FP Register " + getRegisterName()
					+ "Cannot " + "be used for doubles");
		for(FPRegister reg : FPRegister.values()) {
			if(reg.regNum == regNum + 1) return reg;
		}
		throw new RuntimeException("Requested register has no double pairing");
	}
}
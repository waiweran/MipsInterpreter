package floating_point;

public enum FPRegister {
	
	f0 (0);
	// TODO add more
	
	private int regNum;
	
	private FPRegister(int num) {
		regNum = num;
	}
	
	public static FPRegister findRegister(String name) {
		for(FPRegister reg : FPRegister.values()) {
			if(("$f" + reg.regNum).equalsIgnoreCase(name)) return reg;
		}
		throw new RuntimeException("Invalid or Reserved Register, Name: " + name);
	}
	
	public int getRegisterNumber() {
		return regNum;
	}
	
	public String getRegisterName() {
		return "$f" + regNum;
	}
}

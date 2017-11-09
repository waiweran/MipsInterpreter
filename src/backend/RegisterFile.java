package backend;
import java.util.HashMap;
import java.util.Map;

public class RegisterFile {
	
	private Map<Register, Integer> vals;
	private int lo, hi;
	
	public RegisterFile() {
		vals = new HashMap<>();
		for(Register r : Register.values()) {
			vals.put(r, 0);
		}
		vals.put(Register.sp, Integer.MAX_VALUE + 1);
	}
	
	public void write(Register write, int value) {
		if(!write.equals(Register.zero)) {
			vals.put(write, value);
		}
	}
	
	public int read(Register read) {
		return vals.get(read);
	}
	
	public void writeLO(int value) {
		lo = value;
	}
	
	public int readLO() {
		return lo;
	}	
	
	public void writeHI(int value) {
		hi = value;
	}
	
	public int readHI() {
		return hi;
	}

}

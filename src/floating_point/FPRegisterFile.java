package floating_point;
import java.util.HashMap;
import java.util.Map;

public class FPRegisterFile {
	
	private Map<FPRegister, FPData> vals;
	private boolean cond;
	
	public FPRegisterFile() {
		vals = new HashMap<>();
		for(FPRegister r : FPRegister.values()) {
			vals.put(r, new FPData());
		}
	}
	
	public void write(FPRegister write, FPData value) {
		vals.put(write, value);
	}
	
	public FPData read(FPRegister read) {
		return vals.get(read);
	}
	
	public void writeCond(boolean value) {
		cond = value;
	}
	
	public boolean readCond() {
		return cond;
	}

}

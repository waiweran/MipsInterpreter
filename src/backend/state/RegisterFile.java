package backend.state;
import java.util.HashMap;
import java.util.Map;

import backend.program.Register;

public class RegisterFile {
	
	private Map<Register, Data> vals;
	private Data lo, hi;
	
	public RegisterFile() {
		vals = new HashMap<>();
		lo = new Data();
		hi = new Data();
		for(Register r : Register.values()) {
			vals.put(r, new Data());
		}
		vals.put(Register.sp, new Data(Integer.MAX_VALUE + 1, 
				Data.DataType.Address, Data.Permissions.Write));
	}
	
	public void write(Register write, Data value) {
		if(!write.equals(Register.zero)) {
			vals.put(write, value);
		}
	}
	
	public Data read(Register read) {
		return vals.get(read);
	}
	
	public void writeLO(Data value) {
		lo = value;
	}
	
	public Data readLO() {
		return lo;
	}	
	
	public void writeHI(Data value) {
		hi = value;
	}
	
	public Data readHI() {
		return hi;
	}

}

package backend.state;
import java.util.HashMap;
import java.util.Map;

import backend.state.Data.DataType;

public class FPRegisterFile {
	
	private Map<FPRegister, Data> vals;
	private boolean cond;
	
	public FPRegisterFile() {
		vals = new HashMap<>();
		for(FPRegister r : FPRegister.values()) {
			vals.put(r, new Data());
		}
	}
	
	public void write(FPRegister write, Data value) {
		vals.put(write, value);
	}
	
	public Data read(FPRegister read) {
		return vals.get(read);
	}
	
	public void writeDouble(FPRegister write, double value) {
		long bits = Double.doubleToLongBits(value);
		int upper = (int)(bits >>> 32);
		int lower = (int)(Long.rotateLeft(bits, 32) >>> 32);
		vals.put(write, new Data(lower, DataType.Double_L));
		vals.put(write.getDoubleUpper(), new Data(upper, DataType.Double_H));
	}
	
	public double readDouble(FPRegister read) {
		long lower = vals.get(read).getValue();
		long upper = vals.get(read.getDoubleUpper()).getValue();
		return Double.longBitsToDouble(lower + (upper << 32));
	}
	
	public void writeCond(boolean value) {
		cond = value;
	}
	
	public boolean readCond() {
		return cond;
	}

}

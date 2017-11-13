package backend.state;

import java.util.ArrayList;
import java.util.List;

import backend.TextParser;

public class Data {

	public enum DataType {
		String,
		Integer,
		Byte,
		Address,
		J_Target,
		Space;
	}
	
	public enum Permissions {
		Read_Only,
		Write;
	}
	
	private int val;
	private DataType typ;
	private Permissions per;
	
	public Data() {
		this(0, DataType.Space);
	}
	
	public Data(int value) {
		this(value, DataType.Integer);
	}
	
	public Data(int value, DataType type) {
		this(value, type, Permissions.Write);
	}
	
	public Data(int value, DataType type, Permissions permission) {
		val = value;
		typ = type;
		per = permission;
	}
	
	public int getValue() {
		return (int)val;
	}
	
	public DataType getDataType() {
		return typ;
	}
	
	public Permissions getPermissions() {
		return per;
	}
	
	@Override
	public int hashCode() {
		return new Integer(val).hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Data && this.val == ((Data) other).val;
	}
	
	@Override
	public String toString() {

		if(typ.equals(DataType.String)) {
			List<Data> string = new ArrayList<Data>();
			string.add(this);
			string.add(new Data());
			return TextParser.dataArrayToString(string).replaceAll("\\n", "\\\\n");
		}
		if(typ.equals(DataType.Address)) {
			return Integer.toHexString(val);
		}
		return Integer.toString(val);
	}
	
}

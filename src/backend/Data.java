package backend;

import java.util.ArrayList;
import java.util.List;

public class Data {

	public enum DataType {
		String,
		Integer,
		Address,
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
		return val;
	}
	
	public DataType getDataType() {
		return typ;
	}
	
	public Permissions getPermissions() {
		return per;
	}
	
	@Override
	public String toString() {

		if(typ.equals(DataType.String)) {
			List<Data> string = new ArrayList<Data>();
			string.add(this);
			string.add(new Data());
			return TextParser.dataArrayToString(string).replaceAll("\\n", "\\\\n");
		}
		return Integer.toString(val);
	}
	
}

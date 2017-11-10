package backend;

public class Data {

	public enum DataType {
		String_Head,
		String,
		String_Tail,
		Integer,
		Float, 
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
		return Integer.toString(val);
	}
	
}

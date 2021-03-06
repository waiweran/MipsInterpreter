package backend.state;

/**
 * Stores a single word-length data value in register files or memory.
 * Stores information regarding the data type held, for debugging.
 * @author Nathaniel
 * @version 11-10-2017
 */
public class Data {

	/**
	 * Enum specifying the type of data stored.
	 * @author Nathaniel
	 */
	public enum DataType {
		String,
		Integer,
		Byte,
		Address,
		J_Target,
		Float,
		Double_L,
		Double_H,
		Space,
		Unknown;
	}
	
	private int val;
	private DataType typ;
	
	/**
	 * Initializes a new Data storing nothing (Space).
	 */
	public Data() {
		this(0, DataType.Space);
	}
	
	/**
	 * Initializes a new Data with the specified value.
	 * Data type is Integer.
	 * @param value the value to store.
	 */
	public Data(int value) {
		this(value, DataType.Integer);
	}
	
	/**
	 * Initializes a new Data.
	 * @param value the value to store.
	 * @param type the data type stored.
	 */
	public Data(int value, DataType type) {
		val = value;
		typ = type;
	}
	
	/**
	 * @return the value stored.
	 */
	public int getValue() {
		return val;
	}
	
	/**
	 * @return the data type of the stored value.
	 */
	public DataType getDataType() {
		return typ;
	}
	
	/**
	 * Combines two Data to produce the expected result type.
	 * @param other The other Data being combined.
	 * @return The expected type of the result.
	 */
	public DataType combineType(Data other) {
		return combineType(other.typ);
	}
	
	/**
	 * Combines this Data with another type to produce the expected result type.
	 * @param other the type of the other Data being combined.
	 * @return The expected type of the result.
	 */
	public DataType combineType(DataType other) {
		if(typ.equals(other)) return typ;
		if(typ.equals(DataType.Space)) return other;
		if(other.equals(DataType.Space)) return typ;
		if(typ.equals(DataType.Unknown)) return other;
		if(other.equals(DataType.Unknown)) return typ;
		if(typ.equals(DataType.Byte)) return other;
		if(other.equals(DataType.Byte)) return typ;
		if(typ.equals(DataType.Integer)) return other;
		if(other.equals(DataType.Integer)) return typ;
		if(typ.equals(DataType.Address) || other.equals(DataType.Address)) 
			return DataType.Address;
		
		return DataType.Unknown;
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
			return toCharString();
		}
		if(typ.equals(DataType.Float)) {
			return toFloatString();
		}
		if(typ.equals(DataType.Address)
				|| typ.equals(DataType.Double_H)
				|| typ.equals(DataType.Double_L)
				|| typ.equals(DataType.Unknown)) {
			return toHex();
		}
		return toDecimal();
	}

	/**
	 * @return String representing the data as a string.
	 */
	public String toCharString() {
		StringBuilder output = new StringBuilder();
		for(int j = 3; j >= 0; j--) {
			char nextChar = (char) ((getValue() >> 8*j) & 255);
			if(nextChar == (char)10) output.append("\\n");
			else output.append(nextChar);
		}
		return output.toString();
	}
	
	/**
	 * @return String representing the data as a hexadecimal value.
	 */
	public String toHex() {
		String output = Integer.toHexString(val);
		return "00000000".substring(output.length()) + output.toUpperCase();
	}
	
	/**
	 * @return String representing the data as a base-10 value.
	 */
	public String toDecimal() {
		return Integer.toString(val);
	}
	
	/**
	 * @return String representing the data as a IEEE standard 32-bit floating point number.
	 */
	public String toFloatString() {
		return Float.toString(Float.intBitsToFloat(val));
	}
	
}

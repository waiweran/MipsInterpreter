package terminal;

/**
 * Represents a command line flag.
 * @author Nathaniel
 * @version 01-12-2018
 */
public class Flag {

	private String n;
	private char abbr;
	private boolean used;
	private String val;
	
	/**
	 * Initializes a flag with the given name and no abbreviation.
	 * @param name
	 */
	public Flag(String name) {
		n = name;
		abbr = 0;
		used = false;
	}
	
	/**
	 * Initializes a flag with the given name and single-character abbreviation.
	 * @param name
	 * @param abbreviation
	 */
	public Flag(String name, char abbreviation) {
		n = name;
		abbr = abbreviation;
		used = false;
	}
	
	/**
	 * Marks the flag as used (present in the arguments).
	 */
	void setUsed() {
		used = true;
	}
	
	/**
	 * Sets the string value assigned to a flag
	 * @param value
	 */
	void setValue(String value) {
		val = value;
	}
	
	/**
	 * @return the name of the flag
	 */
	public String getName() {
		return n;
	}
	
	/**
	 * @return the 1-letter abbreviation of the flag.
	 */
	public char getAbbreviation() {
		return abbr;
	}
	
	/**
	 * @return true if the flag was used, false otherwise.
	 */
	public boolean isUsed() {
		return used;
	}
	
	/**
	 * @return a value assigned to the flag with an = operator.
	 */
	public String getValue() {
		return val;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Flag && ((Flag)o).n.equals(n);
	}

}

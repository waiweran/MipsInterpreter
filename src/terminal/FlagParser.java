package terminal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parses command line flags.
 * @author Nathaniel
 * @version 01-12-2018
 */
public class FlagParser {
	
	private List<Flag> flags;
	private List<String> arguments;

	/**
	 * Initializes flag parser and parses flags in the command line arguments.
	 * @param args arguments to parse for flags.
	 */
	public FlagParser() {
		flags = new ArrayList<>();
		arguments = new ArrayList<>();
	}

	/**
	 * Checks for flags in the given command line arguments
	 * @param args
	 */
	public void parseFlags(String[] args) {
		for(String s : args) {
			if(s.startsWith("--")) {
				boolean realFlag = false; 
				for(Flag f : flags) {
					String fname = s.substring(2);
					String fval = null;
					if(fname.indexOf("=") > 0) {
						fval = fname.substring(fname.indexOf("=") + 1, fname.length());
						fname = fname.substring(0, fname.indexOf("="));
					}
					if(fname.equals(f.getName())) {
						f.setUsed();
						f.setValue(fval);
						realFlag = true;
						break;
					}
				}
				if(!realFlag) throw new RuntimeException("Invalid Flag: " + s.substring(2));
			}
			else if(s.startsWith("-")) {
				for(int i = 1; i < s.length(); i++) {
					boolean realFlag = false;
					for(Flag f : flags) {
						if(s.charAt(i) == f.getAbbreviation()) {
							f.setUsed();
							realFlag = true;
							break;
						}
					}
					if(!realFlag) throw new RuntimeException("Invalid Flag: " + s.charAt(i));
				}
			}
			else {
				arguments.add(s);
			}
		}
	}
	
	/**
	 * Adds a flag to the list of flags to check for
	 * @param flag
	 */
	public void addFlag(Flag flag) {
		flags.add(flag);
	}
	
	/**
	 * Get a flag by name.
	 * @param name 
	 * @return the flag.
	 */
	public Flag getFlag(String name) {
		for(Flag f : flags) {
			if(f.getName().equals(name)) return f;
		}
		return null;
	}
	
	/**
	 * @return a list of non-flag arguments.
	 */
	public List<String> getArgs() {
		return Collections.unmodifiableList(arguments);
	}

}

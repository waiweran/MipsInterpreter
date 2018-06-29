package terminal;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses command line flags.
 * @author Nathaniel
 * @version 01-12-2018
 */
public class FlagParser {
	
	/**
	 * Represents a command line flag.
	 * @author Nathaniel
	 * @version 01-12-2018
	 */
	private class Flag {

		private String n;
		private char abbr;
		
		/**
		 * Initializes a flag with the given name and single-character abbreviation.
		 * @param name
		 * @param abbreviation
		 */
		private Flag(String name, char abbreviation) {
			n = name;
			abbr = abbreviation;
		}
		
		@Override
		public boolean equals(Object o) {
			return o instanceof Flag && ((Flag)o).n.equals(n);
		}

	}
	
	private List<Flag> possibleFlags, presentFlags;

	/**
	 * Initializes flag parser and parses flags in the command line arguments.
	 * @param args arguments to parse for flags.
	 */
	public FlagParser(String[] args) {
		possibleFlags = new ArrayList<>();
		listFlags();
		presentFlags = new ArrayList<>();
		parseFlags(args);
	}

	/**
	 * Checks for flags in the given command line arguments
	 * @param args
	 */
	private void parseFlags(String[] args) {
		for(String s : args) {
			if(s.startsWith("-")) {
				for(int i = 1; i < s.length(); i++) {
					for(Flag f : possibleFlags) {
						if(s.charAt(i) == f.abbr) {
							presentFlags.add(f);
						}
					}
				}
			}
			if(s.startsWith("--")) {
				for(Flag f : possibleFlags) {
					if(s.substring(2) == f.n) {
						presentFlags.add(f);
					}
				}
			}
		}
	}

	/**
	 * Lists possible flags to check command line for.
	 */
	private void listFlags() {
		possibleFlags.add(new Flag("help", 'h'));
		possibleFlags.add(new Flag("verbose", 'v'));
		possibleFlags.add(new Flag("callcheck", 'c'));
		possibleFlags.add(new Flag("bigendian", 'b'));
		possibleFlags.add(new Flag("littleendian", 'l'));
	}
	
	/**
	 * Detemines whether the flag with the given name was present.
	 * @param name
	 * @return true if present, false if not.
	 */
	public boolean hasFlag(String name) {
		for(Flag f : presentFlags) {
			if(f.n.equals(name)) return true;
		}
		return false;
	}
	
	

}

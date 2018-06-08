package backend.parser;

import java.util.ArrayList;
import java.util.List;

import backend.program.Program;
import backend.state.Data;
import exceptions.DataFormatException;
import exceptions.ExecutionException;
import exceptions.UnsupportedDataException;

/**
 * Parses data section of MIPS file.
 * @author Nathaniel
 * @version 06-08-2018
 */
public class DataParser {

	private Program prog;

	/**
	 * Initializes The DataParser
	 * @param program to save parsed data to.
	 */
	public DataParser(Program program) {
		prog = program;
	}
	
	/**
	 * Converts a line in the file to a global data entry.
	 * @param line line of file to convert.
	 * @throws DataFormatException if a label on the line is already in use.
	 */
	public void makeData(String line) throws DataFormatException {
		String text = line;
		if(line.indexOf('#') >= 0) text = text.substring(0,  line.indexOf('#')); // Remove Comments
		text = text.trim(); // Remove leading and trailing whitespace
		if(text.startsWith(".align")) return; // Skip alignment commands
		if(text.isEmpty()) return; // Skip empty lines
		
		if(text.indexOf(':') >= 0 && text.indexOf(':') < text.indexOf('.')) {
			String reference = text.substring(0, text.indexOf(':'));
			prog.getMem().addDataReference(reference);
			text = text.substring(text.indexOf(':') + 1).trim();
			if(text.isEmpty()) return;
		}
		
		String[] dataSplit = text.split("\\s+", 2); // Split around remaining whitespace
		String dataType = dataSplit[0];
		String dataVal = dataSplit[1];
		
		if(dataType.equals(".space")) makeSpace(dataVal);
		else if(dataType.equals(".word")) makeWords(dataVal);
		else if(dataType.equals(".float")) makeFloats(dataVal);
		else if(dataType.equals(".double")) makeDoubles(dataVal);
		else if(dataType.equals(".byte")) makeBytes(dataVal);
		else if(dataType.equals(".ascii")) makeAscii(dataVal);
		else if(dataType.equals(".asciiz")) makeAscii(dataVal + "\0");
		else if(dataType.equals(".halfword") || dataType.equals(".half")) {
			throw new UnsupportedDataException("Data types .half, .halfword are not supported");
		}
		else {
			throw new DataFormatException("Unknown Data Type: " + dataType);
		}
	}

	/**
	 * Creates an ASCII string in global data memory.
	 * @param dataVal the string.
	 */
	private void makeAscii(String dataVal) {
		dataVal = processString(dataVal);
		ArrayList<Data> memOutput = new ArrayList<>();
		char[] charArray = (dataVal).toCharArray();		
		for(int i = 0; i < charArray.length; i += 4) {
			memOutput.add(new Data());
			for(int j = i; j < i + 4; j++) {
				if(j < charArray.length) {
					memOutput.set(i/4, new Data((memOutput.get(i/4).getValue() << 8)
							+ charArray[j], Data.DataType.String));
				}
				else {
					memOutput.set(i/4, new Data(memOutput.get(i/4).getValue() << 8, 
							Data.DataType.String));
				}
			}
		}
		prog.getMem().addToGlobalData(memOutput);
	}

	/**
	 * Makes a list of bytes in global data memory.
	 * @param dataVal String defining the bytes.
	 */
	private void makeBytes(String dataVal) {
		String[] dataVals = dataVal.split(",");
		byte[] byteArray = new byte[dataVals.length];
		for(int i = 0; i < dataVals.length; i ++) {
			byteArray[i] = Byte.parseByte(dataVal.trim());
		}
		ArrayList<Data> memOutput = new ArrayList<>();
		for(int i = 0; i < byteArray.length; i += 4) {
			memOutput.add(new Data());
			for(int j = i; j < i + 4; j++) {
				if(j < byteArray.length) {
					memOutput.set(i/4, new Data((memOutput.get(i/4).getValue() << 8)
							+ byteArray[j], Data.DataType.String));
				}
				else {
					memOutput.set(i/4, new Data(memOutput.get(i/4).getValue() << 8, 
							Data.DataType.String));
				}
			}
		}
		prog.getMem().addToGlobalData(memOutput);
	}

	/**
	 * Creates a list of double precision floats in global data memory.
	 * @param dataVal String defining the doubles.
	 */
	private void makeDoubles(String dataVal) {
		ArrayList<Data> output = new ArrayList<>();
		String[] dataVals = dataVal.split(",");
		for(int i = 0; i < dataVals.length; i++) {
			long doubleVal = Double.doubleToLongBits(Double.parseDouble(dataVal.trim()));
			int bottom = (int)(Long.rotateLeft(doubleVal, 32) >>> 32);
			int top = (int)(doubleVal >>> 32);
			output.add(new Data(bottom, Data.DataType.Double_L));
			output.add(new Data(top, Data.DataType.Double_H));
		}
		prog.getMem().addToGlobalData(output);
	}

	/**
	 * Creates a list of floats in global data memory.
	 * @param dataVal String defining the floats.
	 */
	private void makeFloats(String dataVal) {
		ArrayList<Data> output = new ArrayList<>();
		String[] dataVals = dataVal.split(",");
		for(int i = 0; i < dataVals.length; i++) {
			output.add(new Data(Float.floatToIntBits(Float.parseFloat(dataVal.trim())), 
					Data.DataType.Float));
		}
		prog.getMem().addToGlobalData(output);
	}

	/**
	 * Creates a list of words in global data memory.
	 * @param dataVal String defining the words.
	 */
	private void makeWords(String dataVal) {
		ArrayList<Data> output = new ArrayList<>();
		String[] dataVals = dataVal.split(",");
		for(int i = 0; i < dataVals.length; i++) {
			output.add(new Data(Integer.parseInt(dataVal.trim()), 
					Data.DataType.Integer));
		}
		prog.getMem().addToGlobalData(output);
	}

	/**
	 * Creates a segment of writeable space in global data memory.
	 * @param dataVal String determining size of space.
	 */
	private void makeSpace(String dataVal) {
		int size = (int)Math.round(Math.ceil(Double.parseDouble(dataVal)/4.0));
		ArrayList<Data> space = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			space.add(new Data());
		}
		prog.getMem().addToGlobalData(space);
	}

	/**
	 * Converts escape characters in a string to the actual escape character.
	 * @param dataVal the input string to process.
	 * @return processed string with proper escape character values.
	 */
	private String processString(String dataVal) {
		String inString = dataVal.substring(dataVal.indexOf(34) + 1, 
				dataVal.lastIndexOf(34));
		if(inString.contains("\\")) {
			inString = inString.replaceAll("\\\\n", "\n");
			inString = inString.replaceAll("\\\\r", "\r");
			inString = inString.replaceAll("\\\\t", "\t");
			inString = inString.replaceAll("\\\\\"", "\"");
			inString = inString.replaceAll("\\\\\\\\", "\\");
		}
		return inString;
	}
	
	/**
	 * Converts a String to a list of Data.
	 * Splits string into chars, stores each char as a byte, 
	 * clumps 4 consecutive bytes into a word, and saves that
	 * as a Data value.
	 * @param dataVal the String to convert.
	 * @return List of Data values produced from the string.
	 */
	public static List<Data> stringToDataArray(String dataVal) {
		ArrayList<Data> memOutput = new ArrayList<>();
		char[] charArray = (dataVal + "\0").toCharArray();		
		for(int i = 0; i < charArray.length; i += 4) {
			memOutput.add(new Data());
			for(int j = i; j < i + 4; j++) {
				if(j < charArray.length) {
					memOutput.set(i/4, new Data((memOutput.get(i/4).getValue() << 8)
							+ charArray[j], Data.DataType.String));
				}
				else {
					memOutput.set(i/4, new Data(memOutput.get(i/4).getValue() << 8, 
							Data.DataType.String));
				}
			}
		}
		return memOutput;
	}
	
	/**
	 * Converts a list of Data values to a String.
	 * Takes each Data value, splits it into 4 bytes, converts
	 * each byte into a character, and concatenates the characters
	 * into the output string.
	 * @param input the list of Data values to convert to a String.
	 * @return output String produced from Data values.
	 */
	public static String dataArrayToString(List<Data> input) {
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < input.size(); i++) {
			for(int j = 3; j >= 0; j--) {
				char nextChar = (char) ((input.get(i).getValue() >> 8*j) % 256);
				if(nextChar == (char)0) {
					return output.toString();
				}
				output.append(nextChar);
			}
		}
		throw new ExecutionException("String missing null termination: " + output.toString());
	}

}

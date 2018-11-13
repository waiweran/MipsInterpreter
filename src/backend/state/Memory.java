package backend.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.program.Register;
import backend.state.Data.DataType;
import exceptions.DataFormatException;
import exceptions.ExecutionException;
import exceptions.MemoryException;
import exceptions.SegmentationFault;

/**
 * Holds all of main memory.
 * @author Nathaniel
 * @version 11-05-2017
 */
public class Memory {
	
	private RegisterFile regs;
	private List<Data> globalData;
	private Map<String, Integer> dataRefs;
	private List<Data> heap;
	private List<Data> stack;
	private boolean bigEndian;

	/**
	 * Initializes the main memory
	 * @param registers the Register File.
	 * Used to read the value of the stack pointer.
	 */
	public Memory(RegisterFile registers) {
		regs = registers;
		globalData = new ArrayList<>();
		dataRefs = new HashMap<>();
		heap = new ArrayList<>();
		stack = new ArrayList<>();
		bigEndian = false;
	}
	
	/**
	 * Checks to see if the given String references an item in global data.
	 * @param ref the String in question.
	 * @return true if the string references global data, false if not.
	 */
	public boolean isDataReference(String ref) {
		return dataRefs.containsKey(ref);
	}
	
	/**
	 * Adds values to global data.
	 * @param values the Data items to add to global data.
	 */
	public void addToGlobalData(List<Data> values) {
		for(Data value : values) {
			if(bigEndian) globalData.add(value);
			else globalData.add(reverseEndian(value));
		}
	}
	
	/**
	 * Adds Strings to global data.
	 * @param values the Data items to add to global data.
	 */
	public void addStringsToGlobalData(List<Data> values) {
		for(Data value : values) {
			globalData.add(value);
		}
	}
	
	/**
	 * Adds a global data reference label.
	 * @param reference the String referencing the global data address.
	 * @throws DataFormatException if the reference already exists.
	 */
	public void addDataReference(String reference) throws DataFormatException {
		if(dataRefs.containsKey(reference)) 
			throw new DataFormatException("Memory address reference \""
				+ reference + "\" already used");
		dataRefs.put(reference, globalData.size()*4);
	}
	
	/**
	 * Gets the memory address represented by a reference string.
	 * @param reference the reference string.
	 * @return integer representing the address in global data.
	 */
	public int getMemoryAddress(String reference) {
		if(!dataRefs.containsKey(reference)) throw new MemoryException("Reference \"" 
				+ reference + "\" not in memory ");
		return dataRefs.get(reference);
	}
	
	/**
	 * Allocates space on the heap.
	 * @param amount number of bytes to allocate.
	 * @return address of start of allocated heap space.
	 */
	public int allocateHeap(int amount) {
		int size = amount/4 + ((amount%4 == 0)? 0 : 1);
		int address = (globalData.size() + heap.size())*4;
		for(int i = 0; i < size; i++) {
			heap.add(new Data());
		}
		return address;
	}
	
	/**
	 * Loads a word from memory.
	 * Must be word aligned.
	 * Accesses global data, heap, and stack.
	 * @param address the memory address.
	 * @return the Data at that address.
	 */
	public Data loadWord(int address) {
		Data output;
		int word = getWordAddress(address);
		// Global data
		if(word < globalData.size()) {
			output =  globalData.get(word);
		}
		// Heap
		else if(word - globalData.size() < heap.size()) {
			output =  heap.get(word - globalData.size());
		}
		// Stack
		else if(word >= Integer.MAX_VALUE/4 + 1 - stack.size()) {
			output =  stack.get(Integer.MAX_VALUE/4 - word);
		}
		else {
			throw new SegmentationFault(address, globalData, heap, stack);
		}
		if(!bigEndian) output = reverseEndian(output);
		return output;
	}
	
	/**
	 * Stores a word to the given address.
	 * Must be word aligned.
	 * @param data the Data to store.
	 * @param address location in memory to store the data.
	 */
	public void storeWord(Data data, int address) {
		if(!bigEndian) data = reverseEndian(data);
		int word = getWordAddress(address);
		// Global Data
		if(word < globalData.size()) {
			globalData.set(word, data);
		}
		// Heap
		else if(word - globalData.size() < heap.size()) {
			heap.set(word - globalData.size(), data);
		}
		// Stack
		else if(regs.read(Register.sp).getValue() > 0 
				&& word >= regs.read(Register.sp).getValue()/4) {
			for(int i = stack.size(); i < Integer.MAX_VALUE/4 + 1
					- regs.read(Register.sp).getValue()/4; i++) {
				stack.add(new Data());
			}
			stack.set(Integer.MAX_VALUE/4 - word, data);
		}
		else {
			throw new SegmentationFault(address, globalData, heap, stack);
		}
	}
	
	/**
	 * Loads a byte from memory.
	 * @param address the memory address.
	 * @return Data holding the byte at that address.
	 */
	public Data loadByte(int address, boolean signed) {
		int byteAddr = getByteAddress(address);
		Data word = loadWord(address - (address & 3));
		int val = (word.getValue() & (255 << (8*byteAddr))) >>> (8*byteAddr);
		if(signed && val > 127) val = val | (-1 << (8*(byteAddr + 1)));
		return new Data(val, Data.DataType.Byte);
	}
	
	/**
	 * Stores a byte to memory
	 * @param data Data holding a byte to store.
	 * Stores only first byte held in Data.
	 * @param address the memory location to write to.
	 */
	public void storeByte(Data data, int address) {
		int byteAddr = getByteAddress(address);
		Data word = loadWord(address - (address & 3));
		int top = byteAddr == 3? 0 : word.getValue() & (-1 << (8*byteAddr + 8));
		int bottom = word.getValue() & ((1 << (8*byteAddr)) - 1);
		int mid = (data.getValue() & 255) << (8*byteAddr);
		Data.DataType datatype = word.getDataType();
		if(datatype == Data.DataType.Space) datatype = Data.DataType.Byte;
		storeWord(new Data(top | mid | bottom, datatype), address - (address & 3));
	}
	
	/**
	 * Loads a series of consecutive values from memory.
	 * @param address the starting address of the array.
	 * @return contents of Global Data, Heap, or Stack,
	 *  starting at that address and continuing to end of memory sector. 
	 */
	public String loadString(int address) {
		ArrayList<Data> values = new ArrayList<>();
		int word = getWordAddress(address);
		// Global Data
		if(word < globalData.size()) {
			for(int i = 0; word + i < globalData.size(); i++) {
				Data value = globalData.get(word + i);
				values.add(value);
			}
		}
		// Heap
		else if(word - globalData.size() < heap.size()) {			
			for(int i = 0; word - globalData.size() + i < heap.size(); i++) {
				Data value = heap.get(word - globalData.size() + i);
				values.add(value);
			}
		}
		// Stack
		else if(word >= Integer.MAX_VALUE/4 + 1 - stack.size()) {
			for(int i = 0; Integer.MAX_VALUE/4 + 1 - word + i < values.size(); i++) {
				Data value = stack.get(Integer.MAX_VALUE/4 + 1 - word + i);
				values.add(value);
			}
		}
		else {
			throw new SegmentationFault(address, globalData, heap, stack);
		}
		StringBuilder sb = new StringBuilder();
		for(Data value : values) {
			for(int i = 3; i >= 0; i--) {
				char letter = (char) ((value.getValue() & (255 << 8*i)) >>> 8*i);
				sb.append(letter);
				if(letter == (char)0) return sb.toString();
			}
		}
		throw new ExecutionException("String has no null termination: " + sb.toString());
	}

	/**
	 * Stores a list of Data values to consecutive memory locations.
	 * @param address the Address to store the first data value at.
	 * @param values the list of Data values to store.
	 */
	public void storeString(int address, String value) {
		List<Data> values = new ArrayList<>();
		for(int wordNum = 0; true; wordNum++) {
			int dataVal = 0;
			boolean end = false;
			for(int byteNum = 0; byteNum < 4; byteNum++) {
				int index = wordNum*4 + byteNum;
				if(index < value.length()) {
					dataVal = dataVal | (((int)value.charAt(index)) << (8*(3-byteNum)));
				}
				else {
					end = true;
				}
			}
			values.add(new Data(dataVal, DataType.String));
			if(end) break;
		}
		int word = getWordAddress(address);
		// Global Data
		if(word < globalData.size()) {
			try {
				for(int i = 0; i < values.size(); i++) {
					globalData.set(word + i, values.get(i));
				}
			}
			catch(IndexOutOfBoundsException e) {
				throw new MemoryException("Array too long for given memory slot");
			}
		}
		// Heap
		else if(word - globalData.size() < heap.size()) {			
			try {
				for(int i = 0; i < values.size(); i++) {
					heap.set(word - globalData.size() + i, values.get(i));
				}
			}
			catch(IndexOutOfBoundsException e) {
				throw new MemoryException("Array too long for given memory slot");
			}
		}
		// Stack
		else if(word >= regs.read(Register.sp).getValue()/4) {
			for(int i = stack.size(); i < Integer.MAX_VALUE/4 + 1
					- regs.read(Register.sp).getValue()/4; i++) {
				stack.add(new Data());
			}
			try {
				for(int i = 0; i < values.size(); i++) {
					stack.set(Integer.MAX_VALUE/4 + 1 - word + i, values.get(i));
				}
			}
			catch(IndexOutOfBoundsException e) {
				throw new MemoryException("Array too long for given memory slot");
			}
		}
		else {
			throw new SegmentationFault(address, globalData, heap, stack);
		}
	}
	
	/**
	 * Sets the endianness of the memory.
	 * @param bigEndian True to set to big endian, false to set to little endian.
	 */
	public void setEndianness(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Global Data: \n");
		for(int i = 0; i < globalData.size(); i++) {
			output.append(getHex(i*4) + ":\t" 
					+ globalData.get(i).getDataType() + "\t" 
					+ printWithEndian(globalData.get(i)) + "\n");
		}
		output.append("\nHeap: \n");
		for(int i = 0; i < heap.size(); i++) {
			output.append(getHex((i + globalData.size())*4) + ":\t"
					+ heap.get(i).getDataType() + "\t" 
					+ printWithEndian(heap.get(i)) + "\n");
		}
		output.append("\nStack: \n");
		for(int i = 0; i < stack.size(); i++) {
			output.append(getHex((Integer.MAX_VALUE/4 - i)*4) + ":\t"
					+ stack.get(i).getDataType() + "\t" 
					+ printWithEndian(stack.get(i)) + "\n");
		}
		return output.toString();
	}
	
	/**
	 * @return Representation of memory with stored data as Strings
	 */
	public String toCharString() {
		StringBuilder output = new StringBuilder();
		output.append("Global Data: \n");
		for(int i = 0; i < globalData.size(); i++) {
			output.append(getHex(i*4) + ":\t" 
					+ globalData.get(i).getDataType() + "\t"
					+ globalData.get(i).toCharString() + "\n");
		}
		output.append("\nHeap: \n");
		for(int i = 0; i < heap.size(); i++) {
			output.append(getHex((i + globalData.size())*4) + ":\t"
					+ heap.get(i).getDataType() + "\t"
					+ heap.get(i).toCharString() + "\n");
		}
		output.append("\nStack: \n");
		for(int i = 0; i < stack.size(); i++) {
			output.append(getHex((Integer.MAX_VALUE/4 - i)*4) + ":\t"
					+ stack.get(i).getDataType() + "\t"
					+ stack.get(i).toCharString() + "\n");
		}
		return output.toString();
	}
	
	/**
	 * @return representation of memory with stored data in Hex.
	 */
	public String toHexString() {
		StringBuilder output = new StringBuilder();
		output.append("Global Data: \n");
		for(int i = 0; i < globalData.size(); i++) {
			output.append(getHex(i*4) + ":\t" + globalData.get(i).getDataType()
					+ "\t" + globalData.get(i).toHex() + "\n");
		}
		output.append("\nHeap: \n");
		for(int i = 0; i < heap.size(); i++) {
			output.append(getHex((i + globalData.size())*4) + ":\t"
					+ heap.get(i).getDataType() + "\t" + heap.get(i).toHex() + "\n");
		}
		output.append("\nStack: \n");
		for(int i = 0; i < stack.size(); i++) {
			output.append(getHex((Integer.MAX_VALUE/4 - i)*4) + ":\t"
					+ stack.get(i).getDataType() + "\t" + stack.get(i).toHex() + "\n");
		}
		return output.toString();
	}
	
	/**
	 * @return representation of memory with stored data as decimal values.
	 */
	public String toDecimalString() {
		StringBuilder output = new StringBuilder();
		output.append("Global Data: \n");
		for(int i = 0; i < globalData.size(); i++) {
			output.append(getHex(i*4) + ":\t" + globalData.get(i).getDataType()
					+ "\t" + (bigEndian? globalData.get(i).toDecimal() : 
						reverseEndian(globalData.get(i)).toDecimal()) + "\n");
		}
		output.append("\nHeap: \n");
		for(int i = 0; i < heap.size(); i++) {
			output.append(getHex((i + globalData.size())*4) + ":\t"
					+ heap.get(i).getDataType() + "\t" + 
					(bigEndian? heap.get(i).toDecimal() : 
						reverseEndian(heap.get(i)).toDecimal()) + "\n");
		}
		output.append("\nStack: \n");
		for(int i = 0; i < stack.size(); i++) {
			output.append(getHex((Integer.MAX_VALUE/4 - i)*4) + ":\t"
					+ stack.get(i).getDataType() + "\t" + 
					(bigEndian? stack.get(i).toDecimal() : 
						reverseEndian(stack.get(i)).toDecimal()) + "\n");
		}
		return output.toString();
	}
	
	/**
	 * @return representation of memory with stored data as IEEE 32 bit floating point numbers.
	 */
	public String toFloatString() {
		StringBuilder output = new StringBuilder();
		output.append("Global Data: \n");
		for(int i = 0; i < globalData.size(); i++) {
			output.append(getHex(i*4) + ":\t" + globalData.get(i).getDataType()
					+ "\t" + (bigEndian? globalData.get(i).toFloatString() : 
						reverseEndian(globalData.get(i)).toFloatString()) + "\n");
		}
		output.append("\nHeap: \n");
		for(int i = 0; i < heap.size(); i++) {
			output.append(getHex((i + globalData.size())*4) + ":\t"
					+ heap.get(i).getDataType() + "\t" + 
					(bigEndian? heap.get(i).toFloatString() : 
						reverseEndian(heap.get(i)).toFloatString()) + "\n");
		}
		output.append("\nStack: \n");
		for(int i = 0; i < stack.size(); i++) {
			output.append(getHex((Integer.MAX_VALUE/4 - i)*4) + ":\t"
					+ stack.get(i).getDataType() + "\t" + 
					(bigEndian? stack.get(i).toFloatString() : 
						reverseEndian(stack.get(i)).toFloatString()) + "\n");
		}
		return output.toString();
	}
	
	/**
	 * Gets the hex value of a given integer.
	 * @param value integer input.
	 * @return String of hex value.
	 */
	private String getHex(int value) {
		String output = Integer.toHexString(value);
		return "00000000".substring(output.length()) + output.toUpperCase();
	}
	
	private String printWithEndian(Data value) {
		Data.DataType type = value.getDataType();
		if(!bigEndian && type != Data.DataType.String && type != Data.DataType.Byte) {
			return reverseEndian(value).toString();
		}
		else {
			return value.toString();
		}
	}
	
	/**
	 * Converts a byte indexed address to the word indexed address containing that byte.
	 * @param address the address input.
	 * @return the index in word-addressed memory to access.
	 * @throws MemoryException if non-word aligned address input.
	 */
	private int getWordAddress(int address) {
		if((address & 3) != 0) {
			throw new MemoryException("Misaligned Word Access: " + address);
		}
		return address >>> 2;
	}
	
	/**
	 * Gets the byte index within an integer representing the given address.
	 * @param address The memory address.
	 * @return Byte index within the loaded integer.
	 */
	private int getByteAddress(int address) {
		if(bigEndian) return 3 - (address & 3);
		else return address & 3;
	}
	
	/**
	 * Reverses the endianness of a Data value.
	 * Used to convert natively big-endian data to little-endian.
	 * @param input The input data.
	 * @return The same data but in the opposite endianness.
	 */
	private Data reverseEndian(Data input) {
		int byte0 = (input.getValue() & (255 << 24)) >>> 24; // MSB to LSB
		int byte1 = (input.getValue() & (255 << 16)) >>> 8;
		int byte2 = (input.getValue() & (255 << 8)) << 8;
		int byte3 = (input.getValue() & 255) << 24; // LSB to MSB
		return new Data(byte0 | byte1 | byte2 | byte3, input.getDataType());
	}

}

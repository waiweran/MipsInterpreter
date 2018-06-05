package backend.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.program.Register;
import exceptions.DataFormatException;
import exceptions.MemoryException;
import exceptions.SegmentationFault;

/**
 * Holds all of main memory.
 * @author Nathaniel
 * @version 11-05-2017
 */
public class Memory {
	
	/**
	 * Holds a value in Global Data.
	 * Used to designate whether the value
	 * can be overwritten.
	 */
	private class GlobalDataValue {
		
		private boolean readOnly;
		private Data value;
		
		private GlobalDataValue(Data val, boolean noWrite) {
			value = val;
			readOnly = noWrite;
		}
		
		@Override
		public String toString() {
			return value.toString();
		}
	}
	
	private RegisterFile regs;
	private List<GlobalDataValue> globalData;
	private Map<String, Integer> dataRefs;
	private List<Data> heap;
	private List<Data> stack;

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
	 * Adds an item to global data.
	 * @param reference the String referencing the global data address.
	 * @param values the Data items to add to global data.
	 * @throws DataFormatException 
	 */
	public void addToGlobalData(String reference, List<Data> values) throws DataFormatException {
		if(dataRefs.containsKey(reference)) throw new DataFormatException("Memory address reference \""
				+ reference + "\" already used");
		List<GlobalDataValue> writeVals = new ArrayList<>();
		for(Data val : values) {
			if(val.getDataType().equals(Data.DataType.Space)) {
				writeVals.add(new GlobalDataValue(val, false));
			}
			else {
				writeVals.add(new GlobalDataValue(val, true));
			}
		}
		dataRefs.put(reference, globalData.size()*4);
		globalData.addAll(writeVals);
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
		int word = getWordAddress(address);
		// Global data
		if(word < globalData.size()) {
			return globalData.get(word).value;
		}
		// Heap
		else if(word - globalData.size() < heap.size()) {
			return heap.get(word - globalData.size());
		}
		// Stack
		else if(word >= Integer.MAX_VALUE/4 + 1 - stack.size()) {
			return stack.get(Integer.MAX_VALUE/4 - word);
		}
		else {
			throw new SegmentationFault(address, globalData, heap, stack);
		}
	}
	
	/**
	 * Stores a word to the given address.
	 * Must be word aligned.
	 * @param data the Data to store.
	 * @param address location in memory to store the data.
	 */
	public void storeWord(Data data, int address) {
		int word = getWordAddress(address);
		// Global Data
		if(word < globalData.size()) {
			if(globalData.get(word).readOnly)
				throw new MemoryException("Cannot write to read only address " + address);
			globalData.set(word, new GlobalDataValue(data, false));
		}
		// Heap
		else if(word - globalData.size() < heap.size()) {
			heap.set(word - globalData.size(), data);
		}
		// Stack
		else if(word >= regs.read(Register.sp).getValue()/4) {
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
		Data word = loadWord(address - (address % 4));
		int val;
		if(signed) val = (word.getValue() << 8*(address % 4)) >> 24;
		else val = (word.getValue() << 8*(address % 4)) >>> 24;
		return new Data(val, Data.DataType.Byte);
	}
	
	/**
	 * Stores a byte to memory
	 * @param data Data holding a byte to store.
	 * Stores only first byte held in Data.
	 * @param address the memory location to write to.
	 */
	public void storeByte(Data data, int address) {
		int offset = address % 4;
		Data word = loadWord(address - offset);
		int top = (offset == 0)? 0 : (word.getValue() >>> 8*(4 - offset)) << 8*(4 - offset);
		int bottom = word.getValue() - ((word.getValue() >>> 8*(3 - offset)) << 8*(3 - offset));
		int mid = (data.getValue() % 256) << 8*(3 - offset);
		storeWord(new Data(top | mid | bottom, word.getDataType()), address - offset);
	}
	
	/**
	 * Loads a series of consecutive values from memory.
	 * @param address the starting address of the array.
	 * @return contents of Global Data, Heap, or Stack,
	 *  starting at that address and continuing to end of memory sector. 
	 */
	public List<Data> loadArray(int address) {
		ArrayList<Data> values = new ArrayList<>();
		int word = getWordAddress(address);
		// Global Data
		if(word < globalData.size()) {
			for(int i = 0; word + i < globalData.size(); i++) {
				values.add(globalData.get(word + i).value);
			}
		}
		// Heap
		else if(word - globalData.size() < heap.size()) {			
			for(int i = 0; word - globalData.size() + i < heap.size(); i++) {
				values.add(heap.get(word - globalData.size() + i));
			}
		}
		// Stack
		else if(word >= Integer.MAX_VALUE/4 + 1 - stack.size()) {
			for(int i = 0; Integer.MAX_VALUE/4 + 1 - word + i < values.size(); i++) {
				values.add(stack.get(Integer.MAX_VALUE/4 + 1 - word + i));
			}
		}
		else {
			throw new SegmentationFault(address, globalData, heap, stack);
		}
		return values;
	}

	/**
	 * Stores a list of Data values to consecutive memory locations.
	 * @param address the Address to store the first data value at.
	 * @param values the list of Data values to store.
	 */
	public void storeArray(int address, List<Data> values) {
		int word = getWordAddress(address);
		// Global Data
		if(word < globalData.size()) {
			try {
				for(int i = 0; i < values.size(); i++) {
					globalData.set(word + i, new GlobalDataValue(values.get(i), false));
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
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Global Data: \n");
		for(int i = 0; i < globalData.size(); i++) {
			output.append(getHex(i*4) + ":\t" 
					+ globalData.get(i).value.getDataType() + "\t" + globalData.get(i) + "\n");
		}
		output.append("\nHeap: \n");
		for(int i = 0; i < heap.size(); i++) {
			output.append(getHex((i + globalData.size())*4) + ":\t"
					+ heap.get(i).getDataType() + "\t" + heap.get(i) + "\n");
		}
		output.append("\nStack: \n");
		for(int i = 0; i < stack.size(); i++) {
			output.append(getHex((Integer.MAX_VALUE/4 - i)*4) + ":\t"
					+ stack.get(i).getDataType() + "\t" + stack.get(i) + "\n");
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
					+ globalData.get(i).value.getDataType() + "\t"
					+ globalData.get(i).value.toCharString() + "\n");
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
			output.append(getHex(i*4) + ":\t" + globalData.get(i).value.getDataType()
					+ "\t" + globalData.get(i).value.toHex() + "\n");
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
			output.append(getHex(i*4) + ":\t" + globalData.get(i).value.getDataType()
					+ "\t" + globalData.get(i).value.toDecimal() + "\n");
		}
		output.append("\nHeap: \n");
		for(int i = 0; i < heap.size(); i++) {
			output.append(getHex((i + globalData.size())*4) + ":\t"
					+ heap.get(i).getDataType() + "\t" + heap.get(i).toDecimal() + "\n");
		}
		output.append("\nStack: \n");
		for(int i = 0; i < stack.size(); i++) {
			output.append(getHex((Integer.MAX_VALUE/4 - i)*4) + ":\t"
					+ stack.get(i).getDataType() + "\t" + stack.get(i).toDecimal() + "\n");
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
			output.append(getHex(i*4) + ":\t" + globalData.get(i).value.getDataType()
					+ "\t" + globalData.get(i).value.toFloatString() + "\n");
		}
		output.append("\nHeap: \n");
		for(int i = 0; i < heap.size(); i++) {
			output.append(getHex((i + globalData.size())*4) + ":\t"
					+ heap.get(i).getDataType() + "\t" + heap.get(i).toFloatString() + "\n");
		}
		output.append("\nStack: \n");
		for(int i = 0; i < stack.size(); i++) {
			output.append(getHex((Integer.MAX_VALUE/4 - i)*4) + ":\t"
					+ stack.get(i).getDataType() + "\t" + stack.get(i).toFloatString() + "\n");
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
	
	/**
	 * Converts a byte indexed address to the word indexed address containing that byte.
	 * @param address the address input.
	 * @return the index in word-addressed memory to access.
	 * @throws MemoryException if non-word aligned address input.
	 */
	private int getWordAddress(int address) {
		if(address%4 != 0) {
			throw new MemoryException("Misaligned Word Access: " + address);
		}
		return address/4;
	}

}

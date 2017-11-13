package backend.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.program.Register;

public class Memory {
	
	private RegisterFile regs;
	private List<Data> globalData;
	private Map<String, Integer> dataRefs;
	private List<Data> heap;
	private List<Data> stack;

	public Memory(RegisterFile registers) {
		regs = registers;
		globalData = new ArrayList<>();
		dataRefs = new HashMap<>();
		heap = new ArrayList<>();
		stack = new ArrayList<>();
	}
	
	public boolean isDataReference(String ref) {
		return dataRefs.containsKey(ref);
	}
	
	public void addToGlobalData(String reference, List<Data> values) {
		if(dataRefs.containsKey(reference)) throw new RuntimeException("Memory address reference \""
				+ reference + "\" already used");
		dataRefs.put(reference, globalData.size()*4);
		globalData.addAll(values);
	}
	
	public int getMemoryAddress(String reference) {
		if(!dataRefs.containsKey(reference)) throw new RuntimeException("Reference \"" 
				+ reference + "\" not in memory ");
		return dataRefs.get(reference);
	}
	
	public int allocateHeap(int amount) {
		int size = amount/4 + ((amount%4 == 0)? 0 : 1);
		int address = (globalData.size() + heap.size())*4;
		for(int i = 0; i < size; i++) {
			heap.add(new Data());
		}
		return address;
	}
	
	public Data loadWord(int address) {
		int word = getWordAddress(address);
		// Global data
		if(word < globalData.size()) {
			return globalData.get(word);
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
			throw new RuntimeException("Segmentation Fault: " + address + 
					" Global Data: 0 to " + (globalData.size()*4) + ", Heap: " + 
					(globalData.size()*4 + " to " + (globalData.size()*4 + heap.size()*4) + 
					", Stack: " + ((long)Integer.MAX_VALUE - stack.size()*4 + 1) + " to " + 
					Integer.MAX_VALUE));
		}
	}
	
	public void storeWord(Data data, int address) {
		int word = getWordAddress(address);
		// Global Data
		if(word < globalData.size()) {
			if(globalData.get(word).getPermissions().equals(Data.Permissions.Read_Only))
				throw new RuntimeException("Segmentation Fault: "
						+ "Cannot write to read only address " + address);
			globalData.set(word, data);
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
			throw new RuntimeException("Segmentation Fault: " + address + 
					" Global Data: 0 to " + (globalData.size()*4) + ", Heap: " + 
					(globalData.size()*4 + " to " + (globalData.size()*4 + heap.size()*4) + 
					", Stack: " + (Integer.MAX_VALUE - stack.size()*4) + " to " + 
					((long)Integer.MAX_VALUE) + 1));
		}
	}
	
	public Data loadByte(int address) {
		Data word = loadWord(address - (address % 4));
		int val = (word.getValue() << 8*(address % 4)) >>> 24;
		return new Data(val, Data.DataType.Byte, word.getPermissions());
	}
	
	public void storeByte(Data data, int address) {
		int offset = address % 4;
		Data word = loadWord(address - offset);
		int top = (offset == 0)? 0 : (word.getValue() >>> 8*(4 - offset)) << 8*(4 - offset);
		int bottom = word.getValue() - ((word.getValue() >>> 8*(3 - offset)) << 8*(3 - offset));
		int mid = (data.getValue() % 256) << 8*(3 - offset);
		storeWord(new Data(top | mid | bottom, word.getDataType(), 
				data.getPermissions()), address - offset);
	}
	
	public List<Data> loadArray(int address) {
		ArrayList<Data> values = new ArrayList<>();
		int word = getWordAddress(address);
		// Global Data
		if(word < globalData.size()) {
			for(int i = 0; word + i < globalData.size(); i++) {
				values.add(globalData.get(word + i));
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
			throw new RuntimeException("Segmentation Fault: " + address + 
					" Global Data: 0 to " + (globalData.size()*4) + ", Heap: " + 
					(globalData.size()*4 + " to " + (globalData.size()*4 + heap.size()*4) + 
					", Stack: " + (Integer.MAX_VALUE - stack.size()*4) + " to " + 
					((long)Integer.MAX_VALUE) + 1));
		}
		return values;
	}

	public void storeArray(int address, List<Data> values) {
		int word = getWordAddress(address);
		// Global Data
		if(word < globalData.size()) {
			try {
				for(int i = 0; i < values.size(); i++) {
					globalData.set(word + i, values.get(i));
				}
			}
			catch(IndexOutOfBoundsException e) {
				throw new RuntimeException("Array too long for given memory slot");
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
				throw new RuntimeException("Array too long for given memory slot");
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
				throw new RuntimeException("Array too long for given memory slot");
			}
		}
		else {
			throw new RuntimeException("Segmentation Fault: " + address + 
					" Global Data: 0 to " + (globalData.size()*4) + ", Heap: " + 
					(globalData.size()*4 + " to " + (globalData.size()*4 + heap.size()*4) + 
					", Stack: " + (Integer.MAX_VALUE - stack.size()*4) + " to " + 
					((long)Integer.MAX_VALUE) + 1));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Global Data: \n");
		for(int i = 0; i < globalData.size(); i++) {
			output.append(getHex(i*4) + ":\t" 
					+ globalData.get(i).getDataType() + "\t" + globalData.get(i) + "\n");
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
	
	private String getHex(int value) {
		String output = Integer.toHexString(value);
		return "00000000".substring(output.length()) + output.toUpperCase();
	}
	
	private int getWordAddress(int address) {
		if(address%4 != 0) {
			throw new RuntimeException("Misaligned Word Access: " + address);
		}
		return address/4;
	}

}

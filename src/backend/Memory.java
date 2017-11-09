package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memory {
	
	private RegisterFile regs;
	private List<Integer> globalData;
	private Map<String, Integer> dataRefs;
	private List<Integer> heap;
	private List<Integer> stack;

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
	
	public void addToGlobalData(String reference, List<Integer> values) {
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
			heap.add(0);
		}
		return address;
	}
	
	public int loadWord(int address) {
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
	
	public void storeWord(int data, int address) {
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
		else if(word >= regs.read(Register.sp)/4) {
			for(int i = stack.size(); i < Integer.MAX_VALUE/4 + 1 - regs.read(Register.sp)/4; i++) {
				stack.add(0);
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
	
	public int loadByte(int address) {
		int word = loadWord(address - (address % 4));
		word = (word << 8*(address % 4)) >>> 24;
		return word;
	}
	
	public void storeByte(int data, int address) {
		int word = loadWord(address - (address % 4));
		int top = (word >>> 8*(4 - (address % 4))) << 8*(4 - (address % 4));
		int bottom = (word << 8*((address % 4) + 1)) >>> 8*((address % 4) + 1);
		data = (data % 256) << 8*(3 - (address % 4));
		storeWord(top & data & bottom, address - (address % 4));
	}
	
	public List<Integer> loadArray(int address) {
		ArrayList<Integer> values = new ArrayList<>();
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

	public void storeArray(int address, List<Integer> values) {
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
		else if(word >= regs.read(Register.sp)/4) {
			for(int i = stack.size(); i < Integer.MAX_VALUE/4 + 1 - regs.read(Register.sp)/4; i++) {
				stack.add(0);
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
	
	private int getWordAddress(int address) {
		if(address%4 != 0) {
			throw new RuntimeException("Misaligned Word Access: " + address);
		}
		return address/4;
	}

}

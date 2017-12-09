package exceptions;

import java.util.List;

/**
 * Exception class for problems caused memory accesses outside of allocated space.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class SegmentationFault extends MemoryException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new Segmentation Fault exception
	 * @param address the address that caused the exception
	 * @param globalData
	 * @param heap
	 * @param stack
	 */
	public SegmentationFault(int address, 
			List<?> globalData, List<?> heap, List<?> stack) {
		super("Segmentation Fault: Address = " + Integer.toHexString(address) + 
				", Global Data: 0 to " + Integer.toHexString(globalData.size()*4) + 
				", Heap: " + Integer.toHexString(globalData.size()*4) + " to " + 
				Integer.toHexString(globalData.size()*4 + heap.size()*4) + ", Stack: " + 
				Integer.toHexString(Integer.MAX_VALUE - stack.size()*4 + 1) + " to " + 
				Integer.toHexString(Integer.MAX_VALUE + 1));
	}

}

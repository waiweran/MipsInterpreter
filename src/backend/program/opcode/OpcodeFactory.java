package backend.program.opcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import exceptions.InstructionFormatException;
import exceptions.UnsupportedOpcodeException;

/**
 * Creates an Opcode object given the Opcode name
 * @author Nathaniel 
 * @version 12-10-2017
 */
public class OpcodeFactory {
	
	private List<Opcode> values;

	/**
	 * Initializes the list of available Opcodes.
	 */
	public OpcodeFactory() {
		initialize();
	}
	
	/**
	 * gets an Opcode given the command name.
	 * @param name the name of the opcode.
	 * @return the Opcode.
	 */
	public Opcode findOpcode(String name) {
		for(Opcode op : values) {
			if(op.getName().equalsIgnoreCase(name)) return op;
		}
		throw new InstructionFormatException("Invalid Opcode, Name: " + name);
	}
	
	/**
	 * Determines whether the given name is a recognized Opcode.
	 * @param name the name to check.
	 * @return true if valid opcode name, false if not.
	 */
	public boolean isOpcode(String name) {
		for(Opcode op : values) {
			if(op.getName().equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	/**
	 * Initializes the list of available opcodes from the file.
	 */
	private void initialize() {
		values = new ArrayList<>();
		try {
			Scanner in = new Scanner(new File("available_opcodes.txt"));
			while(in.hasNextLine()) {
				String input = in.nextLine();
				if(!input.startsWith("#") && !input.isEmpty()) {
					try {
						Class<?> opClass = Class.forName(input);
						Object o = opClass.newInstance();
						values.add((Opcode) o);
					} catch (ClassNotFoundException | InstantiationException
							| IllegalAccessException | ClassCastException e) {
						in.close();
						throw new UnsupportedOpcodeException("Opcode " + input + " not supported", e);
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Opcodes list (available_opcodes.txt) not found");
		}
	}
	

}

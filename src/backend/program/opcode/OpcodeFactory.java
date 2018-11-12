package backend.program.opcode;

import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import exceptions.UnsupportedOpcodeException;

/**
 * Creates an Opcode object given the Opcode name
 * @author Nathaniel 
 * @version 12-10-2017
 */
public class OpcodeFactory {
	
	private static final ResourceBundle OPCODES = 
			ResourceBundle.getBundle("backend.program.opcode.Opcodes");
	
	/**
	 * gets an Opcode given the command name.
	 * @param name the name of the opcode.
	 * @return the Opcode.
	 * @throws RuntimeException if invalid opcode given.
	 * We recommend checking validity of opcode names before calling this method.
	 */
	public Opcode findOpcode(String name) {
		String className = OPCODES.getString(name);
		try {
			Class<?> opClass = Class.forName(className);
			Object o = opClass.getConstructor(String.class).newInstance(name);
			return (Opcode) o;
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | ClassCastException
				| NoSuchMethodException | SecurityException 
				| IllegalArgumentException | InvocationTargetException e) {
			throw new UnsupportedOpcodeException("Opcode " + name + " not supported", e);
		}

	}
	
	/**
	 * Determines whether the given name is a recognized Opcode.
	 * @param name the name to check.
	 * @return true if valid opcode name, false if not.
	 */
	public boolean isOpcode(String name) {
		return OPCODES.containsKey(name);
	}

}

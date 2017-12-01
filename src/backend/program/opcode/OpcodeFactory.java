package backend.program.opcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OpcodeFactory {
	
	private List<Opcode> values;

	public OpcodeFactory() {
		initialize();
	}
	
	public Opcode findOpcode(String name) {
		for(Opcode op : values) {
			if(op.getName().equalsIgnoreCase(name)) return op;
		}
		throw new RuntimeException("Invalid Opcode, Name: " + name);
	}
	
	public boolean isOpcode(String name) {
		for(Opcode op : values) {
			if(op.getName().equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
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
						throw new RuntimeException("Opcode " + input + " not supported", e);
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Opcodes list not found", e);
		}
	}
	

}

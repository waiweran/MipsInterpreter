package frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import backend.assembler.Assembler;
import exceptions.InstructionFormatException;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

/**
 * Top Menu of the MIPS Interpreter GUI.
 * @author Nathaniel
 * @version 11-08-2017
 */
public class TopMenu implements ScreenObject {
	
	private MenuBar topBar;
	private MainGUI gui;
	private List<File> recents;
	
	private Menu file;
	private Menu settings;
	private Menu export;
	private List<MenuItem> recentDisp;

	/**
	 * Initializes the Top Menu.
	 * @param mainGui the GUI that this menu controls.
	 */
	public TopMenu(MainGUI mainGui) {
		gui = mainGui;
		topBar = new MenuBar();
		recents = new ArrayList<>();
		recentDisp = new ArrayList<>();
		file = new Menu("File");
		makeFileMenu();
		settings = new Menu("Settings");
		makeSettingsMenu();
		export = new Menu("Export");
		makeExportMenu();
		topBar.getMenus().addAll(file, settings, export);
	}

	/**
	 * Sets up everything under File menu.
	 */
	private void makeFileMenu() {
		MenuItem open = new MenuItem("Open");
		open.setOnAction(e -> {
			gui.openFile();
			gui.loadProgram();
			addToRecents();
		});
		file.getItems().add(open);
		listRecentFiles();
	}
	
	/**
	 * Sets up everything under Display menu.
	 */
	private void makeSettingsMenu() {
		Menu endianness = new Menu("Memory Endianness");
		ToggleGroup endianToggle = new ToggleGroup();
		RadioMenuItem little = new RadioMenuItem("Little Endian");
		little.setSelected(true);
		little.setOnAction(e -> {
			gui.setEndianness(false);
		});
		little.setToggleGroup(endianToggle);
		RadioMenuItem big = new RadioMenuItem("Big Endian");
		big.setOnAction(e -> {
			gui.setEndianness(true);
		});
		big.setToggleGroup(endianToggle);
		endianness.getItems().addAll(little, big);
		settings.getItems().add(endianness);
		
		Menu dataDisp = new Menu("Data Format");
		ToggleGroup dispToggle = new ToggleGroup();
		RadioMenuItem auto = new RadioMenuItem("Auto");
		auto.setSelected(true);
		auto.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.AUTO);
			gui.getRegisters().setDisplayType(DataDisplay.AUTO);
		});
		auto.setToggleGroup(dispToggle);
		RadioMenuItem hex = new RadioMenuItem("Hexadecimal");
		hex.setOnAction(e -> {
			gui.setDisplayType(DataDisplay.HEX);
		});		
		hex.setToggleGroup(dispToggle);
		RadioMenuItem dec = new RadioMenuItem("Decimal");
		dec.setOnAction(e -> {
			gui.setDisplayType(DataDisplay.DECIMAL);
		});	
		dec.setToggleGroup(dispToggle);
		RadioMenuItem flo = new RadioMenuItem("Floating Point");
		flo.setOnAction(e -> {
			gui.setDisplayType(DataDisplay.FLOAT);
		});	
		flo.setToggleGroup(dispToggle);
		RadioMenuItem str = new RadioMenuItem("String");
		str.setOnAction(e -> {
			gui.setDisplayType(DataDisplay.STRING);
		});
		str.setToggleGroup(dispToggle);
		dataDisp.getItems().addAll(auto, hex, dec, flo, str);
		settings.getItems().add(dataDisp);
	}
	
	/**
	 * Sets up everything under the Export menu.
	 */
	private void makeExportMenu() {
		MenuItem binaryString = new MenuItem("Binary Text File");
		binaryString.setOnAction(e -> {
			Assembler assemble = new Assembler(gui.getProgram());
			File file = gui.saveFile("Save Assembled Code");
			if(file != null) {
				try {
					assemble.assembleProgram(file);
				}
				catch (IOException ex) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Could Not Save File");
					alert.show();
				}
				catch (InstructionFormatException ex) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Could Not Export");
					alert.setContentText(ex.getMessage());
					alert.show();
				}
			}
		});
		export.getItems().add(binaryString);
	}

	@Override
	public Node getGraphics() {
		return topBar;
	}

	@Override
	public void update() {
		// do nothing
	}
	
	/**
	 * Opens the most recent file (on program start).
	 */
	public void openMostRecent() {
		if(!recents.isEmpty()) {
			gui.setFile(recents.get(0));
		}
	}
	
	/**
	 * Adds an item to the list of recent files.
	 */
	private void addToRecents() {
		recents.remove(gui.getFile());
		recents.add(0, gui.getFile());
		if(recents.size() > 6) recents.remove(recents.size() - 1);
		writeOutRecents();
		listRecentFiles();
	}
	
	/**
	 * Generates list of recent files.
	 */
	private void listRecentFiles() {
		if(recents.isEmpty()) readInRecents();
		file.getItems().removeAll(recentDisp);
		recentDisp.clear();
		for(File f : recents) {
			MenuItem item = new MenuItem(f.getName());
			item.setOnAction(e -> {
				gui.setFile(f);
				gui.loadProgram();
				addToRecents();
			});
			recentDisp.add(item);
		}
		file.getItems().addAll(recentDisp);
	}
	
	/**
	 * Reads recents list in from a file.
	 */
	private void readInRecents() {
		try {
			Scanner in = new Scanner(new File("mipsdata.txt"));
			while(in.hasNextLine()) {
				recents.add(new File(in.nextLine()));
			}
			in.close();
		} catch (FileNotFoundException e) {
			// Do nothing, just start new recent list
		}
	}
	
	/**
	 * Writes recents list out to a file.
	 */
	private void writeOutRecents() {
		try {
			FileWriter fw = new FileWriter(new File("mipsdata.txt"));
			PrintWriter pw = new PrintWriter(fw);
			for(File f : recents) {
				pw.println(f.getAbsolutePath());
			}
			pw.close();
			fw.close();
		} catch (IOException e) {
			// Do nothing, recents just aren't saved
		}
	}

}

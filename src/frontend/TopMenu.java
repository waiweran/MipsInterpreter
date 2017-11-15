package frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.scene.Node;
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
	private List<MenuItem> recentDisp;
	private Menu display;

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
		display = new Menu("Display");
		makeDisplayMenu();
		topBar.getMenus().addAll(file, display);
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
	private void makeDisplayMenu() {
		Menu dataDisp = new Menu("Data Format");
		ToggleGroup toggle = new ToggleGroup();
		RadioMenuItem auto = new RadioMenuItem("Auto");
		auto.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.AUTO);
			gui.getRegisters().setDisplayType(DataDisplay.AUTO);
		});
		auto.setToggleGroup(toggle);
		RadioMenuItem hex = new RadioMenuItem("Hexadecimal");
		hex.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.HEX);
			gui.getRegisters().setDisplayType(DataDisplay.HEX);
		});		
		hex.setToggleGroup(toggle);
		RadioMenuItem dec = new RadioMenuItem("Decimal");
		dec.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.DECIMAL);
			gui.getRegisters().setDisplayType(DataDisplay.DECIMAL);
		});	
		dec.setToggleGroup(toggle);
		RadioMenuItem flo = new RadioMenuItem("Floating Point");
		flo.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.FLOAT);
			gui.getRegisters().setDisplayType(DataDisplay.FLOAT);
		});	
		flo.setToggleGroup(toggle);
		RadioMenuItem str = new RadioMenuItem("String");
		str.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.STRING);
			gui.getRegisters().setDisplayType(DataDisplay.STRING);
		});
		str.setToggleGroup(toggle);
		dataDisp.getItems().addAll(auto, hex, dec, flo, str);
		display.getItems().add(dataDisp);
	}

	@Override
	public Node getGraphics() {
		return topBar;
	}

	@Override
	public void update() {
		// do nothing
	}
	
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

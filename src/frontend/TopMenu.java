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

public class TopMenu implements ScreenObject {
	
	private MenuBar topBar;
	private MainGUI gui;
	private List<File> recents;
	
	private Menu file;
	private List<MenuItem> recentDisp;
	private Menu display;

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
		RadioMenuItem str = new RadioMenuItem("String");
		str.setOnAction(e -> {
			gui.getMemory().setDisplayType(DataDisplay.STRING);
			gui.getRegisters().setDisplayType(DataDisplay.STRING);
		});
		str.setToggleGroup(toggle);
		dataDisp.getItems().addAll(auto, hex, dec, str);
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
	
	private void addToRecents() {
		recents.remove(gui.getFile());
		recents.add(0, gui.getFile());
		if(recents.size() > 6) recents.remove(recents.size() - 1);
		writeOutRecents();
		listRecentFiles();
	}
	
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
			// Do nothing, recents aren't saved, no biggie
		}
	}

}

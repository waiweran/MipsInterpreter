package frontend;

import java.io.File;
import java.io.FileNotFoundException;

import backend.assembler.Assembler;
import backend.parser.TextParser;
import backend.program.Line;
import backend.program.Program;
import exceptions.DataFormatException;
import exceptions.InstructionFormatException;
import exceptions.JumpTargetException;
import exceptions.ProgramFormatException;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Runs the main GUI for the MIPS Interpreter.
 * @author Nathaniel
 * @version 11-08-2017
 */
public class MainGUI {
	
	public static final double SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
	public static final double SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
	
	private RegisterDisplay regs;
	private MemoryDisplay mem;
	private CommandLine cmd;
	private CodeDisplay code;
	private TopMenu menu;
	private ProgramControls control;
	private File currentFile;
	private Stage mainStage;
	
	private Program prog;

	/**
	 * Initializes the main GUI.
	 * @param primaryStage the stage to initialize the GUI inside.
	 */
	public MainGUI(Stage primaryStage) {
		mainStage = primaryStage;
		mainStage.setResizable(false);
		cmd = new CommandLine();
		menu = new TopMenu(this);
		menu.openMostRecent();
		try {
			loadProgram();
		}
		catch(Exception e) {
			currentFile = null;
			loadProgram();
		}
	}

	/**
	 * Initializes the GUI components given a program.
	 * @param program the program to initialize with.
	 */
	private void initialize(Program program) {
		regs = new RegisterDisplay(program);
		mem = new MemoryDisplay(program);
		code = new CodeDisplay(program);
		control = new ProgramControls(program, this);
		BorderPane root = new BorderPane();
		root.setLeft(regs.getGraphics());
		root.setCenter(code.getGraphics());
		root.setBottom(cmd.getGraphics());
		root.setRight(mem.getGraphics());
		VBox topBar = new VBox();
		topBar.getChildren().addAll(menu.getGraphics(), control.getGraphics());
		root.setTop(topBar);
		Scene scene = new Scene(root);
		mainStage.setScene(scene);
		mainStage.show();
		cmd.clear();
	}
	
	/**
	 * Loads the current program file into the MIPS Interpreter.
	 */
	public void loadProgram() {
		prog = new Program(cmd.getInputStream(), cmd.getPrintStream());
		if(currentFile != null) {
			try {
				new TextParser(currentFile, prog);
				mainStage.setTitle(currentFile.getName());
				prog.setupProgramClose();
				assembleProgram();
				prog.start();
			}
			catch(FileNotFoundException e) {
				currentFile = null;
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("File Not Found");
				alert.setContentText("Try opening a different file");
				alert.show();
			}
			catch(JumpTargetException e) {
				initialize(prog);
				control.lock();
				getCommandLine().printException(e);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Syntax Error: Jump Target");
				alert.setContentText(e.getMessage());
				alert.show();
			}
			catch(InstructionFormatException e) {
				initialize(prog);
				control.lock();
				getCommandLine().printException(e);
				getCode().errorHighlight(prog.getProgramLines().get(
						prog.getProgramLines().size() - 1));
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Syntax Error: Instruction Section");
				alert.setContentText(e.getMessage());
				alert.show();
			} catch (DataFormatException e) {
				initialize(prog);
				control.lock();
				getCommandLine().printException(e);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Syntax Error: Data Section");
				alert.setContentText(e.getMessage());
				alert.show();
			} catch (ProgramFormatException e) {
				initialize(prog);
				control.lock();
				getCommandLine().printException(e);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Syntax Error: General");
				alert.setContentText(e.getMessage());
				alert.show();
			}
		}
		initialize(prog);
		if(currentFile == null) control.lock();
		else control.unlock();
	}

	/**
	 * Assembles the program.
	 */
	private void assembleProgram() {
		Assembler assemble = new Assembler(prog);
		int insnNum = 0;
		for(Line l : prog.getProgramLines()) {
			try {
				if(l.isExecutable()) l.setHex(assemble.assemble(l, insnNum++));
			}
			catch(InstructionFormatException e) {
				System.out.println("Failure on line " + l);
				e.printStackTrace();
				initialize(prog);
				control.lock();
				getCommandLine().printException(e);
				getCode().errorHighlight(l);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Syntax Error: Instruction Section");
				alert.setContentText(e.getMessage());
				alert.show();
				throw new RuntimeException("Syntax Error: Instruction Section, "
						+ "line " + l, e);
			}
		}
	}

	/**
	 * Opens a MIPS File to interpret.
	 */
	public void openFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open MIPS File");
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("MIPS Code", "*.s"));
		File code = fileChooser.showOpenDialog(mainStage);
		currentFile = code;
	}
	
	/**
	 * Gets a file to save output into.
	 * @param title the title of the File Chooser.
	 * @return the File selected by the user.
	 */
	public File saveFile(String title) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.setInitialFileName(currentFile.getName().replace(".s", ".txt"));
		return fileChooser.showSaveDialog(mainStage);
	}
	
	/**
	 * @return the Code Display.
	 */
	public CodeDisplay getCode() {
		return code;
	}
	
	/**
	 * @return the Register Display.
	 */
	public RegisterDisplay getRegisters() {
		return regs;
	}
	
	/**
	 * @return the Memory Display.
	 */
	public MemoryDisplay getMemory() {
		return mem;
	}
	
	/**
	 * @return the Command Line.
	 */
	public CommandLine getCommandLine() {
		return cmd;
	}
	
	/**
	 * @return the top menu.
	 */
	public TopMenu getMenu() {
		return menu;
	}
	
	/**
	 * @return the currently loaded MIPS File.
	 */
	public File getFile() {
		return currentFile;
	}
	
	/**
	 * Sets the currently loaded MIPS File.
	 * @param newFile the file to set the current file to.
	 */
	public void setFile(File newFile) {
		currentFile = newFile;
	}
	
	/**
	 * @return the processed Program representing the MIPS File.
	 */
	public Program getProgram() {
		return prog;
	}
	
	/**
	 * Sets the background of a given JavaFX Region.
	 * Used to color executing lines of code, among other things.
	 * @param line the region to set the background of.
	 * @param color the color to set the background to.
	 */
	public static void setBackground(Region line, Color color) {
		line.setBackground(new Background(new BackgroundFill(color, 
				new CornerRadii(0.01), new Insets(0, 0, 0, 0))));
	}
	
}

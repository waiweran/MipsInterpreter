package frontend;

import java.io.File;
import java.io.FileNotFoundException;

import backend.TextParser;
import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.normal_mips.Jump;
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
	
	public void loadProgram() {
		prog = new Program(cmd.getInputStream(), cmd.getPrintStream());
		if(currentFile != null) {
			try {
				new TextParser(currentFile, prog);
				mainStage.setTitle(currentFile.getName());
				prog.setupProgramClose();
				new Instruction(new Jump(), null, null, null, null, null, null,
						0, "main").execute(prog);
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
				throw new RuntimeException("Syntax Error: Jump Target", e);
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
				throw new RuntimeException("Syntax Error: Instruction Section, Line " + 
						prog.getProgramLines().get(prog.getProgramLines().size() - 1), e);
			} catch (DataFormatException e) {
				initialize(prog);
				control.lock();
				getCommandLine().printException(e);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Syntax Error: Data Section");
				alert.setContentText(e.getMessage());
				alert.show();
				throw new RuntimeException("Syntax Error: Data Section", e);
			} catch (ProgramFormatException e) {
				initialize(prog);
				control.lock();
				getCommandLine().printException(e);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Syntax Error: General");
				alert.setContentText(e.getMessage());
				alert.show();
				throw new RuntimeException("Syntax Error: General", e);
			}
		}
		initialize(prog);
		if(currentFile == null) control.lock();
		else control.unlock();
	}

	public void openFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open MIPS File");
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("MIPS Code", "*.s"));
		File code = fileChooser.showOpenDialog(mainStage);
		currentFile = code;
	}
	
	public File saveFile(String title) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		return fileChooser.showSaveDialog(mainStage);
	}
	
	public CodeDisplay getCode() {
		return code;
	}
	
	public RegisterDisplay getRegisters() {
		return regs;
	}
	
	public MemoryDisplay getMemory() {
		return mem;
	}
	
	public CommandLine getCommandLine() {
		return cmd;
	}
	
	public TopMenu getMenu() {
		return menu;
	}
	
	public File getFile() {
		return currentFile;
	}
	
	public void setFile(File newFile) {
		currentFile = newFile;
	}
	
	public Program getProgram() {
		return prog;
	}
	
	public static void setBackground(Region line, Color color) {
		line.setBackground(new Background(new BackgroundFill(color, 
				new CornerRadii(0.01), new Insets(0, 0, 0, 0))));
	}
	
}

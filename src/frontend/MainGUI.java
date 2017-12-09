package frontend;

import java.io.File;
import java.io.FileNotFoundException;

import backend.TextParser;
import backend.program.Instruction;
import backend.program.Line;
import backend.program.Program;
import backend.program.Register;
import backend.program.opcode.normal_mips.Jump;
import backend.program.opcode.normal_mips.Syscall;
import backend.program.opcode.specially_added.LoadImmediate;
import backend.state.Data;
import exceptions.MIPSException;
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

	public MainGUI(Stage primaryStage) {
		mainStage = primaryStage;
		mainStage.setResizable(false);
		cmd = new CommandLine();
		menu = new TopMenu(this);
		menu.openMostRecent();
		loadProgram();
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
		Program prog = new Program(cmd.getInputStream(), cmd.getPrintStream());
		if(currentFile != null) {
			try {
				new TextParser(currentFile, prog);
				mainStage.setTitle(currentFile.getName());
				setupProgramClose(prog);
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
			catch(MIPSException e1) {
				System.out.println(prog.getProgramLines());
				initialize(prog);
				getCode().errorHighlight(prog.getProgramLines().get(
						prog.getProgramLines().size() - 1));
				control.lock();
				getCommandLine().printException(e1);
				throw new RuntimeException("Syntax Error", e1);
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
	
	private void setupProgramClose(Program prog) {
		prog.getRegFile().write(Register.ra, new Data(prog.getProgramLines().size(),
				Data.DataType.Address));
		prog.getProgramLines().add(new Line("", new Instruction(new LoadImmediate(), 
				Register.v0, null, null, null, null, null, 10, "")));
		prog.getProgramLines().add(new Line("", new Instruction(new Syscall(), 
				null, null, null, null, null, null, 0, "")));
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
	
	public static void setBackground(Region line, Color color) {
		line.setBackground(new Background(new BackgroundFill(color, 
				new CornerRadii(0.01), new Insets(0, 0, 0, 0))));
	}
	
}

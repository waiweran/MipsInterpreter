package frontend;

import java.io.File;

import backend.Data;
import backend.Instruction;
import backend.Line;
import backend.Opcode;
import backend.Program;
import backend.Register;
import backend.TextParser;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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
	
	public MainGUI(Stage stage) {
		mainStage = stage;
		cmd = new CommandLine();
		menu = new TopMenu(this);
		openFile();
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
		HBox topBar = new HBox();
		topBar.getChildren().addAll(menu.getGraphics(), control.getGraphics());
		root.setTop(topBar);
		Scene scene = new Scene(root);
		mainStage.setScene(scene);
		mainStage.show();
	}
	
	public void loadProgram() {
		Program prog = new Program(cmd.getInputStream(), cmd.getPrintStream());
		TextParser parser = new TextParser(currentFile, prog);
		prog = parser.getProgram();
		setupProgramClose(prog);
		new Instruction(Opcode.Jump, null, null, null, 0, "main").execute(prog);
		initialize(prog);
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
				Data.DataType.Address, Data.Permissions.Read_Only));
		prog.getProgramLines().add(new Line("", new Instruction(Opcode.LoadImmediate, 
				Register.v0, null, null, 10, "")));
		prog.getProgramLines().add(new Line("", new Instruction(Opcode.Syscall, 
				null, null, null, 0, "")));
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
	
	public static void setBackground(Region line, Color color) {
		line.setBackground(new Background(new BackgroundFill(color, 
				new CornerRadii(0.01), new Insets(0, 0, 0, 0))));
	}
	
}

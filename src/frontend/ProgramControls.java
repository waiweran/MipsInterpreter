package frontend;

import backend.program.Line;
import backend.program.Program;
import exceptions.ExecutionException;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Holds the controls that set execution parameters for the program.
 * @author Nathaniel
 * @version 11-08-2017
 */
public class ProgramControls implements ScreenObject {

	private HBox controls;
	private Timeline runProg;
	private Program prog;
	private MainGUI gui;
	private Button back, playPause, step, reload;
	private Slider rate;
	
	/**
	 * Initializes the program controls window.
	 * @param program the program that will be executing.
	 * @param mainGui the GUI holding the other program display components.
	 */
	public ProgramControls(Program program, MainGUI mainGui) {
		prog = program;
		gui = mainGui;
		controls = new HBox(5);
		KeyFrame frame = new KeyFrame(Duration.millis(1000), e -> step());
		runProg = new Timeline(frame);
		runProg.setCycleCount(Timeline.INDEFINITE);
		initialize();
		
	}
	
	/**
	 * Initializes the program controls.  
	 */
	private void initialize() {
		controls.setPadding(new Insets(5, 5, 5, 5));
		controls.setAlignment(Pos.CENTER_LEFT);
		back = new Button("Back");
		playPause = new Button("Play ");
		step = new Button("Step");
		reload = new Button("Reload");
		rate = new Slider(1, 50, 20);
		rate.setMinWidth(200);
		runProg.setRate(rate.getValue());
		back.setOnAction(e -> undo());
		playPause.setOnAction(e -> playPause(runProg.getStatus() == Status.RUNNING));
		step.setOnAction(e -> step());
		reload.setOnAction(e -> {
			playPause(true);
			gui.loadProgram();
		});
		rate.setOnMouseDragged(e -> updateAnimationRate());
		controls.getChildren().addAll(back, playPause, step, reload, new Text("     Speed:"), rate);
	}
	
	/**
	 * Plays or pauses the program execution.
	 * @param isPlaying True if program playing, false if not.  Determines play or pause functionality.
	 */
	public void playPause(boolean isPlaying) {
		if(isPlaying) {
			runProg.pause();
			playPause.setText("Play ");
			back.setDisable(false);
			step.setDisable(false);
		}
		else {
			runProg.play();
			playPause.setText("Pause");
			back.setDisable(true);
			step.setDisable(true);
		}
	}
	
	/**
	 * Steps the program back by 1 instruction.
	 */
	private void undo() {
		Line prevLine = prog.getLog().undo();
		gui.getCode().executionHighlight(prevLine);
		gui.getRegisters().update();
		gui.getMemory().update();
		gui.getCommandLine().update();
		gui.getCode().update();
		gui.getMenu().update();
	}
	
	/**
	 * Steps the program by 1 instruction.
	 */
	private void step() {
		if(prog.isDone()) {
			playPause(true);
			back.setDisable(true);
			playPause.setDisable(true);
			step.setDisable(true);
			return;
		}
		Line currentLine = prog.getNextLine();
		gui.getCode().executionHighlight(currentLine);
		if(currentLine.isExecutable()) {
			try {
				currentLine.getInstruction().execute(prog);
			}
			catch(Exception e) {
				gui.getCode().errorHighlight(currentLine);
				gui.getCommandLine().printException(e);
				playPause(true);
				back.setDisable(true);
				playPause.setDisable(true);
				step.setDisable(true);
				throw new ExecutionException("Program line " + currentLine + 
						" caused exception", e);
			}
			gui.getRegisters().update();
			gui.getMemory().update();
			gui.getCommandLine().update();
			gui.getCode().update();
			gui.getMenu().update();
		}
		else {
			step();
		}
	}
	
	/**
	 * Unlocks the play/pause, reset, and step features.
	 */
	public void unlock() {
		back.setDisable(false);
		playPause.setDisable(false);
		step.setDisable(false);
		reload.setDisable(false);
	}
	
	/**
	 * Locks the play/pause, reset, and step features.
	 * Typically used if error is detected in the program.
	 */
	public void lock() {
		back.setDisable(true);
		playPause.setDisable(true);
		step.setDisable(true);
		reload.setDisable(true);
	}
	
	/**
	 * Updates the animation rate based on the slider position.
	 */
	public void updateAnimationRate() {
		if(rate.getValue() < rate.getMax()) {
			runProg.setRate(rate.getValue());
		}
		else {
			runProg.setRate(10000);
		}
	}

	@Override
	public Node getGraphics() {
		return controls;
	}

	@Override
	public void update() {
		// do nothing
	}

}

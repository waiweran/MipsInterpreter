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

public class ProgramControls implements ScreenObject {

	private HBox controls;
	private Timeline runProg;
	private Program prog;
	private MainGUI gui;
	private Button playPause, step, reset;
	private Slider rate;
	
	public ProgramControls(Program program, MainGUI mainGui) {
		prog = program;
		gui = mainGui;
		controls = new HBox(5);
		KeyFrame frame = new KeyFrame(Duration.millis(1000), e -> step());
		runProg = new Timeline(frame);
		runProg.setCycleCount(Timeline.INDEFINITE);
		initialize();
		
	}
	
	private void initialize() {
		controls.setPadding(new Insets(5, 5, 5, 5));
		controls.setAlignment(Pos.CENTER_LEFT);
		playPause = new Button("Play ");
		step = new Button("Step");
		reset = new Button("Reset");
		rate = new Slider(1, 50, 20);
		rate.setMinWidth(200);
		runProg.setRate(rate.getValue());
		playPause.setOnAction(e -> playPause(runProg.getStatus() == Status.RUNNING));
		step.setOnAction(e -> step());
		reset.setOnAction(e -> {
			playPause(true);
			gui.loadProgram();
		});
		rate.setOnMouseDragged(e -> updateAnimationRate());
		controls.getChildren().addAll(playPause, step, reset, new Text("     Speed:"), rate);
	}
	
	private void playPause(boolean isPlaying) {
		if(isPlaying) {
			runProg.pause();
			playPause.setText("Play ");
			step.setDisable(false);
		}
		else {
			runProg.play();
			playPause.setText("Pause");
			step.setDisable(true);
		}
	}
	
	private void step() {
		if(prog.isDone()) {
			playPause(true);
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
	
	public void unlock() {
		playPause.setDisable(false);
		step.setDisable(false);
		reset.setDisable(false);
	}
	
	public void lock() {
		playPause.setDisable(true);
		step.setDisable(true);
		reset.setDisable(true);
	}
	
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

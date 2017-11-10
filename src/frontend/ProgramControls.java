package frontend;

import backend.Line;
import backend.Program;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class ProgramControls implements ScreenObject {

	private HBox controls;
	private Timeline runProg;
	private Program prog;
	private MainGUI gui;
	private Button playPause, step, reset;
	
	public ProgramControls(Program program, MainGUI mainGui) {
		prog = program;
		gui = mainGui;
		controls = new HBox(5);
		KeyFrame frame = new KeyFrame(Duration.millis(50), e -> step());
		runProg = new Timeline(frame);
		runProg.setCycleCount(Timeline.INDEFINITE);
		initialize();
		
	}
	
	private void initialize() {
		controls.setPadding(new Insets(5, 5, 5, 5));
		playPause = new Button("Play ");
		step = new Button("Step");
		reset = new Button("Reset");
		playPause.setOnAction(e -> playPause(runProg.getCurrentRate() > 0));
		step.setOnAction(e -> step());
		reset.setOnAction(e -> {
			playPause(true);
			gui.loadProgram();
		});
		controls.getChildren().addAll(playPause, step, reset);
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
				playPause(true);
				playPause.setDisable(true);
				step.setDisable(true);
				throw new RuntimeException("Program line " + currentLine + 
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

	@Override
	public Node getGraphics() {
		return controls;
	}

	@Override
	public void update() {
		// do nothing
	}

}

package frontend;

import java.util.HashMap;
import java.util.Map;

import backend.program.Program;
import backend.program.Register;
import backend.state.Data;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class RegisterDisplay implements ScreenObject {

	private VBox regs;
	private Map<Register, Text> regDisplays;
	private Text hiVal, loVal;
	private Program prog;
	private DataDisplay displayType;
	
	public RegisterDisplay(Program program) {
		regs = new VBox();
		regDisplays = new HashMap<>();
		prog = program;
		displayType = DataDisplay.AUTO;
		initialize();
	}
	
	private void initialize() {
		MainGUI.setBackground(regs, Color.WHITE);
		regs.setPadding(new Insets(10, 10, 10, 10));
		// Normal Registers
		for(Register r : Register.values()) {
			HBox reg = new HBox(10);
			MainGUI.setBackground(reg, Color.WHITE);
			String regName = "R" + r.getRegisterNumber() + "\t["
					+ r.getRegisterName() + "]:\t";
			reg.getChildren().add(new Text(regName));
			Text val = new Text(dataToString(prog.getRegFile().read(r)));
			reg.getChildren().add(val);
			regDisplays.put(r, val);
			regs.getChildren().add(reg);
		}
		// HI
		regs.getChildren().add(new Text(""));
		HBox regHI = new HBox(10);
		MainGUI.setBackground(regHI, Color.WHITE);
		regHI.getChildren().add(new Text("HI\t\t\t"));
		hiVal = new Text(dataToString(prog.getRegFile().readHI()));
		regHI.getChildren().add(hiVal);
		regs.getChildren().add(regHI);
		// LO
		HBox regLO = new HBox(10);
		MainGUI.setBackground(regLO, Color.WHITE);
		regLO.getChildren().add(new Text("LO\t\t\t"));
		loVal = new Text(dataToString(prog.getRegFile().readLO()));
		regLO.getChildren().add(loVal);
		regs.getChildren().add(regLO);

	}

	@Override
	public Node getGraphics() {
		regs.setMinWidth(MainGUI.SCREEN_WIDTH/6);
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(regs);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setMinViewportWidth(MainGUI.SCREEN_WIDTH/6);
		scroll.setMinHeight(MainGUI.SCREEN_HEIGHT*2/3);
		scroll.setMaxHeight(MainGUI.SCREEN_HEIGHT*2/3);
		return scroll;	}

	@Override
	public void update() {
		hiVal.setText(dataToString(prog.getRegFile().readHI()));
		loVal.setText(dataToString(prog.getRegFile().readLO()));
		for(Register r : Register.values()) {
			regDisplays.get(r).setText(dataToString(prog.getRegFile().read(r)));
		}
	}
	
	private String dataToString(Data input) {
		if(displayType.equals(DataDisplay.AUTO)) return input.toString();
		if(displayType.equals(DataDisplay.HEX)) return input.toHex();
		if(displayType.equals(DataDisplay.DECIMAL)) return input.toDecimal();
		return input.toCharString();
	}
	
	public void setDisplayType(DataDisplay type) {
		displayType = type;
		update();
	}

}

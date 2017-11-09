package frontend;

import java.util.HashMap;
import java.util.Map;

import backend.Program;
import backend.Register;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class RegisterDisplay implements ScreenObject {

	private VBox regs;
	private Map<Register, Text> regDisplays;
	private Text pcVal, hiVal, loVal;
	private Program prog;
	
	public RegisterDisplay(Program program) {
		regs = new VBox();
		regDisplays = new HashMap<>();
		prog = program;
		initialize();
	}
	
	private void initialize() {
		MainGUI.setBackground(regs, Color.WHITE);
		regs.setPadding(new Insets(10, 10, 10, 10));
		// PC
		HBox pc = new HBox(10);
		MainGUI.setBackground(pc, Color.WHITE);
		pc.getChildren().add(new Text("PC\t\t\t"));
		pcVal = new Text(prog.getPC() + "");
		pc.getChildren().add(pcVal);
		regs.getChildren().add(pc);
		// HI
		HBox regHI = new HBox(10);
		MainGUI.setBackground(regHI, Color.WHITE);
		regHI.getChildren().add(new Text("HI\t\t\t"));
		hiVal = new Text(prog.getRegFile().readHI() + "");
		regHI.getChildren().add(hiVal);
		regs.getChildren().add(regHI);
		// LO
		HBox regLO = new HBox(10);
		MainGUI.setBackground(regLO, Color.WHITE);
		regLO.getChildren().add(new Text("LO\t\t\t"));
		loVal = new Text(prog.getRegFile().readLO() + "");
		regLO.getChildren().add(loVal);
		regs.getChildren().add(regLO);
		// Normal Registers
		regs.getChildren().add(new Text(""));
		for(Register r : Register.values()) {
			HBox reg = new HBox(10);
			MainGUI.setBackground(reg, Color.WHITE);
			String regName = "R" + r.getRegisterNumber() + "\t["
					+ r.getRegisterName() + "]:\t";
			reg.getChildren().add(new Text(regName));
			Text val = new Text(prog.getRegFile().read(r) + "");
			reg.getChildren().add(val);
			regDisplays.put(r, val);
			regs.getChildren().add(reg);
		}
	}

	@Override
	public Node getGraphics() {
		return regs;
	}

	@Override
	public void update() {
		pcVal.setText(prog.getPC() + "");
		hiVal.setText(prog.getRegFile().readHI() + "");
		loVal.setText(prog.getRegFile().readLO() + "");
		for(Register r : Register.values()) {
			regDisplays.get(r).setText(prog.getRegFile().read(r) + "");
		}
	}

}

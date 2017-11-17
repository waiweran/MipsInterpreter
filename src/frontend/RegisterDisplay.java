package frontend;

import java.util.HashMap;
import java.util.Map;

import backend.program.FPRegister;
import backend.program.Program;
import backend.program.Register;
import backend.state.Data;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class RegisterDisplay implements ScreenObject {

	private VBox regs, fpRegs;
	private Map<Register, Text> regDisplays;
	private Map<FPRegister, Text> fpRegDisplays;
	private Text hiVal, loVal, cond;
	private Program prog;
	private DataDisplay displayType;
	
	public RegisterDisplay(Program program) {
		regs = new VBox();
		fpRegs = new VBox();
		regDisplays = new HashMap<>();
		fpRegDisplays = new HashMap<>();
		prog = program;
		displayType = DataDisplay.AUTO;
		initializeIntRegs();
		initializeFPRegs();
	}
	
	private void initializeIntRegs() {
		MainGUI.setBackground(regs, Color.WHITE);
		regs.setPadding(new Insets(5, 10, 10, 10));
		// Normal Registers
		for(Register r : Register.values()) {
			HBox reg = new HBox(10);
			MainGUI.setBackground(reg, Color.WHITE);
			String regName = "R" + r.getRegisterNumber() + "\t["
					+ r.getRegisterName() + "]:\t";
			reg.getChildren().add(new Text(regName));
			Text val = new Text(prog.getRegFile().read(r).getDataType() + "\t" +
					dataToString(prog.getRegFile().read(r)));
			reg.getChildren().add(val);
			regDisplays.put(r, val);
			regs.getChildren().add(reg);
		}
		// HI
		regs.getChildren().add(new Text(""));
		HBox regHI = new HBox(10);
		MainGUI.setBackground(regHI, Color.WHITE);
		regHI.getChildren().add(new Text("HI\t\t\t\t\t"));
		hiVal = new Text(dataToString(prog.getRegFile().readHI()));
		regHI.getChildren().add(hiVal);
		regs.getChildren().add(regHI);
		// LO
		HBox regLO = new HBox(10);
		MainGUI.setBackground(regLO, Color.WHITE);
		regLO.getChildren().add(new Text("LO\t\t\t\t\t"));
		loVal = new Text(dataToString(prog.getRegFile().readLO()));
		regLO.getChildren().add(loVal);
		regs.getChildren().add(regLO);
	}
	
	private void initializeFPRegs() {
		MainGUI.setBackground(fpRegs, Color.WHITE);
		fpRegs.setPadding(new Insets(5, 10, 10, 10));
		// Normal FP Registers
		for(FPRegister r : FPRegister.values()) {
			HBox reg = new HBox(10);
			MainGUI.setBackground(reg, Color.WHITE);
			String regName = "F" + r.getRegisterNumber() + "\t["
					+ r.getRegisterName() + "]:\t";
			reg.getChildren().add(new Text(regName));
			Text val = new Text(prog.getFPRegFile().read(r).getDataType() + "\t" +
					dataToString(prog.getFPRegFile().read(r)));
			reg.getChildren().add(val);
			fpRegDisplays.put(r, val);
			fpRegs.getChildren().add(reg);
		}
		// Comp
		fpRegs.getChildren().add(new Text(""));
		HBox regCond = new HBox(10);
		MainGUI.setBackground(regCond, Color.WHITE);
		regCond.getChildren().add(new Text("Comp\t\t\t\t"));
		cond = new Text(prog.getFPRegFile().readCond() + "");
		regCond.getChildren().add(cond);
		fpRegs.getChildren().add(regCond);
	}

	@Override
	public Node getGraphics() {
		regs.setMinWidth(MainGUI.SCREEN_WIDTH*9/40);
		fpRegs.setMinWidth(MainGUI.SCREEN_WIDTH*9/40);
		ScrollPane scrollReg = new ScrollPane();
		scrollReg.setContent(regs);
		scrollReg.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollReg.setMinViewportWidth(MainGUI.SCREEN_WIDTH*9/40);
		ScrollPane scrollFP = new ScrollPane();
		scrollFP.setContent(fpRegs);
		scrollFP.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollFP.setMinViewportWidth(MainGUI.SCREEN_WIDTH*9/40);
		TabPane tabs = new TabPane();
		tabs.setMaxHeight(MainGUI.SCREEN_HEIGHT*2/3);
		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		Tab regTab = new Tab("Registers");
		regTab.setContent(scrollReg);
		Tab fpTab = new Tab("Floating Point");
		fpTab.setContent(scrollFP);
		tabs.getTabs().addAll(regTab, fpTab);
		return tabs;
	}

	@Override
	public void update() {
		hiVal.setText(dataToString(prog.getRegFile().readHI()));
		loVal.setText(dataToString(prog.getRegFile().readLO()));
		for(Register r : Register.values()) {
			regDisplays.get(r).setText(prog.getRegFile().read(r).getDataType() + "\t" +
					dataToString(prog.getRegFile().read(r)));
		}
		for(FPRegister r : FPRegister.values()) {
			fpRegDisplays.get(r).setText(prog.getFPRegFile().read(r).getDataType() + "\t" +
					dataToString(prog.getFPRegFile().read(r)));
		}
	}
	
	private String dataToString(Data input) {
		if(displayType.equals(DataDisplay.AUTO)) return input.toString();
		if(displayType.equals(DataDisplay.HEX)) return input.toHex();
		if(displayType.equals(DataDisplay.DECIMAL)) return input.toDecimal();
		if(displayType.equals(DataDisplay.FLOAT)) return input.toFloatString();
		return input.toCharString();
	}
	
	public void setDisplayType(DataDisplay type) {
		displayType = type;
		update();
	}

}

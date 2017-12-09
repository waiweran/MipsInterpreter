package frontend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class CommandLine implements ScreenObject {

	private class CustomOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			currentText.append(Character.toString((char)b));
			cmds.setText(currentText.toString() + input.toString());
			cmds.positionCaret(cmds.getLength());
			cmds.setScrollTop(Double.MAX_VALUE);
		}
		
	}
	
	private class CustomInputStream extends InputStream {
		
		@Override
		public int available() {
			return (input.length() > 0 && 
					input.charAt(input.length() - 1) == '\r')? 1 : 0;
		}
		
		@Override
		public int read() throws IOException {
			try {
				int read = (int)input.charAt(0);
				if(read == '\r') read = '\n';
				out.write(read);
				input.deleteCharAt(0);
				return read;
			}
			catch(IndexOutOfBoundsException e) {
				throw new IOException("Nothing to read", e);
			}
		}
		
	}
	
	private StringBuilder currentText;
	private StringBuilder input;
	private InputStream in;
	private OutputStream out;
	private PrintStream print;
	private TextArea cmds;
	
	public CommandLine() {
		currentText = new StringBuilder();
		input = new StringBuilder();
		cmds = new TextArea();
		cmds.setOnKeyTyped(e -> {
			if(e.getCharacter().length() == 0) {
				input.deleteCharAt(input.length() - 1);
			}
			input.append(e.getCharacter());
		});
		in = new CustomInputStream();
		out = new CustomOutputStream();
		print = new PrintStream(out);
		
	}
	
	public void clear() {
		input.setLength(0);
		currentText.setLength(0);
		cmds.clear();
	}
	
	public PrintStream getPrintStream() {
		return print;
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public void printException(Exception e) {
		print.println("\n******** ERROR ********\n\n" + e.getMessage() + "\n\n"
				+ "************************\n");
	}

	@Override
	public Node getGraphics() {
		cmds.setMaxHeight(MainGUI.SCREEN_HEIGHT/6);
		return cmds;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}

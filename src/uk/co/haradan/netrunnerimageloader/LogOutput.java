package uk.co.haradan.netrunnerimageloader;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JTextArea;

public class LogOutput {
	
	private final JTextArea area;
	
	public LogOutput(JTextArea area) {
		this.area = area;
	}
	
	public void println(String str) {
		System.out.println(str);
		area.append(str+"\n");
		area.setCaretPosition(area.getDocument().getLength());
	}
	
	public void println() {
		System.out.println();
		area.append("\n");
		area.setCaretPosition(area.getDocument().getLength());
	}
	
	public void errorln(String str) {
		System.err.println(str);
		area.append(str+"\n");
		area.setCaretPosition(area.getDocument().getLength());
	}
	
	public void error(Exception e) {
		StringWriter s = new StringWriter();
		PrintWriter p = new PrintWriter(s);
		e.printStackTrace(p);
		String stackTrace = s.toString();
		System.err.print(stackTrace);
		area.append(stackTrace);
		area.setCaretPosition(area.getDocument().getLength());
	}

}

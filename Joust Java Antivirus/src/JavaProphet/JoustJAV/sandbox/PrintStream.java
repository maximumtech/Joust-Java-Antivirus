package JavaProphet.JoustJAV.sandbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class PrintStream extends java.io.PrintStream {
	public PrintStream(OutputStream out) {
		super(out);
	}
	
	public PrintStream(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}
	
	public PrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
	}
	
	public PrintStream(String fileName) throws FileNotFoundException {
		this(new FileOutputStream(fileName));
	}
	
	public PrintStream(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		this(new FileOutputStream(fileName), false);
	}
	
	public PrintStream(File file) throws FileNotFoundException {
		this(new FileOutputStream(file));
	}
	
	public PrintStream(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
		this(new FileOutputStream(file));
	}
}

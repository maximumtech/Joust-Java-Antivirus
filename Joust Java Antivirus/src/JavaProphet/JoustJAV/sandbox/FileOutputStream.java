package JavaProphet.JoustJAV.sandbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import JavaProphet.JoustJAV.MainWindow;

public class FileOutputStream extends java.io.FileOutputStream {
	
	private static boolean isWriteEnabled() {
		try {
			Class<?> c = Runtime.class.getClassLoader().loadClass("JavaProphet.JoustJAV.sandbox.FileWriteEnabled");
			if (c != null) {
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	String fn = "";
	
	private final boolean b;
	
	public FileOutputStream(String name) throws FileNotFoundException {
		super(name);
		b = isWriteEnabled();
		fn = name;
		System.out.println("FileWrite Sandbox: Attempting to write to " + name);
	}
	
	public FileOutputStream(String name, boolean append) throws FileNotFoundException {
		super(name, append);
		b = isWriteEnabled();
		fn = name;
		System.out.println("FileWrite Sandbox: Attempting to write to " + name);
	}
	
	public FileOutputStream(File file) throws FileNotFoundException {
		super(file);
		b = isWriteEnabled();
		fn = file != null ? file.getPath() : "null";
		System.out.println("FileWrite Sandbox: Attempting to write to " + file != null ? file.getPath() : "null");
	}
	
	public FileOutputStream(File file, boolean append) throws FileNotFoundException {
		super(file, append);
		b = isWriteEnabled();
		fn = file != null ? file.getPath() : "null";
		System.out.println("FileWrite Sandbox: Attempting to write to " + file != null ? file.getPath() : "null");
	}
	
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	public void write(int byt) throws IOException {
		if (!b) {
			super.write(byt);
		}
		out.write(byt);
	}
	
	public void write(byte[] byt) throws IOException {
		if (!b) {
			super.write(byt);
		}
		out.write(byt);
	}
	
	public void write(byte[] byt, int off, int l) throws IOException {
		if (!b) {
			super.write(byt, off, l);
		}
		out.write(byt, off, l);
	}
	
	public void flush() throws IOException {
		if (!b) super.flush();
		out.flush();
	}
	
	public void close() throws IOException {
		System.out.println("FileWrite Sandbox: Closing stream...");
		super.close();
		System.out.println("FileWrite Sandbox: Dumping unwritten data...");
		if (MainWindow.frame.textField_1.getText().length() > 0) {
			File f = new File(new File(MainWindow.frame.textField_1.getText()), new File(fn).getName());
			java.io.FileOutputStream ot = new java.io.FileOutputStream(f, true);
			ot.write(dumpWrote());
			ot.flush();
			ot.close();
		}
	}
	
	public byte[] dumpWrote() {
		byte[] o = out.toByteArray();
		out = new ByteArrayOutputStream();
		return o;
	}
}

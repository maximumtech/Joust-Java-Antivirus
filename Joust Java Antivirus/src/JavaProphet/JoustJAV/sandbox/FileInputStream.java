package JavaProphet.JoustJAV.sandbox;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileInputStream extends java.io.FileInputStream {
	
	private static boolean isReadEnabled() {
		try {
			Class<?> c = Runtime.class.getClassLoader().loadClass("JavaProphet.JoustJAV.sandbox.FileReadEnabled");
			if (c != null) {
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private final boolean b;
	
	public FileInputStream(File file) throws FileNotFoundException {
		super(file);
		b = isReadEnabled();
		System.out.println("FileRead Sandbox: Attempting to read file " + file != null ? file.getPath() : "null");
	}
	
	public FileInputStream(FileDescriptor fd) {
		super(fd);
		b = isReadEnabled();
		System.out.println("FileRead Sandbox: Attempting to read file from descriptor.");
	}
	
	public FileInputStream(String name) throws FileNotFoundException {
		super(name);
		b = isReadEnabled();
		System.out.println("FileRead Sandbox: Attempting to read file " + name);
	}
	
	public int read() throws IOException {
		if (b) {
			return -1;
		}else {
			return super.read();
		}
	}
	
	public int read(byte byt[]) throws IOException {
		if (b) {
			return 0;
		}else {
			return super.read(byt);
		}
	}
	
	public int read(byte byt[], int off, int len) throws IOException {
		if (b) {
			return 0;
		}else {
			return super.read(byt, off, len);
		}
	}
	
	public long skip(long n) throws IOException {
		if (b) {
			return 0;
		}else {
			return super.skip(n);
		}
	}
	
	public int available() throws IOException {
		if (b) {
			return 0;
		}else {
			return super.available();
		}
	}
}

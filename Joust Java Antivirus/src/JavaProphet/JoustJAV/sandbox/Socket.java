package JavaProphet.JoustJAV.sandbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import JavaProphet.JoustJAV.MainWindow;

public class Socket extends java.net.Socket {
	
	private static boolean isTCPEnabled() {
		try {
			Class<?> c = Runtime.class.getClassLoader().loadClass("JavaProphet.JoustJAV.sandbox.TCPEnabled");
			if (c != null) {
				return false;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean t = isTCPEnabled();
	
	public boolean isConnected() {
		return t ? true : super.isConnected();
	}
	
	public boolean isClosed() {
		return t ? false : super.isClosed();
	}
	
	public boolean isBound() {
		return t ? true : super.isBound();
	}
	
	public InputStream getInputStream() throws IOException {
		if (t) {
			return new ByteArrayInputStream(new byte[1048576]);
		}
		return super.getInputStream();
	}
	
	public void close() throws IOException {
		if (MainWindow.frame.textField_1.getText().length() > 0) {
			File f = new File(new File(MainWindow.frame.textField_1.getText()), "socket" + getInetAddress().getHostAddress() + "[" + getPort() + "]");
			java.io.FileOutputStream ot = new java.io.FileOutputStream(f, true);
			ot.write(out.toByteArray());
			ot.flush();
			ot.close();
		}
		super.close();
	}
	
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	public OutputStream getOutputStream() throws IOException {
		if (t) {
			Thread thrd = new Thread() {
				public void run() {
					try {
						Thread.sleep(10000L);
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						close();
					}catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			thrd.start();
			return out;
		}
		return super.getOutputStream();
	}
	
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		if (!t) {
			super.connect(endpoint, timeout);
		}
	}
	
	public void bind(SocketAddress bindpoint) throws IOException {
		if (!t) {
			super.bind(bindpoint);
		}
	}
	
}

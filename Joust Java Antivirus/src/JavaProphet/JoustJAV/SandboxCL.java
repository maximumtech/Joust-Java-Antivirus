package JavaProphet.JoustJAV;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class SandboxCL extends ClassLoader {
	
	byte[] jar = null;
	
	private HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private HashMap<String, byte[]> resources = new HashMap<String, byte[]>();
	
	SandboxPermissions perms = null;
	SandboxLog logger = null;
	
	public SandboxCL(byte[] jar, SandboxLog logger, SandboxPermissions perms) {
		this.jar = jar;
		this.logger = logger;
		this.perms = perms;
		ArrayList[] lsts = perms.getToggleClasses();
		for (int i = 0; i < lsts[0].size(); i++) {
			classes.put((String)lsts[1].get(i), (Class<?>)lsts[0].get(i));
		}
	}
	
	private JarInputStream getStream() {
		try {
			return new JarInputStream(new ByteArrayInputStream(jar));
		}catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public InputStream getResourceAsStream(String name) {
		if (!resources.containsKey(name)) {
			try {
				JarInputStream stream = getStream();
				JarEntry entry = stream.getNextJarEntry();
				ArrayList<JarEntry> ent = new ArrayList<JarEntry>();
				while (entry != null) {
					String en = entry.getName().replace("/", ".");
					if (en.contains(".")) {
						en = en.substring(0, en.lastIndexOf("."));
					}
					if (en.equals(name)) {
						break;
					}
					ent.add(entry);
					entry = stream.getNextJarEntry();
				}
				if (entry == null) {
					for (JarEntry e : ent) {
						String en = e.getName().replace("/", ".");
						if (en.contains(".")) {
							en = en.substring(0, en.lastIndexOf("."));
						}
						if (en.lastIndexOf(".") > 0 && en.substring(en.lastIndexOf(".") + 1).equals(name)) {
							entry = e;
							break;
						}
					}
				}
				InputStream strm2 = stream;
				if (entry == null) {
					strm2 = super.getResourceAsStream(name);
				}
				if (strm2 == null) return null;
				ent = null;
				ByteArrayOutputStream byt = new ByteArrayOutputStream();
				while (true) {
					int r = stream.read();
					if (r < 0) break;
					byt.write(r);
				}
				stream.close();
				byte[] reqc = byt.toByteArray();
				return new ByteArrayInputStream(reqc);
			}catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			return new ByteArrayInputStream(resources.get(name));
		}
		return null;
	}
	
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> c = findClass(name);
			if (c == null) {
				c = super.loadClass(name, resolve);
			}
			if (resolve) {
				resolveClass(c);
			}
			return c;
		}
	}
	
	public Class<?> findClass(String name) {
		if (!classes.containsKey(name)) {
			try {
				InputStream fs = null;
				JarInputStream stream = getStream();
				JarEntry entry = stream.getNextJarEntry();
				while (entry != null) {
					String en = entry.getName().replace("/", ".");
					if (en.contains(".")) {
						en = en.substring(0, en.lastIndexOf("."));
					}
					if (en.equals(name)) {
						break;
					}
					entry = stream.getNextJarEntry();
				}
				if (entry != null) {
					fs = stream;
				}
				
				ByteArrayOutputStream byt = new ByteArrayOutputStream();
				while (true) {
					int r = stream.read();
					if (r < 0) break;
					byt.write(r);
				}
				stream.close();
				byte[] reqc = perms.replace(byt.toByteArray());
				Class<?> c = defineClass(name, reqc, 0, reqc.length, SandboxCL.class.getProtectionDomain());
				classes.put(name, c);
				return c;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			return classes.get(name);
		}
		classes.put(name, null);
		return null;
	}
}

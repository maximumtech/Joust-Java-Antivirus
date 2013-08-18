package JavaProphet.JoustJAV;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class CL extends ClassLoader {
	
	byte[] jar = null;
	
	private HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private HashMap<String, byte[]> resources = new HashMap<String, byte[]>();
	
	public CL(byte[] jar) {
		this.jar = jar;
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
				if (entry == null) {
					return null;
				}
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
	
	public Class<?> findClass(String name) {
		if (!classes.containsKey(name)) {
			try {
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
				if (entry == null) {
					Class<?> c = CL.getSystemClassLoader().loadClass(name);
					if (c == null) return null;
					classes.put(name, c);
					return c;
				}
				ByteArrayOutputStream byt = new ByteArrayOutputStream();
				while (true) {
					int r = stream.read();
					if (r < 0) break;
					byt.write(r);
				}
				stream.close();
				byte[] reqc = byt.toByteArray();
				Class<?> c = defineClass(name, reqc, 0, reqc.length, CL.class.getProtectionDomain());
				classes.put(name, c);
				return c;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			return classes.get(name);
		}
		return null;
	}
}

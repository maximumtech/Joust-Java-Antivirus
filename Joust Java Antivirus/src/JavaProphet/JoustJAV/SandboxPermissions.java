package JavaProphet.JoustJAV;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SandboxPermissions {
	public static enum Perms {
		RUNTIME, PROCESSBUILDER, DESKTOP, FILE_WRITE, FILE_READ, REFLECTION, CLASSLOADING, UDP, TCP, URLCONNECTION, JNI
	}
	
	ArrayList<Perms> perms = new ArrayList<Perms>();
	
	public SandboxPermissions(Perms... perm) {
		perms.addAll(Arrays.asList(perm));
	}
	
	public boolean isPerm(Perms perm) {
		return perms.contains(perm);
	}
	
	private static HashMap<String, String> repl = new HashMap<String, String>();
	static {
		repl.put("java/lang/Runtime", "JavaProphet/JoustJAV/sandbox/Runtime");
		repl.put("java/lang/ProcessBuilder", "JavaProphet/JoustJAV/sandbox/ProcessBuilder");
		repl.put("java/awt/Desktop", "JavaProphet/JoustJAV/sandbox/Desktop");
		repl.put("java/io/FileInputStream", "JavaProphet/JoustJAV/sandbox/FileInputStream");
		repl.put("java/io/FileOutputStream", "JavaProphet/JoustJAV/sandbox/FileOutputStream");
		repl.put("java/io/PrintStream", "JavaProphet/JoustJAV/sandbox/PrintStream");
		repl.put("java/net/Socket", "JavaProphet/JoustJAV/sandbox/Socket");
	}
	
	public byte[] replace(byte[] cls) {
		byte[] vol = new byte[cls.length * 2];
		int rl = cls.length;
		int i3 = 0;
		for (int i = 0; i < cls.length; i++) {
			boolean wasTaken = false;
			for (String key : repl.keySet()) {
				String value = repl.get(key);
				byte[] tmp = new byte[Math.min(key.length(), Math.max(0, cls.length - (i + key.length())))];
				System.arraycopy(cls, i, tmp, 0, tmp.length);
				try {
					String utf = new String(tmp, "UTF-8");
					if (utf.equals(key)) {
						System.arraycopy(value.getBytes(), 0, vol, i3, value.getBytes().length);
						byte[] tmp2 = new byte[Math.min(128, i3)];
						System.arraycopy(vol, i3 - tmp2.length, tmp2, 0, tmp2.length);
						String f128 = new String(tmp2, "UTF-8");
						tmp2 = new byte[Math.min(128, cls.length - (i + key.getBytes().length))];
						System.arraycopy(cls, i + key.getBytes().length, tmp2, 0, tmp2.length);
						String l128 = new String(tmp2, "UTF-8");
						byte rs = (byte)(value.length() - key.length());
						if (f128.endsWith("L")) {
							if (f128.endsWith("()L") && l128.startsWith(";")) {
								vol[i3 - 4] = (byte)(rs + vol[i3 - 4]);
							}else if (f128.endsWith("(L") && l128.startsWith(";")) {
								int ind = f128.length() - (f128.lastIndexOf("(") - 1);
								vol[i3 - ind] = (byte)(rs + vol[i3 - ind]); // possible error
							}else if (f128.endsWith(";L") && l128.startsWith(";)")) {
								int ind = f128.length() - (f128.lastIndexOf("(") - 1);
								vol[i3 - ind] = (byte)(rs + vol[i3 - ind]); // possible error
							}else if (f128.endsWith(";)L") && l128.startsWith(";")) {
								int ind = f128.length() - (f128.lastIndexOf("(") - 1);
								vol[i3 - ind] = (byte)(rs + vol[i3 - ind]); // possible error
							}else if (f128.endsWith(";L") && l128.startsWith(";")) {
								int ind = f128.length() - (f128.lastIndexOf("(") - 1);
								vol[i3 - ind] = (byte)(rs + vol[i3 - ind]); // possible error
							}else if (f128.endsWith("L") && l128.startsWith(";")) {
								vol[i3 - 2] = (byte)(rs + vol[i3 - 2]);
							}
						}else {
							vol[i3 - 1] = (byte)value.length();
						}
						rl += (value.length() - key.length());
						i += key.length() - 1;
						i3 += value.length();
						wasTaken = true;
					}else if (utf.equals(key.replace("/", "."))) {
						System.arraycopy(value.getBytes(), 0, vol, i3, value.getBytes().length);
						byte rs = (byte)(value.length() - key.length());
						vol[i3 - 1] = (byte)(vol[i3 - 1] + rs);
						rl += (value.length() - key.length());
						i += key.length() - 1;
						i3 += value.length();
						wasTaken = true;
					}
				}catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			if (!wasTaken) {
				vol[i3] = cls[i];
				i3++;
			}
		}
		byte[] end = new byte[rl];
		System.arraycopy(vol, 0, end, 0, rl);
		return end;
	}
	
	public ArrayList[] getToggleClasses() {
		ArrayList<Class<?>> ar = new ArrayList<Class<?>>();
		ArrayList<String> ar2 = new ArrayList<String>();
		if (isPerm(Perms.RUNTIME)) {
			ar.add(JavaProphet.JoustJAV.sandbox.RuntimeEnabled.class);
			ar2.add("JavaProphet.JoustJAV.sandbox.RuntimeEnabled");
		}
		if (isPerm(Perms.PROCESSBUILDER)) {
			ar.add(JavaProphet.JoustJAV.sandbox.ProcessBuilderEnabled.class);
			ar2.add("JavaProphet.JoustJAV.sandbox.ProcessBuilderEnabled");
		}
		if (isPerm(Perms.JNI)) {
			ar.add(JavaProphet.JoustJAV.sandbox.JNIEnabled.class);
			ar2.add("JavaProphet.JoustJAV.sandbox.JNIEnabled");
		}
		if (isPerm(Perms.DESKTOP)) {
			ar.add(JavaProphet.JoustJAV.sandbox.DesktopEnabled.class);
			ar2.add("JavaProphet.JoustJAV.sandbox.DesktopEnabled");
		}
		if (isPerm(Perms.FILE_READ)) {
			ar.add(JavaProphet.JoustJAV.sandbox.FileReadEnabled.class);
			ar2.add("JavaProphet.JoustJAV.sandbox.FileReadEnabled");
		}
		if (isPerm(Perms.FILE_WRITE)) {
			ar.add(JavaProphet.JoustJAV.sandbox.FileWriteEnabled.class);
			ar2.add("JavaProphet.JoustJAV.sandbox.FileWriteEnabled");
		}
		if (isPerm(Perms.TCP)) {
			ar.add(JavaProphet.JoustJAV.sandbox.FileWriteEnabled.class);
			ar2.add("JavaProphet.JoustJAV.sandbox.TCPEnabled");
		}
		return new ArrayList[]{ar, ar2};
	}
}

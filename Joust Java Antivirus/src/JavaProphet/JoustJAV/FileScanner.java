package JavaProphet.JoustJAV;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import JavaProphet.JoustJAV.SandboxPermissions.Perms;

public class FileScanner {
	public static FileScanner ins = new FileScanner();
	
	public static class ScanResult {
		private String name = "";
		private String desc = "";
		private int risk = 0;
		private String file;
		
		private ScanResult(String name, String desc, int risk) {
			this.name = name;
			this.desc = desc;
			this.risk = risk;
		}
		
		public String getName() {
			return name;
		}
		
		public String getDesc() {
			return desc;
		}
		
		public String getFile() {
			return file;
		}
		
		public int getRisk() {
			return risk;
		}
		
		public String toString() {
			return name + ": " + getRiskString() + "(" + getRisk() + "): " + file;
		}
		
		public String getRiskString() {
			switch (risk) {
				case -1000:
					return "Undefined Risk";
				case -3:
					return "High Risk Deterrent";
				case -2:
					return "Medium Risk Deterrent";
				case -1:
					return "Low Risk Deterrent";
				case 0:
					return "Little to No Risk";
				case 1:
					return "Low Risk";
				case 2:
					return "Medium Risk";
				case 3:
					return "High Risk";
				default:
					return "High Risk x" + (risk - 2);
			}
		}
		
		public ScanResult clone() {
			return new ScanResult(name, desc, risk);
		}
		
		public ScanResult inscribeFile(String file) {
			ScanResult r = clone();
			r.file = file;
			return r;
		}
		
		public static ScanResult EXE_CONTAINED = new ScanResult("EXE Contained", "Java is meant to be a write once run everywhere language, but this jar contains an executable for windows only, signs of a possible dropper.", 1);
		public static ScanResult SH_CONTAINED = new ScanResult("SH Contained", "Java is meant to be a write once run everywhere language, but this jar contains an executable for linux only, signs of a possible dropper.", 1);
		public static ScanResult APP_CONTAINED = new ScanResult("APP Contained", "Java is meant to be a write once run everywhere language, but this jar contains an executable for mac only, signs of a possible dropper.", 1);
		public static ScanResult UNPACKAGED = new ScanResult("Unpackaged Class Files", "There are unpackaged class files, so this is probably an amatuer program, possibly a virus, however unlikely.", 1);
		public static ScanResult JAR_CONTAINED = new ScanResult("JAR Contained", "This will usually not be a problem, but it contains a internal JAR file. This will be scanned.", 1);
		public static ScanResult SOURCE_CONTAINED = new ScanResult("Source Files Contained", "This is a good thing, or means whomever compiled the virus is not very intelligent.", -1000);
		public static ScanResult HEXE_CONTAINED = new ScanResult("Hidden EXE Contained", "Java is meant to be a write once run everywhere language, but this jar contains an executable for windows only, signs of a possible dropper. This file was hidden under another extension.", 2);
		
		// unimplemented
		public static ScanResult HSH_CONTAINED = new ScanResult("Hidden SH Contained", "Java is meant to be a write once run everywhere language, but this jar contains an executable for linux only, signs of a possible dropper. This file was hidden under another extension.", 2);
		public static ScanResult HAPP_CONTAINED = new ScanResult("Hidden App Contained", "Java is meant to be a write once run everywhere language, but this jar contains an executable for mac only, signs of a possible dropper. This file was hidden under another extension.", 2);
		//
		
		public static ScanResult CUSTOM_CLASSLOADER = new ScanResult("Custom Classloader", "There is a possibility that this program uses a memory loads to make jars undetectable at scantime AND runtime. In most cases not, but in the rare occurance, can be fatal.", 1);
		public static ScanResult CUSTOM_CLASSLOADER_CUSTOM_LOADER = new ScanResult("Custom Classloader w/ Class Declarance Overwrite", "This is a very risky program, it most likely is a memory loader or a advanced web loader, and should be taken EXTREMELY seriously. Some legitimate programs use this features for things such as updates, it's best to manually view the file, run it in sandbox, or consult to creator.", 4);
		public static ScanResult DDOS_DETECTED = new ScanResult("DDOS method detected", "There is an evidant DDOS method present in the file, and is very likely a bot.", 3);
		
		public static ScanResult DEFAULT = new ScanResult("asdfadf", "fdgadfg", 1);
	}
	
	public static class ScanResults implements Iterable<ScanResult> {
		private ArrayList<ScanResult> results = new ArrayList<ScanResult>();
		
		public ScanResults(ScanResult... res) {
			results = new ArrayList<ScanResult>(Arrays.asList(res));
		}
		
		public ScanResults() {
		}
		
		public boolean hasResult(ScanResult res) {
			return results.contains(res);
		}
		
		public int getRiskAverage() {
			int tot = 0;
			int tota = 0;
			for (ScanResult r : results) {
				if (r.getRisk() != -1000) {
					tota++;
					tot += r.getRisk();
				}
			}
			tot /= tota;
			return tot;
		}
		
		public String getWorstDescriptionAndRisk() {
			int wr = -1;
			int ind = -1;
			int tind = 0;
			for (ScanResult r : results) {
				if (r.getRisk() > wr) {
					ind = tind;
					wr = r.getRisk();
				}
				tind++;
			}
			if (ind >= 0) {
				ScanResult worst = results.get(ind);
				if (worst != null) {
					return worst.getRiskString() + "@" + worst.getFile() + ": " + worst.getName() + " Description: " + worst.getDesc();
				}
			}
			return "100%(As far as we can tell) this file is virus-free.";
		}
		
		public boolean hasGeneralResult(ScanResult res) {
			boolean b = false;
			for (ScanResult r : results) {
				if (r.name.equals(res.name)) {
					b = true;
					break;
				}
			}
			return b;
		}
		
		public void addResults(ScanResult... res) {
			results.addAll(Arrays.asList(res));
		}
		
		public ScanResult[] getResults() {
			return (ScanResult[])results.toArray();
		}
		
		public void combine(ScanResults r) {
			results.addAll(r.results);
		}
		
		public Iterator<ScanResult> iterator() {
			return results.iterator();
		}
	}
	
	public ScanResults scanFile(InputStream bin) {
		try {
			JarEntry e = null;
			ScanResults res = new ScanResults();
			ByteArrayOutputStream o2 = new ByteArrayOutputStream();
			int lbr2 = 0;
			while ((lbr2 = bin.read()) > -1) {
				o2.write(lbr2);
			}
			byte[] jar = o2.toByteArray();
			o2 = null;
			JarInputStream in = new JarInputStream(new ByteArrayInputStream(jar));
			CL cl = new CL(jar);
			while ((e = in.getNextJarEntry()) != null) {
				String nb = e.getName();
				String fname = nb.trim();
				String ext = fname.lastIndexOf(".") > -1 ? fname.substring(fname.lastIndexOf(".") + 1).trim().toLowerCase() : "";
				fname = fname.lastIndexOf(".") > -1 ? fname.substring(0, fname.lastIndexOf(".")).trim() : fname;
				fname = fname.replace("/", ".");
				int tt = fname.lastIndexOf(".");
				String pkg = tt > -1 ? fname.substring(0, tt).trim() : "";
				String name = tt > -1 ? fname.substring(tt + 1).trim() : "";
				byte[] data = null;
				String dsample = "";
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				int lbr = 0;
				while ((lbr = in.read()) > -1) {
					o.write(lbr);
				}
				data = o.toByteArray();
				o = null;
				dsample = new String(data, 0, Math.min(data.length, 256));
				if (ext.equals("exe")) {
					res.addResults(ScanResult.EXE_CONTAINED.inscribeFile(nb));
				}else if (ext.equals("sh")) {
					res.addResults(ScanResult.SH_CONTAINED.inscribeFile(nb));
				}else if (ext.equals("app")) {
					res.addResults(ScanResult.APP_CONTAINED.inscribeFile(nb));
				}else if (ext.equals("jar")) {
					res.addResults(ScanResult.JAR_CONTAINED.inscribeFile(nb));
					res.combine(scanFile(new ByteArrayInputStream(data)));
				}
				if (pkg.equals("") && ext.equals("class")) {
					res.addResults(ScanResult.UNPACKAGED.inscribeFile(nb));
				}
				if (ext.equals("java")) {
					res.addResults(ScanResult.SOURCE_CONTAINED.inscribeFile(nb));
				}
				if (data.length > 100 && !ext.equals("exe")) {
					int r = 0;
					if (dsample.startsWith("MZ")) {
						r++;
					}
					if (dsample.contains("This program cannot be run in DOS mode")) {
						r++;
					}
					if (dsample.contains("PE")) {
						r++;
					}
					if (r > 1) {
						res.addResults(ScanResult.HEXE_CONTAINED.inscribeFile(nb));
					}
				}
				// check for hidden sh / app
				if (ext.equals("class")) {
					Class<?> cls = cl.findClass(fname);
					if (cls != null) {
						res.combine(scanClass(cls, nb));
					}
				}
			}
			return res;
		}catch (Exception e) {
			JOptionPane.showMessageDialog(null, "There was an error reading or processing the file: " + e.getMessage() + "@" + e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
			e.printStackTrace();
		}
		return null;
	}
	
	public ScanResults scanClass(Class<?> cls, String nb) throws Exception {
		ScanResults res = new ScanResults();
		boolean isCL = false;
		// cls.getSuperclass() != null && cls.getSuperclass().getName().equals("java.lang.ClassLoader")
		if (ClassLoader.class.isAssignableFrom(cls)) {
			isCL = true;
		}
		boolean bCL = false;
		for (Method m : cls.getMethods()) {
			String mn = m.getName();
			if (isCL) {
				if (mn.contains("loadClass") || mn.contains("defineClass") || mn.contains("findClass")) {
					bCL = true;
				}
			}
			String mnl = mn.toLowerCase();
			if (mnl.contains("rudy") || mnl.contains("flood") || mnl.contains("slowloris") || mnl.contains("ddos")) {
				res.addResults(ScanResult.DDOS_DETECTED.inscribeFile(nb));
			}
		}
		if (isCL) {
			if (!bCL) {
				res.addResults(ScanResult.CUSTOM_CLASSLOADER.inscribeFile(nb));
			}else {
				res.addResults(ScanResult.CUSTOM_CLASSLOADER_CUSTOM_LOADER.inscribeFile(nb));
			}
		}
		String n = cls.getName();
		String nl = n.toLowerCase();
		if (nl.contains("rudy") || nl.contains("flood") || nl.contains("slowloris") || nl.contains("ddos")) {
			res.addResults(ScanResult.DDOS_DETECTED.inscribeFile(nb));
		}
		return res;
	}
	
	public ScanResults scanFile(File f) {
		if (f.getName().endsWith(".jar") || f.getName().endsWith(".class")) {
			try {
				FileInputStream in = new FileInputStream(f);
				ScanResults r = null;
				if (f.getName().endsWith(".jar")) {
					r = scanFile(in);
				}else {
					URLClassLoader cl = new URLClassLoader(new URL[]{new URL(f.getParentFile().toString())});
					Class<?> cls = cl.loadClass(f.getName().substring(0, f.getName().lastIndexOf(".")));
					if (cls != null) {
						r = scanClass(cls, f.getName());
					}
				}
				in.close();
				return r;
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			JOptionPane.showMessageDialog(null, "This program can only scan compiled java files, for other files, please use a conventional antivirus, such as Avast.");
		}
		return null;
	}
	
	SandboxLog logger = new SandboxLog();
	
	public void sandboxFile(InputStream bin) {
		try {
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			int lbr = 0;
			while ((lbr = bin.read()) > -1) {
				o.write(lbr);
			}
			final byte[] jar = o.toByteArray();
			o = null;
			Thread t = new Thread() {
				public void run() {
					try {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								logger.setVisible(true);
							}
						});
						SandboxCL cl = new SandboxCL(jar, logger, new SandboxPermissions(Perms.RUNTIME, Perms.JNI, Perms.PROCESSBUILDER, Perms.DESKTOP, Perms.FILE_READ, Perms.FILE_WRITE));
						JarInputStream in = new JarInputStream(new ByteArrayInputStream(jar));
						String mc = in.getManifest().getMainAttributes().getValue("Main-Class");
						Class<?> main = cl.loadClass(mc);
						Method mainm = main.getMethod("main", new Class<?>[]{String[].class});
						mainm.invoke(null, new Object[]{new String[]{}});
						in.close();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sandboxFile(File f) {
		if (f.getName().endsWith(".jar") || f.getName().endsWith(".class")) {
			try {
				FileInputStream in = new FileInputStream(f);
				if (f.getName().endsWith(".jar")) {
					sandboxFile(in);
				}else {
					URLClassLoader cl = new URLClassLoader(new URL[]{new URL(f.getParentFile().toString())});
					Class<?> cls = cl.loadClass(f.getName().substring(0, f.getName().lastIndexOf(".")));
					if (cls != null) {
						// r = scanClass(cls, f.getName());
					}
				}
				in.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			JOptionPane.showMessageDialog(null, "This program can only run compiled java files, for other files, please use a conventional antivirus, such as Avast.");
		}
	}
}

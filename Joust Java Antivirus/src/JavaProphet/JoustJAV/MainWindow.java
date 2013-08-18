package JavaProphet.JoustJAV;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

public class MainWindow extends JFrame {
	
	private JPanel contentPane;
	private JTextField textField;
	private JButton btnNewButton;
	private JFileChooser chsr;
	private JButton btnRunInSandbox;
	public JTextField textField_1;
	private JLabel lblDirectoryForLogs;
	private JButton button_1;
	public static MainWindow frame;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new MainWindow();
					frame.setVisible(true);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private static class FF extends FileFilter {
		
		@Override
		public boolean accept(File f) {
			return f.getName().endsWith(".jar") || f.getName().endsWith(".class");
		}
		
		@Override
		public String getDescription() {
			return "Compiled Java files";
		}
		
	}
	
	/**
	 * Create the frame.
	 */
	public MainWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		setTitle("Joust Java Antivirus");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		chsr = new JFileChooser();
		chsr.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		
		JLabel lblNewLabel = new JLabel("Filename:");
		lblNewLabel.setBounds(10, 11, 46, 14);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(66, 8, 312, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton button = new JButton("...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chsr.setApproveButtonText("Select");
				chsr.setFileFilter(new FF());
				chsr.setDialogTitle("Select java file");
				chsr.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (chsr.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File f = chsr.getSelectedFile();
					if (f.exists() && f.isFile()) {
						textField.setText(f.toString());
					}
				}
			}
		});
		button.setBounds(388, 7, 46, 23);
		contentPane.add(button);
		
		btnNewButton = new JButton("Scan File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = new File(textField.getText());
				if (f.exists() && f.isFile()) {
					FileScanner.ScanResults r = FileScanner.ins.scanFile(f);
					ResultViewer vr = new ResultViewer(r);
					vr.setVisible(true);
				}
			}
		});
		btnNewButton.setBounds(345, 40, 89, 23);
		contentPane.add(btnNewButton);
		
		btnRunInSandbox = new JButton("Run in sandbox");
		btnRunInSandbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = new File(textField.getText());
				if (f.exists() && f.isFile()) {
					FileScanner.ins.sandboxFile(f);
				}
			}
		});
		btnRunInSandbox.setBounds(234, 40, 107, 23);
		contentPane.add(btnRunInSandbox);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(120, 241, 258, 20);
		contentPane.add(textField_1);
		
		lblDirectoryForLogs = new JLabel("Directory for Logs:");
		lblDirectoryForLogs.setBounds(10, 244, 100, 14);
		contentPane.add(lblDirectoryForLogs);
		
		button_1 = new JButton("...");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chsr.setApproveButtonText("Select");
				chsr.setFileFilter(null);
				chsr.setDialogTitle("Select folder for logs");
				chsr.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (chsr.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File f = chsr.getSelectedFile();
					if (f.exists() && f.isDirectory()) {
						textField_1.setText(f.toString());
					}
				}
			}
		});
		button_1.setBounds(388, 240, 46, 23);
		contentPane.add(button_1);
	}
}

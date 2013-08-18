package JavaProphet.JoustJAV;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class SandboxLog extends JFrame {
	
	private JPanel contentPane;
	
	public JTextArea textArea;
	private JPanel panel;
	private JTextField textField;
	private JButton btnSend;
	
	/**
	 * Create the frame.
	 */
	public SandboxLog() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				textArea.setText("");
			}
		});
		setTitle("Sandbox Log");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		PrintStream old = System.out;
		System.setOut(new PrintStream(new RedistributeOutputStream(textArea, old), true));
		System.setErr(new PrintStream(new RedistributeOutputStream(textArea, old), true));
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		panel.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.setIn(new ByteArrayInputStream((textField.getText() + "\n").getBytes()));
				System.out.println(textField.getText());
				textField.setText("");
			}
		});
		panel.add(btnSend, BorderLayout.EAST);
	}
	
	public static class RedistributeOutputStream extends OutputStream {
		JTextArea area;
		PrintStream old;
		
		public RedistributeOutputStream(JTextArea area, PrintStream old) {
			this.area = area;
			this.old = old;
		}
		
		@Override
		public void write(int b) throws IOException {
			area.setText(area.getText() + (char)b);
			old.print((char)b);
		}
		
	}
	
}

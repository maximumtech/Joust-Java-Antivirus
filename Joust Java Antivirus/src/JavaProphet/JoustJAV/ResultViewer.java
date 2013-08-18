package JavaProphet.JoustJAV;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import JavaProphet.JoustJAV.FileScanner.ScanResult;
import JavaProphet.JoustJAV.FileScanner.ScanResults;

public class ResultViewer extends JFrame {
	
	private JPanel contentPane;
	private JTable table;
	private JTable table_1;
	private JTextField textField;
	
	/**
	 * Create the frame.
	 */
	public ResultViewer(final ScanResults results) {
		setTitle("Scan Results");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"Name", "Description", "Risk", "Infected File"}) {
			private static final long serialVersionUID = 1012710649595506398L;
			Class<?>[] columnTypes = new Class[]{String.class, String.class, String.class, Object.class};
			
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			
			boolean[] columnEditables = new boolean[]{false, false, false, false};
			
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		for (Object o : results) {
			ScanResult res = (ScanResult)o;
			((DefaultTableModel)table.getModel()).addRow(new Object[]{res.getName(), res.getDesc(), res.getRiskString(), res.getFile()});
		}
		table.getColumnModel().getColumn(0).setPreferredWidth(110);
		table.getColumnModel().getColumn(1).setPreferredWidth(177);
		table.getColumnModel().getColumn(2).setPreferredWidth(94);
		table.getColumnModel().getColumn(3).setPreferredWidth(200);
		scrollPane.setViewportView(table);
		
		textField = new JTextField();
		textField.setText(results.getWorstDescriptionAndRisk());
		textField.setEditable(false);
		contentPane.add(textField, BorderLayout.SOUTH);
		textField.setColumns(10);
	}
	
}

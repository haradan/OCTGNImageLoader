package uk.co.haradan.octgnimageloader.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import uk.co.haradan.octgnimageloader.config.OCTGNImageLoaderConfig;

public class PluginChooser {
	
	private PluginChoiceListener listener;
	
	public static interface PluginChoiceListener {
		public void chosenPlugin(OCTGNImageLoaderConfig config);
	}
	
	public void setListener(PluginChoiceListener listener) {
		this.listener = listener;
	}
	
	public void chooseOctgnPlugin(JFrame parent) {
		final JDialog dialog = new JDialog(parent, true);
		dialog.setTitle("Please choose which plugin to load images for");
		
		final OCTGNImageLoaderConfig[] showConfigs = OCTGNImageLoaderConfig.BUILTIN_CONFIGS;
		
		TableModel model = new AbstractTableModel() {
			private static final long serialVersionUID = 1239735833071248351L;
			
			@Override
			public String getColumnName(int column) {
				switch(column) {
				case 0:
					return "Plugin Name";
				case 1:
					return "Website API URL";
				}
				return null;
			}

			@Override
			public int getColumnCount() {
				return 2;
			}

			@Override
			public int getRowCount() {
				return showConfigs.length;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				OCTGNImageLoaderConfig config = showConfigs[rowIndex];
				switch(columnIndex) {
				case 0:
					return config.getPluginName();
				case 1:
					return config.getCardsUrl();
				}
				return null;
			}
		};
		
		final JTable table = new JTable(model);
		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					selectPlugin(table, showConfigs);
					dialog.dispose();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
		});
		
		dialog.add(table.getTableHeader(), BorderLayout.NORTH);
		dialog.add(table, BorderLayout.CENTER);
		
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectPlugin(table, showConfigs);
				dialog.dispose();
			}
		});
		dialog.add(okBtn, BorderLayout.SOUTH);
		
		dialog.setSize(400, 200);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	private void selectPlugin(JTable table, OCTGNImageLoaderConfig[] showConfigs) {
		int selectedRow = table.getSelectedRow();
		if(selectedRow > -1) {
			OCTGNImageLoaderConfig config = showConfigs[selectedRow];
			listener.chosenPlugin(config);
		}
	}

}

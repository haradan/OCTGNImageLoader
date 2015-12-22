package uk.co.haradan.octgnimageloader.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import uk.co.haradan.octgnimageloader.LogOutput;
import uk.co.haradan.octgnimageloader.OCTGNImageLoader.SetLoadListener;
import uk.co.haradan.octgnimageloader.Set;

public class SetSelector implements SetLoadListener {
	
	private List<Set> loadedSets;
	private boolean[] setsSelected;
	
	public synchronized List<Set> getSelectedSets() {
		if(loadedSets == null) return null;
		int numSets = loadedSets.size();
		List<Set> sets = new ArrayList<Set>(numSets);
		for(int i=0; i<numSets; i++) {
			if(setsSelected[i]) {
				sets.add(loadedSets.get(i));
			}
		}
		return sets;
	}

	@Override
	public void onSetsLoaded(File loadedDir, List<Set> sets) {
		if(sets == null) return;
		synchronized(this) {
			loadedSets = sets;
			int numSets = sets.size();
			setsSelected = new boolean[numSets];
			for(int i=0; i<numSets; i++) setsSelected[i] = true;
		}
	}

	@Override
	public void onSetLoadFailed(File failedDir, Exception e) {
	}
	
	public void chooseSets(JFrame parent, LogOutput log) {
		final JDialog dialog = new JDialog(parent, true);
		dialog.setTitle("Please choose which sets to load images for");
		
		final List<Set> showSets;
		final boolean[] selected;
		synchronized(this) {
			showSets = loadedSets;
			selected = setsSelected;
		}
		
		if(showSets == null) {
			log.errorln("No sets loaded");
			return;
		}
		
		final int numSets = showSets.size();
		
		final AbstractTableModel model = new AbstractTableModel() {
			private static final long serialVersionUID = 1239735833071248351L;
			
			@Override
			public String getColumnName(int column) {
				switch(column) {
				case 0:
					return "Set Name";
				case 1:
					return "Size";
				case 2:
					return "Selected";
				}
				return null;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch(columnIndex) {
				case 0:
					return String.class;
				case 1:
					return Integer.class;
				case 2:
					return Boolean.class;
				}
				return null;
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 2;
			}

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public int getRowCount() {
				return numSets;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				Set set = showSets.get(rowIndex);
				switch(columnIndex) {
				case 0:
					return set.getName();
				case 1:
					return set.getCards().size();
				case 2:
					return selected[rowIndex];
				}
				return null;
			}
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				if(columnIndex == 2) {
					selected[rowIndex] = (Boolean) aValue;
				}
			}
		};
		
		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		
		JButton selectAllBtn = new JButton("Select All");
		selectAllBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(int i=0; i<selected.length; i++) {
					selected[i] = true;
				}
				model.fireTableDataChanged();
			}
		});
		
		JButton selectNoneBtn = new JButton("Select None");
		selectNoneBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(int i=0; i<selected.length; i++) {
					selected[i] = false;
				}
				model.fireTableDataChanged();
			}
		});
		
		JPanel btnPanel = new JPanel(new BorderLayout());
		btnPanel.add(selectAllBtn, BorderLayout.NORTH);
		btnPanel.add(selectNoneBtn, BorderLayout.CENTER);
		btnPanel.add(okBtn, BorderLayout.SOUTH);

		JPanel panel = new JPanel(new BorderLayout());
		JTable table = new JTable(model);
		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(100);
		colModel.getColumn(1).setPreferredWidth(5);
		colModel.getColumn(2).setPreferredWidth(5);
		panel.add(table.getTableHeader(), BorderLayout.NORTH);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		dialog.add(panel, BorderLayout.CENTER);
		dialog.add(btnPanel, BorderLayout.SOUTH);
		
		dialog.setSize(330, 500);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

}

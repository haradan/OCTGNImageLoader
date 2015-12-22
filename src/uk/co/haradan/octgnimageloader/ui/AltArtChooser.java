package uk.co.haradan.octgnimageloader.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import uk.co.haradan.octgnimageloader.OCTGNImageLoader.SetLoadListener;
import uk.co.haradan.octgnimageloader.Set;
import uk.co.haradan.octgnimageloader.altart.AltArtChoice;
import uk.co.haradan.octgnimageloader.altart.AltArtSourceXML;

public class AltArtChooser implements SetLoadListener {
	
	private List<Set> loadedSets;
	private final Map<String, List<AltArtChoice>> scanWebpages = new HashMap<String, List<AltArtChoice>>();
	private final Map<File, AltArtChoice> loadFiles = new HashMap<File, AltArtChoice>();

	@Override
	public void onSetsLoaded(File loadedDir, List<Set> sets) {
		if(sets == null) return;
		synchronized(this) {
			loadedSets = sets;
		}
	}

	@Override
	public void onSetLoadFailed(File attemptedDir, Exception e) {
	}
	
	public void scanWebpage(String url) throws Exception {

		final List<Set> loadedSets;
		synchronized(this) {
			loadedSets = this.loadedSets;
		}
		
		List<AltArtSourceXML> sourcesScanned = AltArtSourceXML.scanWebpage(url);
		List<AltArtChoice> choicesScanned = new ArrayList<AltArtChoice>(sourcesScanned.size());
		for(AltArtSourceXML source : sourcesScanned) {
			AltArtChoice choice = AltArtChoice.load(source, loadedSets);
			choicesScanned.add(choice);
		}
		synchronized(this) {
			scanWebpages.put(url, choicesScanned);
		}
	}
	
	public void chooseAltArt(JFrame parent) {
		final JDialog dialog = new JDialog(parent, true);
		dialog.setTitle("Choose alt art to use");
		
		JButton scanWebButton = new JButton("Scan web page for alt art");
		JButton loadFromFileBtn = new JButton("Load alt art from file");
		
		JPanel loadPanel = new JPanel(new BorderLayout());
		loadPanel.add(scanWebButton, BorderLayout.NORTH);
		loadPanel.add(loadFromFileBtn, BorderLayout.CENTER);
		dialog.add(loadPanel, BorderLayout.NORTH);
		
		final AbstractTableModel sourcesModel = new AbstractTableModel() {
			
			private static final long serialVersionUID = 2576891224505040361L;

			@Override
			public String getColumnName(int column) {
				switch(column) {
				case 0:
					return "Source Location";
				}
				return null;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch(columnIndex) {
				case 0:
					return String.class;
				}
				return null;
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public int getRowCount() {
				return scanWebpages.size() + loadFiles.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				int numWebpages = scanWebpages.size();
				if(rowIndex < numWebpages) {
					return scanWebpages.get(rowIndex);
				} else {
					int fileIndex = rowIndex - numWebpages;
					return loadFiles.get(fileIndex);
				}
			}
			
			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		};

		JPanel sourcesTablePanel = new JPanel(new BorderLayout());
		JTable sourcesTable = new JTable(sourcesModel);
		TableColumnModel colModel = sourcesTable.getColumnModel();
		colModel.getColumn(0).setPreferredWidth(100);
		colModel.getColumn(1).setPreferredWidth(5);
		colModel.getColumn(2).setPreferredWidth(5);
		sourcesTablePanel.add(sourcesTable.getTableHeader(), BorderLayout.NORTH);
		sourcesTablePanel.add(new JScrollPane(sourcesTable), BorderLayout.CENTER);
		dialog.add(sourcesTablePanel, BorderLayout.CENTER);
		
		JPanel editPanel = new JPanel(new BorderLayout());
//		editPanel.add(deleteBtn, BorderLayout.NORTH);
//		editPanel.add(editBtn, BorderLayout.CENTER);
//		editPanel.add(okBtn, BorderLayout.SOUTH);
		dialog.add(editPanel, BorderLayout.SOUTH);
	}
	
	public List<AltArtChoice> getAltArtSelections() {
		return loadedAltArt;
	}
	
}

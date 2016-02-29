package uk.co.haradan.octgnimageloader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import uk.co.haradan.octgnimageloader.config.OCTGNImageLoaderConfig;
import uk.co.haradan.octgnimageloader.config.SetSelector;

public class OCTGNImageLoaderUI implements ActionListener, WindowListener {

	private static final String ACTION_CHOOSE_OCTGN_DIR = "chooseOctgnDir";
	private static final String ACTION_CHOOSE_PLUGIN = "choosePlugin";
	private static final String ACTION_CHOOSE_SETS = "chooseSets";
	private static final String ACTION_STARTSTOP = "startStop";
	
	private final JFrame frame;
	private final LogOutput log;
	private final OCTGNImageLoader loader;
	private final JTextField dirTxt;
	private final JTextField pluginTxt;
	private List<Set> loadedSets;
	private boolean[] setsSelected;
	private Worker worker;
	
	public OCTGNImageLoaderUI(OCTGNImageLoader useLoader) {
		loader = useLoader;
		
		frame = new JFrame("OCTGN Card Image Loader");
		Container content = frame.getContentPane();
		
		JTextArea logArea = new JTextArea();
		logArea.setEditable(false);
		JScrollPane logScroll = new JScrollPane(logArea);
		content.add(logScroll, BorderLayout.CENTER);
		
		log = new LogOutput(logArea);
		log.println("Note that by using this tool you assert the right to all of the card images requested.");
		log.println("Unless you have express permission from the rights holders, please choose only the sets you own.");
		log.println();
		
		dirTxt = new JTextField();
		
		File myDocsFolder = new JFileChooser().getFileSystemView().getDefaultDirectory();
		File octgnDir = new File(myDocsFolder, "OCTGN");
		setDirectory(octgnDir);
		
		JButton chooseDirBtn = new JButton("Choose OCTGN data directory");
		chooseDirBtn.setActionCommand(ACTION_CHOOSE_OCTGN_DIR);
		chooseDirBtn.addActionListener(this);
		
		pluginTxt = new JTextField();
		pluginTxt.setEditable(false);
		pluginTxt.setText(loader.getOctgnPluginConfig().getPluginName());
		
		JButton choosePluginBtn = new JButton("Choose OCTGN plugin");
		choosePluginBtn.setActionCommand(ACTION_CHOOSE_PLUGIN);
		choosePluginBtn.addActionListener(this);
		
		JButton chooseSetsBtn = new JButton("Choose sets");
		chooseSetsBtn.setActionCommand(ACTION_CHOOSE_SETS);
		chooseSetsBtn.addActionListener(this);

		JButton startStopBtn = new JButton("Start/Stop");
		startStopBtn.setActionCommand(ACTION_STARTSTOP);
		startStopBtn.addActionListener(this);

		JPanel configSelections = new JPanel();
		configSelections.setLayout(new BoxLayout(configSelections, BoxLayout.Y_AXIS));
		configSelections.add(dirTxt);
		configSelections.add(pluginTxt);
		configSelections.add(startStopBtn);

		JPanel controls = new JPanel(new GridBagLayout());

		GridBagConstraints consSel = new GridBagConstraints();
		consSel.fill = GridBagConstraints.BOTH;
		consSel.weightx = 1;
		consSel.gridx = 0;
		controls.add(dirTxt, consSel);
		controls.add(pluginTxt, consSel);
		controls.add(startStopBtn, consSel);
		
		GridBagConstraints consBtn = new GridBagConstraints();
		consBtn.fill = GridBagConstraints.BOTH;
		consBtn.weightx = 0;
		consBtn.gridx = 1;
		controls.add(chooseDirBtn, consBtn);
		controls.add(choosePluginBtn, consBtn);
		controls.add(chooseSetsBtn, consBtn);

		content.add(controls, BorderLayout.NORTH);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(this);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void setDirectory(File octgnDir) {
		if(octgnDir.isDirectory()) {
			loadSets(octgnDir);
		} else {
			log.errorln("Could not find OCTGN data directory (normally <My Documents>/OCTGN), please specify or install OCTGN.");
		}
	}
	
	private void loadSets(File octgnDir) {
		List<Set> sets = loader.loadSets(log, octgnDir);
		if(sets == null) return;
		SetSelector setSelector = loader.getSetSelector();
		synchronized(this) {
			loadedSets = sets;
			int numSets = sets.size();
			setsSelected = new boolean[numSets];
			for(int i=0; i<numSets; i++) {
				Set set = sets.get(i);
				setsSelected[i] = setSelector.isSelect(set);
			}
			dirTxt.setText(octgnDir.getAbsolutePath());
		}
	}
	
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
	
	public JFrame getFrame() {
		return frame;
	}
	
	public OCTGNImageLoader getLoader() {
		return loader;
	}
	
	public LogOutput getLog() {
		return log;
	}
	
	private static File chooseOctgnDataDirectory(Component parent, JFileChooser chooser) {
	    chooser.setDialogTitle("Please specify OCTGN data directory, eg. My Documents/OCTGN");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		if(chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		return chooser.getSelectedFile();
	}
	
	private void chooseOctgnPlugin() {
		final JDialog dialog = new JDialog(frame, true);
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
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	private void selectPlugin(JTable table, OCTGNImageLoaderConfig[] showConfigs) {
		int selectedRow = table.getSelectedRow();
		if(selectedRow > -1) {
			OCTGNImageLoaderConfig config = showConfigs[selectedRow];
			loader.setOctgnPluginConfig(config);
			pluginTxt.setText(config.getPluginName());
			File octgnDir = new File(dirTxt.getText());
			loadSets(octgnDir);
		}
	}
	
	private void chooseSets() {
		final JDialog dialog = new JDialog(frame, true);
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
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	private class Worker extends SwingWorker<Void, Void> implements OCTGNImageLoader.AbortListener {
		
		private final File octgnDir;
		private final List<Set> sets;
		
		public Worker(File octgnDir, List<Set> sets) {
			this.octgnDir = octgnDir;
			this.sets = sets;
		}

		@Override
		protected Void doInBackground() throws Exception {
			loader.downloadOctgnImages(log, octgnDir, sets, this);
			return null;
		}

		@Override
		public boolean isAbort() {
			return isCancelled() || isDone();
		}
		
	}
	
	private synchronized void start(File octgnDir, List<Set> sets) {
		worker = new Worker(octgnDir, sets);
		worker.execute();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if(actionCommand == null) return;
		
		if(actionCommand == ACTION_STARTSTOP) {
			List<Set> sets;
			synchronized(this) {
				if(worker != null && ! worker.isDone()) {
					worker.cancel(false);
					return;
				}
				sets = getSelectedSets();
			}
			if(sets == null) {
				log.errorln("No sets found");
				return;
			}
			String octgnDirStr = dirTxt.getText();
			if(octgnDirStr == null) return;
			File octgnDir = new File(octgnDirStr);
			if(octgnDir.isDirectory()) {
				start(octgnDir, sets);
			}
			
		} else if(actionCommand == ACTION_CHOOSE_PLUGIN) {
			chooseOctgnPlugin();
			
		} else if(actionCommand == ACTION_CHOOSE_OCTGN_DIR) {
			File octgnDir = chooseOctgnDataDirectory(frame, new JFileChooser());
			if(octgnDir != null) {
				setDirectory(octgnDir);
			}
			
		} else if(actionCommand == ACTION_CHOOSE_SETS) {
			chooseSets();
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public synchronized void windowClosing(WindowEvent arg0) {
		if(worker != null) worker.cancel(false);
		frame.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

}

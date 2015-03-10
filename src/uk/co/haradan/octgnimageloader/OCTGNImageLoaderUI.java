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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

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
import javax.swing.table.TableModel;

public class OCTGNImageLoaderUI {
	
	private final JFrame frame;
	private final LogOutput log;
	private final OCTGNImageLoader loader;
	private final JTextField dirTxt;
	private final JTextField pluginTxt;
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
		
		dirTxt = new JTextField();
		
		File myDocsFolder = new JFileChooser().getFileSystemView().getDefaultDirectory();
		File octgnDir = new File(myDocsFolder, "OCTGN");
		if(octgnDir.isDirectory()) {
			dirTxt.setText(octgnDir.getAbsolutePath());
		} else {
			log.errorln("Could not find OCTGN data directory (normally <My Documents>/OCTGN), please specify or install OCTGN.");
		}
		
		JButton chooseDirBtn = new JButton("Choose OCTGN data directory");
		chooseDirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File octgnDir = chooseOctgnDataDirectory(frame, new JFileChooser());
				if(octgnDir != null) {
					dirTxt.setText(octgnDir.getAbsolutePath());
				}
			}
		});
		
		pluginTxt = new JTextField();
		pluginTxt.setEditable(false);
		pluginTxt.setText(loader.getOctgnPluginConfig().getPluginName());
		
		JButton choosePluginBtn = new JButton("Choose OCTGN plugin");
		choosePluginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseOctgnPlugin();
			}
		});

		JPanel configSelections = new JPanel();
		configSelections.setLayout(new BoxLayout(configSelections, BoxLayout.Y_AXIS));
		configSelections.add(dirTxt);
		configSelections.add(pluginTxt);

		JPanel configBtns = new JPanel();
		configBtns.setLayout(new GridBagLayout());
		GridBagConstraints cons = new GridBagConstraints();
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.weightx = 1;
		cons.gridx = 0;
		configBtns.add(chooseDirBtn, cons);
		configBtns.add(choosePluginBtn, cons);

		JPanel configControls = new JPanel(new BorderLayout());
		configControls.add(configSelections, BorderLayout.CENTER);
		configControls.add(configBtns, BorderLayout.EAST);

		JButton startStopBtn = new JButton("Start/Stop");
		startStopBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(worker != null && ! worker.isDone()) {
					worker.cancel(false);
					return;
				}
				String str = dirTxt.getText();
				if(str == null) return;
				File octgnDir = new File(str);
				if(octgnDir.isDirectory()) {
					start(octgnDir);
				}
			}
		});

		JPanel controls = new JPanel(new BorderLayout());
		controls.add(configControls, BorderLayout.CENTER);
		controls.add(startStopBtn, BorderLayout.SOUTH);
		content.add(controls, BorderLayout.NORTH);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(worker != null) worker.cancel(false);
				frame.dispose();
			}
		});
		frame.setSize(600, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void setDirectory(String str) {
		dirTxt.setText(str);
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
					int selectedRow = table.getSelectedRow();
					OCTGNImageLoaderConfig config = showConfigs[selectedRow];
					loader.setOctgnPluginConfig(config);
					pluginTxt.setText(config.getPluginName());
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
				int selectedRow = table.getSelectedRow();
				OCTGNImageLoaderConfig config = showConfigs[selectedRow];
				loader.setOctgnPluginConfig(config);
				pluginTxt.setText(config.getPluginName());
				dialog.dispose();
			}
		});
		dialog.add(okBtn, BorderLayout.SOUTH);
		
		dialog.setSize(400, 200);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}
	
	private class Worker extends SwingWorker<Void, Void> implements OCTGNImageLoader.AbortListener {
		
		private final File octgnDir;
		
		public Worker(File octgnDir) {
			this.octgnDir = octgnDir;
		}

		@Override
		protected Void doInBackground() throws Exception {
			loader.downloadOctgnImages(log, octgnDir, this);
			return null;
		}

		@Override
		public boolean isAbort() {
			return isCancelled() || isDone();
		}
		
	}
	
	private synchronized void start(File octgnDir) {
		worker = new Worker(octgnDir);
		worker.execute();
	}

}

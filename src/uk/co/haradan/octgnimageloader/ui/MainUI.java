package uk.co.haradan.octgnimageloader.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import uk.co.haradan.octgnimageloader.LogOutput;
import uk.co.haradan.octgnimageloader.OCTGNImageLoader;
import uk.co.haradan.octgnimageloader.OCTGNImageLoader.SetLoadListener;
import uk.co.haradan.octgnimageloader.Set;
import uk.co.haradan.octgnimageloader.config.OCTGNImageLoaderConfig;
import uk.co.haradan.octgnimageloader.ui.PluginChooser.PluginChoiceListener;

public class MainUI implements ActionListener, WindowListener, SetLoadListener, PluginChoiceListener {

	private static final String ACTION_CHOOSE_OCTGN_DIR = "chooseOctgnDir";
	private static final String ACTION_CHOOSE_PLUGIN = "choosePlugin";
	private static final String ACTION_CHOOSE_SETS = "chooseSets";
	private static final String ACTION_CHOOSE_ALT_ART = "chooseAltArt";
	private static final String ACTION_STARTSTOP = "startStop";
	
	private final JFrame frame;
	private final LogOutput log;
	private final OCTGNImageLoader loader;
	private final PluginChooser pluginChooser;
	private final SetSelector setSelector;
	private final AltArtChooser altArtChooser;
	private final JTextField dirTxt;
	private final JTextField pluginTxt;
	private Worker worker;
	
	public MainUI() {
		loader = new OCTGNImageLoader();
		
		pluginChooser = new PluginChooser();
		pluginChooser.setListener(this);
		
		setSelector = new SetSelector();
		loader.addSetLoadListener(setSelector);
		loader.addSetLoadListener(this);
		
		altArtChooser = new AltArtChooser();
		
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
		
		JButton chooseAltArtBtn = new JButton("Choose alt art");
		chooseAltArtBtn.setActionCommand(ACTION_CHOOSE_ALT_ART);
		chooseAltArtBtn.addActionListener(this);

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
		
		GridBagConstraints consStart = new GridBagConstraints();
		consStart.fill = GridBagConstraints.BOTH;
		consStart.weightx = 1;
		consStart.gridx = 0;
		consStart.gridheight = 2;
		controls.add(startStopBtn, consStart);
		
		GridBagConstraints consBtn = new GridBagConstraints();
		consBtn.fill = GridBagConstraints.BOTH;
		consBtn.weightx = 0;
		consBtn.gridx = 1;
		controls.add(chooseDirBtn, consBtn);
		controls.add(choosePluginBtn, consBtn);
		controls.add(chooseSetsBtn, consBtn);
		controls.add(chooseAltArtBtn, consBtn);

		content.add(controls, BorderLayout.NORTH);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(this);
		frame.setSize(600, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void setDirectory(File octgnDir) {
		if(octgnDir.isDirectory()) {
			loader.loadSets(log, octgnDir);
		} else {
			log.errorln("Could not find OCTGN data directory (normally <My Documents>/OCTGN), please specify or install OCTGN.");
		}
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
				sets = setSelector.getSelectedSets();
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
			pluginChooser.chooseOctgnPlugin(frame);
			
		} else if(actionCommand == ACTION_CHOOSE_OCTGN_DIR) {
			File octgnDir = chooseOctgnDataDirectory(frame, new JFileChooser());
			if(octgnDir != null) {
				setDirectory(octgnDir);
			}
			
		} else if(actionCommand == ACTION_CHOOSE_SETS) {
			setSelector.chooseSets(frame, log);
			
		} else if(actionCommand == ACTION_CHOOSE_ALT_ART) {
			altArtChooser.chooseAltArt(frame);
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

	@Override
	public void onSetsLoaded(File loadedDir, List<Set> sets) {
		if(sets == null) return;
		synchronized(this) {
			dirTxt.setText(loadedDir.getAbsolutePath());
		}
	}

	@Override
	public void onSetLoadFailed(File attemptedDir, Exception e) {
	}

	@Override
	public void chosenPlugin(OCTGNImageLoaderConfig config) {
		loader.setOctgnPluginConfig(config);
		pluginTxt.setText(config.getPluginName());
		File octgnDir = new File(dirTxt.getText());
		loader.loadSets(log, octgnDir);
	}

}

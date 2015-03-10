package uk.co.haradan.netrunnerimageloader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class NetrunnerImageLoaderUI {
	
	private final JFrame frame;
	private final LogOutput log;
	private final NetrunnerImageLoader loader;
	private final JTextField dirTxt;
	private Worker worker;
	
	public NetrunnerImageLoaderUI(NetrunnerImageLoader useLoader) {
		loader = useLoader;
		
		frame = new JFrame("NetrunnerImageLoader");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(worker != null) worker.cancel(false);
				frame.dispose();
			}
		});
		Container content = frame.getContentPane();
		
		JTextArea logArea = new JTextArea();
		logArea.setEditable(false);
		JScrollPane logScroll = new JScrollPane(logArea);
		content.add(logScroll, BorderLayout.CENTER);
		
		log = new LogOutput(logArea);

		JPanel controls = new JPanel(new BorderLayout());
		
		dirTxt = new JTextField();
		controls.add(dirTxt, BorderLayout.CENTER);
		
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
		controls.add(chooseDirBtn, BorderLayout.EAST);

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
		
		controls.add(startStopBtn, BorderLayout.SOUTH);
		content.add(controls, BorderLayout.NORTH);
		
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
	
	public NetrunnerImageLoader getLoader() {
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
	
	private class Worker extends SwingWorker<Void, Void> implements NetrunnerImageLoader.AbortListener {
		
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

package uk.co.haradan.netrunnerimageloader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class NetrunnerImageLoaderUI {
	
	private final JFrame frame;
	private final LogOutput log;
	private final NetrunnerImageLoader loader;
	
	private class Worker extends SwingWorker<Object, Object> {
		
		private final File octgnDir;
		
		public Worker(File octgnDir) {
			this.octgnDir = octgnDir;
		}

		@Override
		protected Object doInBackground() throws Exception {
			loader.downloadOctgnImages(log, octgnDir);
			return null;
		}
		
	}
	
	public NetrunnerImageLoaderUI(NetrunnerImageLoader useLoader) {
		loader = useLoader;
		
		frame = new JFrame("NetrunnerImageLoader");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		Container content = frame.getContentPane();
		content.add(scrollPane, BorderLayout.CENTER);
		
		log = new LogOutput(textArea);
		
		JButton chooseDirBtn = new JButton("Choose OCTGN data directory & restart");
		chooseDirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File octgnDir = chooseOctgnDataDirectory(frame, new JFileChooser());
				if(octgnDir != null) {
					Worker worker = new Worker(octgnDir);
					worker.execute();
				}
			}
		});
		content.add(chooseDirBtn, BorderLayout.SOUTH);
		
		frame.setSize(600, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void downloadOctgnImagesDefaultDir() {
		
		JFileChooser chooser = new JFileChooser();
		File myDocsFolder = chooser.getFileSystemView().getDefaultDirectory();
		File octgnDir = new File(myDocsFolder, "OCTGN");
		if(! octgnDir.isDirectory()) {
			octgnDir = chooseOctgnDataDirectory(frame, chooser);
			if(octgnDir == null) {
				log.errorln("Could not find OCTGN data directory (normally <My Documents>/OCTGN), please specify or install OCTGN.");
				return;
			}
		}
		
		Worker worker = new Worker(octgnDir);
		worker.execute();
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

}

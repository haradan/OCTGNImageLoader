package uk.co.haradan.octgnimageloader;

import java.io.File;

import uk.co.haradan.octgnimageloader.ui.MainUI;



public class Main {

	public static void main(String[] args) {
		
		MainUI view = new MainUI();
		
		if(args.length > 0) {
			view.setDirectory(new File(args[0]));
		}
	}

}

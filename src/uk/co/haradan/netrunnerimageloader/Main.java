package uk.co.haradan.netrunnerimageloader;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		
		NetrunnerImageLoader model = new NetrunnerImageLoader();
		NetrunnerImageLoaderUI view = new NetrunnerImageLoaderUI(model);
		
		if(args.length > 0) {
			File octgnDir = new File(args[0]);
			model.downloadOctgnImages(view.getLog(), octgnDir);
		} else {
			view.downloadOctgnImagesDefaultDir();
		}
	}

}

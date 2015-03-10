package uk.co.haradan.netrunnerimageloader;



public class Main {

	public static void main(String[] args) {
		
		NetrunnerImageLoader model = new NetrunnerImageLoader();
		NetrunnerImageLoaderUI view = new NetrunnerImageLoaderUI(model);
		
		if(args.length > 0) {
			view.setDirectory(args[0]);
		}
	}

}

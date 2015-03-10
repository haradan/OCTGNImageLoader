package uk.co.haradan.octgnimageloader;



public class Main {

	public static void main(String[] args) {
		
		OCTGNImageLoader model = new OCTGNImageLoader();
		OCTGNImageLoaderUI view = new OCTGNImageLoaderUI(model);
		
		if(args.length > 0) {
			view.setDirectory(args[0]);
		}
	}

}

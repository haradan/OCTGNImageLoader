package uk.co.haradan.octgnimageloader.altart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AltArtSourceFile extends AltArtAbstractSource {
	
	private File file;
	
	public AltArtSourceFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}

	@Override
	protected InputStream openInputStream() throws FileNotFoundException {
		return new FileInputStream(file);
	}
	
}

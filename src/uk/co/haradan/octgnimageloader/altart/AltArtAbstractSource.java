package uk.co.haradan.octgnimageloader.altart;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import uk.co.haradan.octgnimageloader.SetCard;

public abstract class AltArtAbstractSource implements AltArtSource {

	@Override
	public BufferedImage loadImage() throws Exception {
		InputStream is = openInputStream();
		try {
			return ImageIO.read(is);
		} finally {
			is.close();
		}
	}

	@Override
	public String getFoundCardName() {
		return null;
	}
	
	@Override
	public boolean isMatchCardsSupported() {
		return false;
	}
	
	@Override
	public boolean isCardMatch(SetCard card) {
		return false;
	}
	
	protected abstract InputStream openInputStream() throws Exception;

}

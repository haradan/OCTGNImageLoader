package uk.co.haradan.octgnimageloader.altart;

import java.awt.image.BufferedImage;

import uk.co.haradan.octgnimageloader.SetCard;


public interface AltArtSource {
	
	public String getFoundCardName();
	public boolean isMatchCardsSupported();
	public boolean isCardMatch(SetCard card);
	public BufferedImage loadImage() throws Exception;
	
}

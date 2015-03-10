package uk.co.haradan.netrunnerimageloader.cardkeys;

import org.xml.sax.Attributes;

public interface SaxCardKeyBuilder {
	
	public void startCard(Attributes attrs);
	public void startProperty(Attributes attrs);
	public void endCard();
	public String getKey();
	
}

package uk.co.haradan.octgnimageloader.cardkeys;

import org.xml.sax.Attributes;

public interface OctgnCardKeyBuilder <KeyType extends CardKey> {

	public void startSet(Attributes attrs);
	public void startCard(Attributes attrs);
	public void startProperty(Attributes attrs);
	public void endCard();
	public void endSet();
	public KeyType buildKey();
	
}

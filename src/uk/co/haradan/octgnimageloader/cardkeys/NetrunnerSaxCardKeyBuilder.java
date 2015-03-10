package uk.co.haradan.octgnimageloader.cardkeys;

import org.xml.sax.Attributes;

public class NetrunnerSaxCardKeyBuilder implements SaxCardKeyBuilder {
	
	private String key;

	@Override
	public void startCard(Attributes attrs) {
		String id = attrs.getValue("id");
		if(id == null) key = null;
		key = id.substring(id.length() - 5);
	}

	@Override
	public void startProperty(Attributes attrs) {}

	@Override
	public void endCard() {}

	@Override
	public String getKey() {
		return key;
	}

}

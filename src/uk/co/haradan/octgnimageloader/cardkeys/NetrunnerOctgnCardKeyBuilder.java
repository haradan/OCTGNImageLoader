package uk.co.haradan.octgnimageloader.cardkeys;

import org.xml.sax.Attributes;

public class NetrunnerOctgnCardKeyBuilder implements OctgnCardKeyBuilder<NetrunnerCardKey> {
	
	private NetrunnerCardKey key;

	@Override
	public void startCard(Attributes attrs) {
		String id = attrs.getValue("id");
		if(id == null) key = null;
		int idLen = id.length();
		String setStr = id.substring(idLen-5, idLen-3);
		int setNum;
		try {
			setNum = Integer.parseInt(setStr);
		} catch(NumberFormatException e) {
			setNum = 0;
		}
		String cardStr = id.substring(idLen-3);
		int cardNum;
		try {
			cardNum = Integer.parseInt(cardStr);
		} catch(NumberFormatException e) {
			cardNum = 0;
		}
		key = new NetrunnerCardKey(setNum, cardNum);
	}

	@Override
	public void startProperty(Attributes attrs) {}

	@Override
	public void endCard() {}

	@Override
	public NetrunnerCardKey buildKey() {
		return key;
	}

	@Override
	public void startSet(Attributes attrs) {}
	@Override
	public void endSet() {}

}

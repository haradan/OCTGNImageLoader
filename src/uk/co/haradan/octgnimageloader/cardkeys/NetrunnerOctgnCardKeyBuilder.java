package uk.co.haradan.octgnimageloader.cardkeys;

import org.xml.sax.Attributes;

public class NetrunnerOctgnCardKeyBuilder implements OctgnCardKeyBuilder<NetrunnerCardKey> {
	
	private int cycleNum = -1;
	private int cardNum = -1;
	private String cardName;
	private String setName;
	private String type;

	@Override
	public void startCard(Attributes attrs) {
		
		cardName = attrs.getValue("name");
		
		String id = attrs.getValue("id");
		if(id == null) return;
		int idLen = id.length();
		
		String cycleStr = id.substring(idLen-5, idLen-3);
		try {
			cycleNum = Integer.parseInt(cycleStr);
		} catch(NumberFormatException e) {}
		
		String cardStr = id.substring(idLen-3);
		try {
			cardNum = Integer.parseInt(cardStr);
		} catch(NumberFormatException e) {}
	}

	@Override
	public void startProperty(Attributes attrs) {
		String name = attrs.getValue("name");
		if("Type".equals(name)) {
			type = attrs.getValue("value");
		}
	}

	@Override
	public NetrunnerCardKey buildKey() {
		return new NetrunnerCardKey(cycleNum, cardNum, cardName, setName, type);
	}

	@Override
	public void endCard() {
		cycleNum = -1;
		cardNum = -1;
		cardName = null;
		type = null;
	}

	@Override
	public void startSet(Attributes attrs) {
		setName = attrs.getValue("name");
	}
	@Override
	public void endSet() {
		setName = null;
	}

}

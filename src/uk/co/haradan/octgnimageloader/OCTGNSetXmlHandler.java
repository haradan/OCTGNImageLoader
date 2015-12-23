package uk.co.haradan.octgnimageloader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.haradan.octgnimageloader.cardkeys.OctgnCardKeyBuilder;

public class OCTGNSetXmlHandler extends DefaultHandler {
	
	private final OctgnCardKeyBuilder<?> cardKeyBuilder;
	private Set set = new Set();
	private SetCard card;
	
	public OCTGNSetXmlHandler(String setId, OctgnCardKeyBuilder<?> cardKeyBuilder) {
		set.setId(setId);
		this.cardKeyBuilder = cardKeyBuilder;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		if("set".equals(qName)) {
			String name = attributes.getValue("name");
			if(name == null) return;
			set.setName(name);
			cardKeyBuilder.startSet(attributes);
			
		} else if("card".equals(qName)) {
			
			String id = attributes.getValue("id");
			if(id == null) return;
			String name = attributes.getValue("name");
			
			card = new SetCard();
			card.setId(id);
			card.setName(name);
			set.addCard(card);
			
			cardKeyBuilder.startCard(attributes);
		} else if("property".equals(qName)) {
			cardKeyBuilder.startProperty(attributes);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if("card".equals(qName)) {
			card.setCardKey(cardKeyBuilder.buildKey());
			cardKeyBuilder.endCard();
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		cardKeyBuilder.endSet();
	}
	
	public Set getSet() {
		return set;
	}

}

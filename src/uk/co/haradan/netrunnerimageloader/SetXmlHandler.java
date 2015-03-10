package uk.co.haradan.netrunnerimageloader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.co.haradan.netrunnerimageloader.cardkeys.SaxCardKeyBuilder;

public class SetXmlHandler extends DefaultHandler {
	
	private final SaxCardKeyBuilder[] cardKeyBuilders;
	private Set set = new Set();
	private SetCard card;
	
	public SetXmlHandler(String setId, SaxCardKeyBuilder[] cardKeyBuilders) {
		set.setId(setId);
		this.cardKeyBuilders = cardKeyBuilders;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		if("set".equals(qName)) {
			String name = attributes.getValue("name");
			if(name == null) return;
			set.setName(name);
			
		} else if("card".equals(qName)) {
			
			String id = attributes.getValue("id");
			if(id == null) return;
			String name = attributes.getValue("name");
			
			card = new SetCard();
			card.setId(id);
			card.setName(name);
			set.addCard(card);
			
			for(SaxCardKeyBuilder builder : cardKeyBuilders) {
				builder.startCard(attributes);
			}
		} else if("property".equals(qName)) {
			for(SaxCardKeyBuilder builder : cardKeyBuilders) {
				builder.startProperty(attributes);
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if("card".equals(qName)) {
			for(SaxCardKeyBuilder builder : cardKeyBuilders) {
				builder.endCard();
				card.getCardKey().addKeyPart(builder.getKey());
			}
		}
	}
	
	public Set getSet() {
		return set;
	}

}

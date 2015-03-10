package uk.co.haradan.netrunnerimageloader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SetXmlHandler extends DefaultHandler {
	
	private Set set = new Set();
	
	public SetXmlHandler(String setId) {
		set.setId(setId);
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
			
			SetCard card = new SetCard();
			card.setId(id);
			card.setName(name);
			set.addCard(card);
		}
	}
	
	public Set getSet() {
		return set;
	}

}

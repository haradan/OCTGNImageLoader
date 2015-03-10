package uk.co.haradan.octgnimageloader.cardkeys;

import org.xml.sax.Attributes;

public class SaxCardPropertyBuilder implements SaxCardKeyBuilder {
	
	private final String propertyName;
	private final StringProcessor stringProcessor;
	private String propertyValue;
	
	public SaxCardPropertyBuilder(String attributeName) {
		this.propertyName = attributeName;
		stringProcessor = null;
	}

	public SaxCardPropertyBuilder(String propertyName, StringProcessor stringProcessor) {
		this.propertyName = propertyName;
		this.stringProcessor = stringProcessor;
	}

	@Override
	public void startCard(Attributes attrs) {
		propertyValue = null;
	}

	@Override
	public void startProperty(Attributes attrs) {
		String name = attrs.getValue("name");
		if(propertyName.equals(name)) {
			propertyValue = attrs.getValue("value");
			
			if(stringProcessor != null) {
				propertyValue = stringProcessor.process(propertyValue);
			}
		}
	}

	@Override
	public void endCard() {}

	@Override
	public String getKey() {
		return propertyValue;
	}

}

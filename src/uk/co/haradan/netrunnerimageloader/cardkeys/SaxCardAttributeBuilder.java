package uk.co.haradan.netrunnerimageloader.cardkeys;

import org.xml.sax.Attributes;

public class SaxCardAttributeBuilder implements SaxCardKeyBuilder {
	
	private final String attributeName;
	private final StringProcessor stringProcessor;
	private String attributeValue;
	
	public SaxCardAttributeBuilder(String attributeName) {
		this.attributeName = attributeName;
		stringProcessor = null;
	}
	
	public SaxCardAttributeBuilder(String attributeName, StringProcessor stringProcessor) {
		this.attributeName = attributeName;
		this.stringProcessor = stringProcessor;
	}

	@Override
	public void startCard(Attributes attrs) {
		attributeValue = attrs.getValue(attributeName);
		if(stringProcessor != null) {
			attributeValue = stringProcessor.process(attributeValue);
		}
	}

	@Override
	public void startProperty(Attributes attrs) {}

	@Override
	public void endCard() {}

	@Override
	public String getKey() {
		return attributeValue;
	}

}

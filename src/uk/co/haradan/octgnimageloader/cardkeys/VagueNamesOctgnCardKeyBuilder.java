package uk.co.haradan.octgnimageloader.cardkeys;

import org.xml.sax.Attributes;

public class VagueNamesOctgnCardKeyBuilder implements OctgnCardKeyBuilder<VagueNamesCardKey> {

	private final String typeProperty;
	
	private String setName;
	private String cardName;
	private String typeName;
	
	public VagueNamesOctgnCardKeyBuilder() {
		typeProperty = null;
	}
	
	public VagueNamesOctgnCardKeyBuilder(String typeProperty) {
		this.typeProperty = typeProperty;
	}

	@Override
	public void startSet(Attributes attrs) {
		setName = attrs.getValue("name");
	}

	@Override
	public void startCard(Attributes attrs) {
		cardName = attrs.getValue("name");
	}

	@Override
	public void startProperty(Attributes attrs) {
		if(typeProperty != null) {
			String name = attrs.getValue("name");
			if(typeProperty.equals(name)) {
				typeName = attrs.getValue("value");
			}
		}
	}

	@Override
	public void endCard() {
		cardName = null;
		typeName = null;
	}
	
	@Override
	public void endSet() {
		setName = null;
	}

	@Override
	public VagueNamesCardKey buildKey() {
		return new VagueNamesCardKey(cardName, setName, typeName);
	}

}

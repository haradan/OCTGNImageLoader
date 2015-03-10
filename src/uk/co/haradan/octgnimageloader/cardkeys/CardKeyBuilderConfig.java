package uk.co.haradan.octgnimageloader.cardkeys;


public class CardKeyBuilderConfig {
	
	private final SaxCardKeyBuilder[] saxKeyBuilders;
	private final JsonCardKeyBuilder[] jsonKeyBuilders;
	
	public CardKeyBuilderConfig(SaxCardKeyBuilder[] saxKeyBuilders, JsonCardKeyBuilder[] jsonKeyBuilders) {
		this.saxKeyBuilders = saxKeyBuilders;
		this.jsonKeyBuilders = jsonKeyBuilders;
	}
	
	public SaxCardKeyBuilder[] getSaxKeyBuilders() {
		return saxKeyBuilders;
	}
	public JsonCardKeyBuilder[] getJsonKeyBuilders() {
		return jsonKeyBuilders;
	}
	
	public final static CardKeyBuilderConfig NETRUNNER_CONFIG = new CardKeyBuilderConfig(
			new SaxCardKeyBuilder[] { new NetrunnerSaxCardKeyBuilder() },
			new JsonCardKeyBuilder[] { new NetrunnerJsonCardKeyBuilder() });
	
	public final static CardKeyBuilderConfig DOOMTOWN_CONFIG = new CardKeyBuilderConfig(
			new SaxCardKeyBuilder[] { new SaxCardAttributeBuilder("name", StringToVariableCase.PROCESSOR), new SaxCardPropertyBuilder("Type") },
			new JsonCardKeyBuilder[] { new JsonCardFieldBuilder("title", StringToVariableCase.PROCESSOR), new JsonCardFieldBuilder("type") });

}

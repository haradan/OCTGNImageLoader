package uk.co.haradan.octgnimageloader.cardkeys;


public class CardKeyBuilderConfig<KeyType extends CardKey> {
	
	private final OctgnCardKeyBuilder<KeyType> octgnKeyBuilder;
	private final JsonCardKeyBuilder<KeyType> websiteKeyBuilder;
	
	public CardKeyBuilderConfig(OctgnCardKeyBuilder<KeyType> octgnKeyBuilder, JsonCardKeyBuilder<KeyType> websiteKeyBuilder) {
		this.octgnKeyBuilder = octgnKeyBuilder;
		this.websiteKeyBuilder = websiteKeyBuilder;
	}
	
	public OctgnCardKeyBuilder<KeyType> getOctgnKeyBuilder() {
		return octgnKeyBuilder;
	}
	public JsonCardKeyBuilder<KeyType> getWebsiteKeyBuilder() {
		return websiteKeyBuilder;
	}
	
	public final static CardKeyBuilderConfig<NetrunnerCardKey> NETRUNNER_CONFIG = new CardKeyBuilderConfig<NetrunnerCardKey>(
			new NetrunnerOctgnCardKeyBuilder(),
			new NetrunnerJsonCardKeyBuilder());
	
	public final static CardKeyBuilderConfig<VagueNamesCardKey> DOOMTOWN_CONFIG;
	
	static {
		VagueNamesJsonCardKeyBuilderDefault webKeyBuilder = new VagueNamesJsonCardKeyBuilderDefault("title", "pack", "type");
		webKeyBuilder.addSetNameSubstitution("Base Set", "Core Set");
		DOOMTOWN_CONFIG = new CardKeyBuilderConfig<VagueNamesCardKey>(
				new VagueNamesOctgnCardKeyBuilder("Type"),
				webKeyBuilder);
	}

}

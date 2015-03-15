package uk.co.haradan.octgnimageloader.config;

import uk.co.haradan.octgnimageloader.cardkeys.CardKeyBuilderConfig;

public class OCTGNImageLoaderConfig {
	
	public static final String NETRUNNERDB_CARDS_URL = "http://netrunnerdb.com/api/cards/";
	public static final String NETRUNNERDB_IMAGE_BASE_URL = "http://netrunnerdb.com";
	public static final String NETRUNNER_PLUGIN_ID = "0f38e453-26df-4c04-9d67-6d43de939c77";
	
	public static final String DOOMTOWN_CARDS_URL = "http://dtdb.co/api/cards/";
	public static final String DOOMTOWN_IMAGE_BASE_URL = "http://dtdb.co";
	public static final String DOOMTOWN_PLUGIN_ID = "b440d120-025a-4fbe-9f8d-3873acacb37b";

	private final String pluginId;
	private final String pluginName;
	private final String cardsUrl;
	private final String imageBaseUrl;
	private final CardKeyBuilderConfig cardKeyBuilderConfig;
	private final SetSorter setSorter;
	
	public static final OCTGNImageLoaderConfig NETRUNNER_CONFIG;
	public static final OCTGNImageLoaderConfig DOOMTOWN_CONFIG;
	public static final OCTGNImageLoaderConfig[] BUILTIN_CONFIGS;
	
	static {
		NETRUNNER_CONFIG = new OCTGNImageLoaderConfig(NETRUNNER_PLUGIN_ID, 
				"Android-Netrunner",
				NETRUNNERDB_CARDS_URL,
				NETRUNNERDB_IMAGE_BASE_URL,
				CardKeyBuilderConfig.NETRUNNER_CONFIG,
				new SetSorterNetrunner());
		
		DOOMTOWN_CONFIG = new OCTGNImageLoaderConfig(DOOMTOWN_PLUGIN_ID,
				"Doomtown-Reloaded",
				DOOMTOWN_CARDS_URL,
				DOOMTOWN_IMAGE_BASE_URL,
				CardKeyBuilderConfig.DOOMTOWN_CONFIG,
				new SetSorterByName());
		
		BUILTIN_CONFIGS = new OCTGNImageLoaderConfig[] { NETRUNNER_CONFIG, DOOMTOWN_CONFIG };
	}
	
	public OCTGNImageLoaderConfig(String pluginId, String pluginName,
			String cardsUrl, String imageBaseUrl,
			CardKeyBuilderConfig cardKeyBuilderConfig,
			SetSorter setSorter) {
		this.pluginId = pluginId;
		this.pluginName = pluginName;
		this.cardsUrl = cardsUrl;
		this.imageBaseUrl = imageBaseUrl;
		this.cardKeyBuilderConfig = cardKeyBuilderConfig;
		this.setSorter = setSorter;
	}

	public String getPluginId() {
		return pluginId;
	}
	public String getPluginName() {
		return pluginName;
	}
	public CardKeyBuilderConfig getCardKeyBuilderConfig() {
		return cardKeyBuilderConfig;
	}
	public String getCardsUrl() {
		return cardsUrl;
	}
	public String getImageBaseUrl() {
		return imageBaseUrl;
	}
	public SetSorter getSetSorter() {
		return setSorter;
	}

}

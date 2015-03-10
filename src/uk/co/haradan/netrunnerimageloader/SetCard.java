package uk.co.haradan.netrunnerimageloader;

import uk.co.haradan.netrunnerimageloader.cardkeys.CardKey;

public class SetCard {
	
	private String id;
	private String name;
	private CardKey cardKey = new CardKey();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CardKey getCardKey() {
		return cardKey;
	}

}

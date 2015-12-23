package uk.co.haradan.octgnimageloader;

import uk.co.haradan.octgnimageloader.cardkeys.CardKey;

public class SetCard {
	
	private String id;
	private String name;
	private CardKey cardKey;
	
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
	public void setCardKey(CardKey cardKey) {
		this.cardKey = cardKey;
	}
	public CardKey getCardKey() {
		return cardKey;
	}

}

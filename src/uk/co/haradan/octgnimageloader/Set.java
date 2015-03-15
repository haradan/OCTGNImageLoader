package uk.co.haradan.octgnimageloader;

import java.util.ArrayList;
import java.util.List;

public class Set {
	
	private String id;
	private int number = -1;
	private String name;
	private final List<SetCard> cards = new ArrayList<SetCard>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SetCard> getCards() {
		return cards;
	}
	public void addCard(SetCard card) {
		cards.add(card);
	}

}

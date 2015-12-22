package uk.co.haradan.octgnimageloader.altart;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import uk.co.haradan.octgnimageloader.Set;
import uk.co.haradan.octgnimageloader.SetCard;

public class AltArtChoice {
	
	private final AltArtSource source;
	private final BufferedImage image;
	private final List<SetCard> matchingCards;
	private String chosenCardOctgnId;
	
	public AltArtChoice(AltArtSource source, BufferedImage image, List<SetCard> matchingCards, String chosenCardOctgnId) {
		this.source = source;
		this.image = image;
		this.matchingCards = matchingCards;
		this.chosenCardOctgnId = chosenCardOctgnId;
	}
	
	public AltArtSource getSource() {
		return source;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public List<SetCard> getMatchingCards() {
		return matchingCards;
	}
	
	public void setChosenCardOctgnId(String chosenCardOctgnId) {
		this.chosenCardOctgnId = chosenCardOctgnId;
	}
	
	public String getChosenCardOctgnId() {
		return chosenCardOctgnId;
	}
	
	public static AltArtChoice load(AltArtSource source, List<Set> sets) throws Exception {
		BufferedImage img = source.loadImage();
		List<SetCard> matchingCards = null;
		String chosenCardOctgnId = null;
		if(source.isMatchCardsSupported()) {
			List<SetCard> matched = new ArrayList<SetCard>();
			for(Set set : sets) {
				for(SetCard card : set.getCards()) {
					if(source.isCardMatch(card)) {
						matched.add(card);
					}
				}
			}
			if(matched.size() > 0) matchingCards = matched;
			if(matched.size() == 1) {
				SetCard card = matched.get(0);
				chosenCardOctgnId = card.getId();
			}
		}
		return new AltArtChoice(source, img, matchingCards, chosenCardOctgnId);
	}
	
}

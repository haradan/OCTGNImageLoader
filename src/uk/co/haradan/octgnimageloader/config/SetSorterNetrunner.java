package uk.co.haradan.octgnimageloader.config;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.haradan.octgnimageloader.Set;
import uk.co.haradan.octgnimageloader.SetCard;

public class SetSorterNetrunner implements SetSorter {
	
	private Comparator<Set> comparator = new Comparator<Set>() {
		@Override
		public int compare(Set set0, Set set1) {
			int num0 = set0.getNumber();
			int num1 = set1.getNumber();
			return num0 - num1;
		}
	};

	@Override
	public void sort(List<Set> sets) {
		for(Set set : sets) {
			List<SetCard> cards = set.getCards();
			if(cards.size() < 1) return;
			SetCard card = cards.get(0);
			String cardId = card.getId();
			int idLen = cardId.length();
			
			String cardNumStr = cardId.substring(idLen-3);
			while(cardNumStr.startsWith("0") && cardNumStr.length() > 1) {
				cardNumStr = cardNumStr.substring(1);
			}
			try {
				Integer.parseInt(cardNumStr);
			} catch(NumberFormatException e) {
				continue;
			}
			
			String setNumStr = cardId.substring(idLen-5, idLen-3);
			while(setNumStr.startsWith("0") && setNumStr.length() > 1) {
				setNumStr = setNumStr.substring(1);
			}
			try {
				int setNum = Integer.parseInt(setNumStr);
				set.setNumber(setNum);
			} catch(NumberFormatException e) {
				continue;
			}
		}
		
		Collections.sort(sets, comparator);
	}

}

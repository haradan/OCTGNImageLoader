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
			
			String cycleCardNumStr = cardId.substring(idLen-5);
			while(cycleCardNumStr.startsWith("0") && cycleCardNumStr.length() > 1) {
				cycleCardNumStr = cycleCardNumStr.substring(1);
			}
			int cycleCardNum;
			try {
				cycleCardNum = Integer.parseInt(cycleCardNumStr);
				set.setNumber(cycleCardNum);
			} catch(NumberFormatException e) {
				continue;
			}
		}
		
		Collections.sort(sets, comparator);
	}

}

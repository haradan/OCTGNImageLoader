package uk.co.haradan.octgnimageloader.config;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.co.haradan.octgnimageloader.Set;

public class SetSorterByName implements SetSorter {
	
	private Comparator<Set> comparator = new Comparator<Set>() {
		@Override
		public int compare(Set set0, Set set1) {
			String name0 = set0.getName();
			String name1 = set1.getName();
			if(name0 == name1) return 0;
			if(name0 == null) return 1;
			if(name1 == null) return -1;
			return name0.compareToIgnoreCase(name1);
		}
	};

	@Override
	public void sort(List<Set> sets) {
		Collections.sort(sets, comparator);
	}

}

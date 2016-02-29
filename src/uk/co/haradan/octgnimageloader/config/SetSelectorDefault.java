package uk.co.haradan.octgnimageloader.config;

import uk.co.haradan.octgnimageloader.Set;

public class SetSelectorDefault implements SetSelector {

	@Override
	public boolean isSelect(Set set) {
		return true;
	}

}

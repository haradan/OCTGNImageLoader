package uk.co.haradan.octgnimageloader.config;

import uk.co.haradan.octgnimageloader.Set;

public class SetSelectorNetrunner implements SetSelector {

	private static final String MARKERS_SET_ID = "21bf9e05-fb23-4b1d-b89a-398f671f5999";
	private static final String PROMOS_SET_ID = "fb4b7b07-6bf5-4a52-abd1-1e59280fc85e";
	
	@Override
	public boolean isSelect(Set set) {
		String id = set.getId();
		if(id == null || id.equals(MARKERS_SET_ID) || id.equals(PROMOS_SET_ID)) {
			return false;
		} else {
			return true;
		}
	}

}

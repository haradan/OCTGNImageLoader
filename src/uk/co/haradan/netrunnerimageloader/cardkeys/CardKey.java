package uk.co.haradan.netrunnerimageloader.cardkeys;

import java.util.ArrayList;
import java.util.List;

public class CardKey {
	
	private final List<String> keyParts = new ArrayList<String>();
	
	public List<String> getKeyParts() {
		return keyParts;
	}
	public void addKeyPart(String key) {
		keyParts.add(key);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof CardKey)) return false;
		CardKey comp = (CardKey) obj;
		
		int numKeys = keyParts.size();
		if(comp.keyParts.size() != numKeys) return false;
		
		for(int i=0; i<numKeys; i++) {
			if(! keyParts.get(i).equals(comp.keyParts.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		for(String key : keyParts) {
			hashCode += key.hashCode();
		}
		return hashCode;
	}

}

package uk.co.haradan.octgnimageloader.cardkeys;

public class VagueNamesCardKey extends CardKey {
	
	private final String setName;
	private final String cardName;
	private final String typeName;
	
	public VagueNamesCardKey(String cardName, String setName) {
		this(cardName, setName, null);
	}
	
	public VagueNamesCardKey(String cardName, String setName, String typeName) {
		this.setName = StringToClassCase.process(setName);
		this.cardName = StringToClassCase.process(cardName);
		this.typeName = StringToClassCase.process(typeName);
	}
	
	public String getSetName() {
		return setName;
	}
	public String getCardName() {
		return cardName;
	}

	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof VagueNamesCardKey)) return false;
		VagueNamesCardKey key = (VagueNamesCardKey) obj;
		
		if(cardName == null) return key.cardName == null;
		if(key.cardName == null) return false;
		
		if(setName != null) {
			if(key.setName != null) {
				if(! setName.equals(key.setName)) {
					return false;
				}
			}
		}
		
		if(typeName != null) { 
			if(key.typeName != null) {
				if(! typeName.equals(key.typeName)) return false;
			}
		}
		
		return cardName.contains(key.cardName) || key.cardName.contains(cardName);
	}

	@Override
	public int hashCode() {
		if(setName != null) return setName.hashCode();
		if(typeName != null) return typeName.hashCode();
		return 0;
	}
	
	@Override
	public String toString() {
		return cardName;
	}

}

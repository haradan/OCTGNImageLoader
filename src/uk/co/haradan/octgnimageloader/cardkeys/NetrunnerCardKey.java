package uk.co.haradan.octgnimageloader.cardkeys;

public class NetrunnerCardKey extends VagueNamesCardKey {
	
	private int cycleNum;
	private int cardNum;
	
	public NetrunnerCardKey(int cycleNum, int cardNum, String cardName, String setName) {
		this(cycleNum, cardNum, cardName, setName, null);
	}
	
	public NetrunnerCardKey(int cycleNum, int cardNum, String cardName, String setName, String typeName) {
		super(cardName, setName, typeName);
		this.cycleNum = cycleNum;
		this.cardNum = cardNum;
	}
	
	public int getCycleNum() {
		return cycleNum;
	}
	
	public int getCardNum() {
		return cardNum;
	}

	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof NetrunnerCardKey)) return false;
		NetrunnerCardKey key = (NetrunnerCardKey) obj;
		if(cycleNum != key.cycleNum) return false;
		if(cycleNum <= 0) return super.equals(obj);
		return key.cardNum == cardNum;
	}

	@Override
	public int hashCode() {
		return cycleNum*31 + cardNum;
	}
	
	@Override
	public String toString() {
		return cycleNum+"."+cardNum;
	}

}

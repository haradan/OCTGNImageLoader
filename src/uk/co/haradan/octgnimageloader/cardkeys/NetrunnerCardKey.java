package uk.co.haradan.octgnimageloader.cardkeys;

public class NetrunnerCardKey extends CardKey {
	
	private int cycleNum;
	private int cardNum;
	
	public NetrunnerCardKey(int cycleNum, int cardNum) {
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
		return key.cycleNum == cycleNum && key.cardNum == cardNum;
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

package uk.co.haradan.netrunnerimageloader.cardkeys;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class NetrunnerJsonCardKeyBuilder implements JsonCardKeyBuilder {
	
	private int cycleNum;
	private int cardNum;

	@Override
	public void startCard() {
		cycleNum = -1;
		cardNum = -1;
	}

	@Override
	public void readField(String fieldName, JsonToken valueToken, JsonParser parser) throws JsonParseException, IOException {
		if(valueToken != JsonToken.VALUE_NUMBER_INT) return;
		
		if("number".equals(fieldName)) {
			cardNum = parser.getIntValue();
		} else if("cyclenumber".equals(fieldName)) {
			cycleNum = parser.getIntValue();
		}
	}

	@Override
	public void endCard() {}

	@Override
	public String getKey() {
		if(cycleNum < 0 || cardNum < 0) return null;
		
		// OCTGN Netrunner card ids always end with their cycle number and card number in this format, so we can identify them this way.
		return String.format("%1$02d%2$03d", cycleNum, cardNum);
	}
	
}

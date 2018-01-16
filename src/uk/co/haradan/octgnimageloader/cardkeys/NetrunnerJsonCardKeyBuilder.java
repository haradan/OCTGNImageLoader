package uk.co.haradan.octgnimageloader.cardkeys;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class NetrunnerJsonCardKeyBuilder extends VagueNamesJsonCardKeyBuilder<NetrunnerCardKey> {
	
	private int cycleNum;
	private int cardNum;
	
	public NetrunnerJsonCardKeyBuilder() {
		super("title", "setname", "type");
	}

	@Override
	public void startCard() {
		cycleNum = -1;
		cardNum = -1;
	}

	@Override
	public void readField(String fieldName, JsonToken valueToken, JsonParser parser) throws JsonParseException, IOException {
		
		if("number".equals(fieldName)) {
			if(valueToken != JsonToken.VALUE_NUMBER_INT) return;
			cardNum = parser.getIntValue();
		} else if("cyclenumber".equals(fieldName)) {
			if(valueToken != JsonToken.VALUE_NUMBER_INT) return;
			cycleNum = parser.getIntValue();
		} else if("code".equals(fieldName)) {
			String code = parser.getValueAsString();
			if(code.length() == 5) {
				String cycleNumStr = code.substring(0, 2);
				try {
					cycleNum = Integer.parseInt(cycleNumStr);
				} catch(NumberFormatException e) {}
				String cardNumStr = code.substring(2, 5);
				try {
					cardNum = Integer.parseInt(cardNumStr);
				} catch(NumberFormatException e) {}
			}
		}
	}

	@Override
	public void endCard() {}

	@Override
	public NetrunnerCardKey buildKey() {
		return new NetrunnerCardKey(cycleNum, cardNum, cardName, setName, typeName);
	}
	
}

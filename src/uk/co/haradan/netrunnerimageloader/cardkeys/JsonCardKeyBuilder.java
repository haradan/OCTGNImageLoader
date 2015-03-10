package uk.co.haradan.netrunnerimageloader.cardkeys;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public interface JsonCardKeyBuilder {
	
	public void startCard();
	public void readField(String fieldName, JsonToken valueToken, JsonParser parser) throws JsonParseException, IOException;
	public void endCard();
	public String getKey();
	
}

package uk.co.haradan.octgnimageloader.cardkeys;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public interface JsonCardKeyBuilder <KeyType extends CardKey> {
	
	public void startCard();
	public void readField(String fieldName, JsonToken valueToken, JsonParser parser) throws JsonParseException, IOException;
	public void endCard();
	public KeyType buildKey();
	
}

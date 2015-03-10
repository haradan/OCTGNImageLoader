package uk.co.haradan.netrunnerimageloader.cardkeys;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JsonCardFieldBuilder implements JsonCardKeyBuilder {
	
	private final String fieldName;
	private final StringProcessor stringProcessor;
	private String fieldValue;
	
	public JsonCardFieldBuilder(String fieldName) {
		this.fieldName = fieldName;
		stringProcessor = null;
	}
	
	public JsonCardFieldBuilder(String fieldName, StringProcessor stringProcessor) {
		this.fieldName = fieldName;
		this.stringProcessor = stringProcessor;
	}

	@Override
	public void startCard() {
		fieldValue = null;
	}

	@Override
	public void readField(String fieldName, JsonToken valueToken, JsonParser parser) throws JsonParseException, IOException {
		if(this.fieldName.equals(fieldName)) {
			fieldValue = parser.getValueAsString();
			
			if(stringProcessor != null) {
				fieldValue = stringProcessor.process(fieldValue);
			}
		}
	}

	@Override
	public void endCard() {}

	@Override
	public String getKey() {
		return fieldValue;
	}
	
}

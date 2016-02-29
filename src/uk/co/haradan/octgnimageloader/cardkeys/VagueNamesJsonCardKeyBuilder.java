package uk.co.haradan.octgnimageloader.cardkeys;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public abstract class VagueNamesJsonCardKeyBuilder<K extends VagueNamesCardKey> implements JsonCardKeyBuilder<K> {
	
	private final String setNameField;
	private final String cardNameField;
	private final String typeNameField;
	protected String setName;
	protected String cardName;
	protected String typeName;
	private Map<String, String> setNameSubstitutions = new HashMap<String, String>();
	private Map<String, String> cardNameSubstitutions = new HashMap<String, String>();
	private Map<String, String> typeNameSubstitutions = new HashMap<String, String>();
	
	public VagueNamesJsonCardKeyBuilder(String cardNameField, String setNameField) {
		this(cardNameField, setNameField, null);
	}
	
	public VagueNamesJsonCardKeyBuilder(String cardNameField, String setNameField, String typeNameField) {
		this.setNameField = setNameField;
		this.cardNameField = cardNameField;
		this.typeNameField = typeNameField;
	}
	
	public void addSetNameSubstitution(String find, String replace) {
		setNameSubstitutions.put(find, replace);
	}
	public void addCardNameSubstitution(String find, String replace) {
		cardNameSubstitutions.put(find, replace);
	}
	public void addTypeNameSubstitution(String find, String replace) {
		typeNameSubstitutions.put(find, replace);
	}

	@Override
	public void startCard() {
		setName = null;
		cardName = null;
	}

	@Override
	public void readField(String fieldName, JsonToken valueToken, JsonParser parser) throws JsonParseException, IOException {
		
		if(setNameField.equals(fieldName)) {
			setName = parser.getValueAsString();
			for(Entry<String, String> sub : setNameSubstitutions.entrySet()) {
				setName = setName.replace(sub.getKey(), sub.getValue());
			}
		} else if(cardNameField.equals(fieldName)) {
			cardName = parser.getValueAsString();
			for(Entry<String, String> sub : cardNameSubstitutions.entrySet()) {
				cardName = cardName.replace(sub.getKey(), sub.getValue());
			}
		} else if(typeNameField != null && typeNameField.equals(fieldName)) {
			typeName = parser.getValueAsString();
			for(Entry<String, String> sub : typeNameSubstitutions.entrySet()) {
				typeName = typeName.replace(sub.getKey(), sub.getValue());
			}
		}
	}

	@Override
	public void endCard() {}
	
	public abstract K buildKey();
	
}

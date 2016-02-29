package uk.co.haradan.octgnimageloader.cardkeys;

public class VagueNamesJsonCardKeyBuilderDefault extends VagueNamesJsonCardKeyBuilder<VagueNamesCardKey> {
	
	public VagueNamesJsonCardKeyBuilderDefault(String cardNameField, String setNameField) {
		super(cardNameField, setNameField);
	}
	
	public VagueNamesJsonCardKeyBuilderDefault(String cardNameField, String setNameField, String typeNameField) {
		super(cardNameField, setNameField, typeNameField);
	}

	@Override
	public VagueNamesCardKey buildKey() {
		return new VagueNamesCardKey(cardName, setName, typeName);
	}

}

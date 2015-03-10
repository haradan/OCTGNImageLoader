package uk.co.haradan.netrunnerimageloader.cardkeys;

import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;


public class StringToVariableCase implements StringProcessor {
	
	public static final StringToVariableCase PROCESSOR = new StringToVariableCase();
	
	private StringToVariableCase() {
	}
	
	public String process(String string) {
		
		// Remove accents
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		string = string.replaceAll("\\p{M}", "");

		// Split into words and convert to Java variable case
		String[] parts = string.split("[^A-Za-z0-9]+");
		parts[0] = parts[0].toLowerCase();
		for(int i=1; i<parts.length; i++) {
			String part = parts[i];
			char[] arr = part.toCharArray();
			arr[0] = Character.toUpperCase(arr[0]);
			for(int j=1; j<arr.length; j++) {
				arr[j] = Character.toLowerCase(arr[j]);
			}
			parts[i] = new String(arr);
		}
		return StringUtils.join(parts);
	}
	
}

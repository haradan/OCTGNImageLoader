package uk.co.haradan.octgnimageloader.cardkeys;

import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;


public class StringToClassCase {
	
	public static String process(String string) {
		if(string == null) return null;
		
		// Remove accents
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		string = string.replaceAll("\\p{M}", "");

		// Split into words and convert to Java variable case
		String[] parts = string.split("[^A-Za-z0-9]+");
		for(int i=0; i<parts.length; i++) {
			String part = parts[i];
			if(part.length() < 1) continue;
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

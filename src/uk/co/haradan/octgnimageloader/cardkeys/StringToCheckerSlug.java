package uk.co.haradan.octgnimageloader.cardkeys;

import java.text.Normalizer;


public class StringToCheckerSlug implements StringProcessor {
	
	public static final StringToCheckerSlug PROCESSOR = new StringToCheckerSlug();
	
	private StringToCheckerSlug() {
	}
	
	public String process(String string) {
		return convert(string);
	}
	
	public static String convert(String string) {
		
		// Remove accents
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		string = string.replaceAll("\\p{M}", "");
		
		// Remove all non-alphanumeric, make lower case
		return string.replaceAll("[^A-Za-z0-9]+", "").toLowerCase();
	}
	
}

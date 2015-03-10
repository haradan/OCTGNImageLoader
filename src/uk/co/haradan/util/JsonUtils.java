package uk.co.haradan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	
	public static JsonNode readJSON(JsonFactory jsonFactory, String url) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		return readJSON(jsonFactory, url, null, null);
	}
	
	public static JsonNode readJSON(JsonFactory jsonFactory, String url, String username, String password) throws KeyManagementException, NoSuchAlgorithmException, IOException {

		URLConnection connection = HttpUtils.getConnection(url, username, password);
		connection.setReadTimeout(60000); // Read timeout of 1 minute
		InputStream stream = connection.getInputStream();
		
		try {
			return readJSON(jsonFactory, stream);
		} finally {
			stream.close();
		}
	}
	
	public static JsonNode readJSON(JsonFactory jsonFactory, File file) throws KeyManagementException, NoSuchAlgorithmException, IOException {

		InputStream stream = new FileInputStream(file);
		
		return readJSON(jsonFactory, stream);
	}
	
	public static JsonNode readJSON(JsonFactory jsonFactory, InputStream stream) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		
		JsonParser parser = jsonFactory.createParser(stream);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(parser);
		stream.close();
		
		return rootNode;
	}
	
	public static JsonNode readJSON(JsonFactory jsonFactory, Reader reader) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		
		JsonParser parser = jsonFactory.createParser(reader);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(parser);
		reader.close();
		
		return rootNode;
	}
	
	public static JsonNode readJSONCode(JsonFactory jsonFactory, String code) throws KeyManagementException, NoSuchAlgorithmException, IOException {

		JsonParser parser = jsonFactory.createParser(code);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(parser);
		
		return rootNode;
	}

}

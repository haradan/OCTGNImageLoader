package uk.co.haradan.util;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class HttpUtils {
	
	public static URLConnection getConnection(String url) throws KeyManagementException, NoSuchAlgorithmException, IOException {
		return getConnection(url, null, null);
	}

	public static URLConnection getConnection(String url, String username, String password) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		if(username != null && username.length() > 0) {
			Authenticator.setDefault(new MyAuthenticator(username, password));
		}	
		
		URL urlObj = new URL(url);
		
		URLConnection connection = urlObj.openConnection();
		connection.setReadTimeout(30000);
		connection.setConnectTimeout(30000);

		return connection;
	}
	
	private static final String CONTENT_TYPE_MATCH = "charset=";
	
	public static Charset readCharset(URLConnection conn) throws IllegalCharsetNameException, UnsupportedCharsetException {
		Charset charset = Charset.forName("UTF-8");
		String contentType = conn.getContentType();
		if(contentType != null) {
			int charsetPos = contentType.indexOf(CONTENT_TYPE_MATCH);
			if(charsetPos > 0) {
				charsetPos += CONTENT_TYPE_MATCH.length();
				String charsetName = contentType.substring(charsetPos);
				charset = Charset.forName(charsetName);
			}
		}
		return charset;
	}

	private static class MyAuthenticator extends Authenticator {
		private final String username, password;
	
		public MyAuthenticator(String user, String pass) {
			username = user;
			password = pass;
		}
	
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			System.out.println("Requesting Host  : " + getRequestingHost());
			System.out.println("Requesting Port  : " + getRequestingPort());
			System.out.println("Requesting Prompt : " + getRequestingPrompt());
			System.out.println("Requesting Protocol: "
					+ getRequestingProtocol());
			System.out.println("Requesting Scheme : " + getRequestingScheme());
			System.out.println("Requesting Site  : " + getRequestingSite());
			return new PasswordAuthentication(username, password.toCharArray());
		}
	}

}

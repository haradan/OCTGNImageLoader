package uk.co.haradan.netrunnerimageloader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import uk.co.haradan.util.HttpUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class NetrunnerImageLoader {
	
	private static final String NETRUNNERDB_CARDS_URL = "http://netrunnerdb.com/api/cards/";
	private static final String NETRUNNERDB_IMAGE_BASE_URL = "http://netrunnerdb.com";
	
	private static final String NETRUNNER_PLUGIN_DIRNAME = "0f38e453-26df-4c04-9d67-6d43de939c77";
	
	private boolean isActive = false;
	private boolean isAbort = false;
	
	private AbortListener abortListener;
	
	public static interface AbortListener {
		public boolean isAbort();
	}
	
	private synchronized void activate() throws InterruptedException {
		if(isActive) {
			isAbort = true;
			wait();
		}
		isActive = true;
		isAbort = false;
	}
	
	private synchronized boolean deactivate() {
		if(! isActive) return false;
		isActive = false;
		notify();
		return true;
	}
	
	private synchronized void checkAbort() throws AbortException {
		if(isAbort
		|| (abortListener != null && abortListener.isAbort())) {
			throw new AbortException();
		}
	}
	
	private static class AbortException extends Exception {
		private static final long serialVersionUID = 5965018251907378579L;
	}
	
	public void downloadOctgnImages(LogOutput log, File octgnDir, AbortListener abortListener) {
		try {
			activate();
		} catch (InterruptedException e) {
			return;
		}
		this.abortListener = abortListener;
		try {
			doDownloadOctgnImages(log, octgnDir);
		} catch (AbortException e) {
			log.errorln("Aborted");
		} finally {
			deactivate();
		}
	}
	
	private void doDownloadOctgnImages(LogOutput log, File octgnDir) throws AbortException {
		
		if(! octgnDir.isDirectory()) {
			log.errorln("Specified OCTGN data directory does not exist, please ensure it is correct or install OCTGN.");
			return;
		}
		
		log.println("Using OCTGN data directory: "+octgnDir.getAbsolutePath());
		
		List<Set> sets;
		try {
			sets = readSets(log, octgnDir);
		} catch (LoaderMsgException e) {
			log.errorln(e.getMessage());
			return;
		} catch (Exception e) {
			log.error(e);
			return;
		}
		
		log.println("Using NetrunnerDB cards feed: "+NETRUNNERDB_CARDS_URL);
		
		Map<String, WebsiteCard> cardsByNum;
		try {
			cardsByNum = readCardsByNum(NETRUNNERDB_CARDS_URL);
		} catch (Exception e) {
			log.error(e);
			return;
		}
		
		downloadImages(log, octgnDir, cardsByNum, sets);
		
		log.println("Complete");
	}
	
	private static Map<String, WebsiteCard> readCardsByNum(String url) throws JsonParseException, IOException, KeyManagementException, NoSuchAlgorithmException {
		URLConnection conn = HttpUtils.getConnection(NETRUNNERDB_CARDS_URL);
		InputStream is = conn.getInputStream();
		try {
			JsonFactory jsonFactory = new JsonFactory();
			JsonParser parser = jsonFactory.createParser(is);
			
			return readCardsByNum(parser);
			
		} finally {
			is.close();
		}
	}
	
	private static Map<String, WebsiteCard> readCardsByNum(JsonParser parser) throws JsonParseException, IOException {
		
		JsonToken token = parser.nextToken();
		if(token != JsonToken.START_ARRAY) {
			return null;
		}
		
		Map<String, WebsiteCard> cardsByNum = new HashMap<String, WebsiteCard>();

		WebsiteCard card = null;
		while((token = parser.nextToken()) != null) {
			
			if(token == JsonToken.START_OBJECT) {
				card = new WebsiteCard();
				
			} else if(token == JsonToken.FIELD_NAME) {
				String fieldName = parser.getCurrentName();
				if("title".equals(fieldName)) {
					String title = parser.nextTextValue();
					card.setTitle(title);
				} else if("number".equals(fieldName)) {
					card.setCardNum(parser.nextIntValue(card.getCardNum()));
				} else if("cyclenumber".equals(fieldName)) {
					card.setCycleNum(parser.nextIntValue(card.getCycleNum()));
				} else if("imagesrc".equals(fieldName)) {
					String url = parser.nextTextValue();
					card.setImgUrl(url);
				}
				
			} else if(token == JsonToken.END_OBJECT) {
				
				// OCTGN card ids always end with their cycle number and card number in this format, so we can identify them this way.
				String num = String.format("%1$02d%2$03d", card.getCycleNum(), card.getCardNum());
				cardsByNum.put(num, card);
				
			} else if(token == JsonToken.END_ARRAY) {
				break;
			}
		}
		
		return cardsByNum;
	}
	
	private List<Set> readSets(LogOutput log, File octgnDir) throws ParserConfigurationException, SAXException, LoaderMsgException {
		
		File gameDbDir = new File(octgnDir, "GameDatabase/"+NETRUNNER_PLUGIN_DIRNAME);
		if(! gameDbDir.isDirectory()) {
			throw new LoaderMsgException("Could not find OCTGN Netrunner plugin directory, is it installed?");
		}
		
		File gameDbSetsDir = new File(gameDbDir, "Sets");
		File[] setDirs = gameDbSetsDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File arg) {
				return arg.isDirectory();
			}
		});
		if(setDirs == null || setDirs.length < 1) {
			throw new LoaderMsgException("Could not find OCTGN Netrunner sets, have cards been downloaded?");
		}

	    SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		
		List<Set> sets = new ArrayList<Set>();
		for(File setDir : setDirs) {
			File setXml = new File(setDir, "set.xml");
			SetXmlHandler handler = new SetXmlHandler(setDir.getName());
		    try {
				parser.parse(setXml, handler);
			} catch (Exception e) {
				log.error(e);
				continue;
			}
		    sets.add(handler.getSet());
		}
		
		return sets;
	}
	
	private void downloadImages(LogOutput log, File octgnDir, Map<String, WebsiteCard> cardsByNum, List<Set> sets) throws AbortException {
		
		File imageSetsDir = new File(octgnDir, "ImageDatabase/"+NETRUNNER_PLUGIN_DIRNAME+"/Sets");
		imageSetsDir.mkdirs();
		
		for(Set set : sets) {
			final String setId = set.getId();
			log.println("Downloading cards for set \""+set.getName()+"\" ("+setId+")");
			
			File setImgDir = new File(imageSetsDir, setId+"/Cards");
			setImgDir.mkdirs();
			
			for(SetCard setCard : set.getCards()) {
				checkAbort();

				// Confirm this card can be downloaded
				final String cardId = setCard.getId();
				String num = cardId.substring(cardId.length() - 5);
				WebsiteCard card = cardsByNum.get(num);
				if(card == null) {
					log.println("Card not in website: \""+setCard.getName()+"\" ("+cardId+")");
					continue;
				}
				String url = card.getImgUrl();
				if(url == null) {
					log.println("Card without image URL: \""+card.getTitle()+"\" (#"+num+", id "+cardId+")");
					continue;
				}
				url = NETRUNNERDB_IMAGE_BASE_URL+url;

				// Download new card file
				File downloadFile = new File(setImgDir, cardId+".png.tmp");
				try {
					URLConnection conn = HttpUtils.getConnection(url);
					InputStream is = conn.getInputStream();
					try {
						FileOutputStream os = new FileOutputStream(downloadFile);
						try {
							IOUtils.copy(is, os);
						} finally {
							os.close();
						}
					} finally {
						is.close();
					}
				} catch (Exception e) {
					log.error(e);
					continue;
				}
				
				// Delete old card files
				File[] cardFiles = setImgDir.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						if(!file.isFile()) return false;
						String name = file.getName();
						return name.startsWith(cardId) && ! name.endsWith(".tmp");
					}
				});
				for(File cardFile : cardFiles) {
					cardFile.delete();
				}

				// Install new card file
				File cardFile = new File(setImgDir, cardId+".png");
				downloadFile.renameTo(cardFile);
			}
		}
		
	}

}

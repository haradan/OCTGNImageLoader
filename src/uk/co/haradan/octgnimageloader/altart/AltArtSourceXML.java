package uk.co.haradan.octgnimageloader.altart;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.haradan.octgnimageloader.SetCard;
import uk.co.haradan.octgnimageloader.cardkeys.StringToCheckerSlug;
import uk.co.haradan.util.HttpUtils;

public class AltArtSourceXML extends AltArtAbstractSource {
	
	private final String foundAtUrl;
	private final Node imageNode;
	private final String imageUrl;
	private final List<String> matchableSlugs;
	
	public AltArtSourceXML(String foundAtUrl, Node imageNode, String imageUrl, List<String> matchableSlugs) {
		this.foundAtUrl = foundAtUrl;
		this.imageNode = imageNode;
		this.imageUrl = imageUrl;
		this.matchableSlugs = matchableSlugs;
	}
	
	public String getFoundAtUrl() {
		return foundAtUrl;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public Node getImageNode() {
		return imageNode;
	}
	
	@Override
	public boolean isMatchCardsSupported() {
		return true;
	}
	
	@Override
	public boolean isCardMatch(SetCard card) {
		String cardName = card.getName();
		String cardNameSlug = StringToCheckerSlug.convert(cardName);
		for(String slug : matchableSlugs) {
			if(slug.contains(cardNameSlug)) return true;
		}
		return false;
	}

	@Override
	protected InputStream openInputStream() throws KeyManagementException, NoSuchAlgorithmException, IOException {
		URLConnection conn = HttpUtils.getConnection(imageUrl);
		return conn.getInputStream();
	}
	
	public static List<AltArtSourceXML> scanWebpage(String url) throws ParserConfigurationException, SAXException, IOException {

		List<AltArtSourceXML> images = new ArrayList<AltArtSourceXML>();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(url);
		NodeList imageNodes = doc.getElementsByTagName("img");
		int numImageNodes = imageNodes.getLength();
		for(int i=0; i<numImageNodes; i++) {
			Node imageNode = imageNodes.item(i);
			NamedNodeMap attributes = imageNode.getAttributes();
			Node srcNode = attributes.getNamedItem("src");
			if(srcNode != null) {
				String imageUrl = srcNode.getTextContent();
				List<String> matchableSlugs = new ArrayList<String>();
				gatherMatchableSlugs(imageNode, matchableSlugs, "src");
				AltArtSourceXML image = new AltArtSourceXML(url, imageNode, imageUrl, matchableSlugs);
				images.add(image);
			}
		}
		
		return images;
	}
	
	private static void gatherMatchableSlugs(Node node, List<String> gatheredSlugs, String ignoreAttribute) {
		NamedNodeMap attributes = node.getAttributes();
		int numAttributes = attributes.getLength();
		for(int i=0; i<numAttributes; i++) {
			Node attributeNode = attributes.item(i);
			String name = attributeNode.getNodeName();
			if(ignoreAttribute != null && name.equalsIgnoreCase(ignoreAttribute)) {
				continue;
			}
			String value = attributeNode.getNodeValue();
			String foundSlug = StringToCheckerSlug.convert(value);
			gatheredSlugs.add(foundSlug);
		}
		Node parent = node.getParentNode();
		if(parent != null && parent != node) {
			gatherMatchableSlugs(parent, gatheredSlugs, null);
		}
	}
	
}

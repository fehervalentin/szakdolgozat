package hu.elte.bfw1p6.poker.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A kliens és a szerver által közösen használt property értékek.
 * @author feher
 *
 */
public class PokerProperties {
	private final static String svPropFile = "server.properties";
	
	private Properties properties;
	private static PokerProperties instance = null;
	
	private PokerProperties() throws IOException {
		properties = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(svPropFile);
		
		if (inputStream == null) {
			throw new FileNotFoundException(svPropFile + " file not found on classpath!");
		} else if (inputStream != null) {
			properties.load(inputStream);
		}
	}
	
	public String getProperty(String prop) {
		return properties.getProperty(prop);
	}
	
	public static PokerProperties getInstance() {
		if (instance == null) {
			try {
			instance = new PokerProperties();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		return instance;
	}
}

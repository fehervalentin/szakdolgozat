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
public class PokerProperties extends Properties {

	private static final long serialVersionUID = 3063357583007464160L;

	private final static String svPropFile = "server.properties";

	private static PokerProperties instance = null;

	private PokerProperties() throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(svPropFile);

		if (inputStream == null) {
			throw new FileNotFoundException(svPropFile + " file not found on classpath!");
		} else if (inputStream != null) {
			load(inputStream);
		}
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
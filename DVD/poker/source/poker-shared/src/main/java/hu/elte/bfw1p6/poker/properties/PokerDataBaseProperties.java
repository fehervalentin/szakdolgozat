package hu.elte.bfw1p6.poker.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PokerDataBaseProperties extends Properties {

	private static final long serialVersionUID = 2655325382237474795L;

	private final static String svPropFile = "database.properties";

	private static PokerDataBaseProperties instance = null;

	private PokerDataBaseProperties() throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(svPropFile);

		if (inputStream == null) {
			throw new FileNotFoundException(svPropFile + " file not found on classpath!");
		} else if (inputStream != null) {
			load(inputStream);
		}
	}

	public static PokerDataBaseProperties getInstance() {
		if (instance == null) {
			try {
				instance = new PokerDataBaseProperties();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		return instance;
	}
}
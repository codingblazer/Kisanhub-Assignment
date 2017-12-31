package Assignment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The programs implements a program to download weather data from metoffice and
 * parse it to obtain information in desired csv format.
 * 
 * @author SachinAggarwal
 * @version 1.0
 */

public class Solution {

	private static final String[] regions = { "UK", "England", "Wales", "Scotland" };
	private static final String[] weatherParams = { "Tmax", "Tmin", "Tmean", "Sunshine", "Rainfall" };
	private static final String baseUrl = "https://www.metoffice.gov.uk/pub/data/weather/uk/climate/datasets/";
	private static final String basePath = "/Users/sachinaggarwal/eclipse-workspace/Kisanhub/src/Assignment/";
	private static final String outputPath = "/Users/sachinaggarwal/eclipse-workspace/Kisanhub/src/Assignment/out.csv";
	private static final String orderType = "/date/"; // statistics in yearly order
	private static final String NOT_AVAILABLE = "N/A";
	private static FileWriter fw;
	private static String[] keys;

	private static void createCsv() {
		try {
			fw = new FileWriter(outputPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void downloadFiles() {
		for (String region : regions) {
			for (String param : weatherParams) {
				String url = baseUrl + param + orderType + region + ".txt";
				String fileName = region + "_" + param + ".txt";
				String filePath = basePath + fileName;
				try {
					downloadFile(url, filePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * This method downloads the given file if it doesn't exist.
	 * 
	 * @param url
	 *            This is the url of file to be downloaded.
	 * @param filePath
	 *            This is the path where downloaded file is saved.
	 */
	private static void downloadFile(String url, String filePath) {
		if (Files.exists(Paths.get(filePath)))
			return;
		Path path = Paths.get(filePath);
		URI uri = URI.create(url);
		try (InputStream in = uri.toURL().openStream()) {
			Files.copy(in, path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private static void parseFiles() throws IOException {
		for (String region : regions) {
			for (String param : weatherParams) {
				String fileName = region + "_" + param + ".txt";
				String filePath = basePath + fileName;
				parseFile(filePath, region, param);
			}
		}
	}

	
	/**
	 * This method is used to parse the the file read the lines. The first 7 lines
	 * are skipped which is the metadata of file.
	 * 
	 * @param from
	 *            The path of the file to be parsed.
	 * @param region
	 *            The file belongs to this region.
	 * @param param
	 *            The file belongs to this weather_param.
	 */
	private static void parseFile(String from, String region, String param) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(from));
			String line;
			for (int lineCount = 0; (line = reader.readLine()) != null; lineCount++) {
				if (lineCount == 7) {
					storeKeys(line);
				} else if (lineCount > 7) {
					parseLine(line, region, param);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	private static void storeKeys(String line) {
		keys = line.split("\\s{1,5}");
	}

	
	/**
	 * This method is used to parse the the line and write it to .csv.
	 * 
	 * @param line
	 *            The line from .txt file
	 * @param region
	 *            The file belongs to this region.
	 * @param param
	 *            The file belongs to this weather_param.
	 */
	private static void parseLine(String line, String region, String param) {
		String[] values = line.split("\\s{1,6}");
		String year = values[0];
		for (int i = 1; i < values.length; i++) {
			if ("".equals(values[i]) || "---".equals(values[i])) {
				values[i] = NOT_AVAILABLE;
			}
			writeToCsv(region, param, year, keys[i], values[i]);
		}

	}

	
	/**
	 * This method writes the line to .csv in specific format.
	 * 
	 * @param region
	 *            The file belongs to this region.
	 * @param param
	 *            The file belongs to this weather_param.
	 * @param year
	 *            The year of data.
	 * @param key
	 *            The key to which value belongs to.
	 * @param value
	 *            The value corresponding to key.
	 */

	private static void writeToCsv(String region, String param, String year, String key, String value) {
		try {
			fw.append(region).append(",");
			fw.append(param).append(",");
			fw.append(year).append(",");
			fw.append(key).append(",");
			fw.append(value).append(",");
			fw.append(System.getProperty("line.separator"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private static void finish() {
		try {
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		createCsv();
		downloadFiles();
		parseFiles();
		finish();
	}

}

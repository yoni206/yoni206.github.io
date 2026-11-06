
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;


public class Utils {
	public static String getFullPathNoEndSeparator(String fileName) {
		return FilenameUtils.getFullPathNoEndSeparator(fileName);
	}
	public static String getBaseName(String fileName) {
		return FilenameUtils.getBaseName(fileName);
	}
	public static void writeStringToFile(File file, String data) throws IOException {
		FileUtils.writeStringToFile(file, data);
	}
	public static String getFileAsString(String fullPath) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fullPath));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        String everything = sb.toString();
	        return everything;
	    } finally {
	        br.close();
	    }
	}
	
	public static List<String> getFileAsListOfLines(String fullPath) throws FileNotFoundException, IOException {
		return IOUtils.readLines(new FileInputStream(new File(fullPath)));
	}
}

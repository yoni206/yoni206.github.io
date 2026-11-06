import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;




public class SmvUtils {
	public static String getFullPathNoEndSeparator(String fileName) {
		int lastSlash = lastIndexOfSlash(fileName);
		return fileName.substring(0, lastSlash);
	}
	public static String getBaseName(String fileName) {
		int lastSlash = lastIndexOfSlash(fileName);
		return fileName.substring(lastSlash+1);
	}
	public static void writeStringToFile(File file, String data) throws IOException {
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter( 
					new FileWriter( file.getAbsolutePath()));
			writer.write( data);

		}
		catch ( IOException e)
		{
		}
		finally
		{
			try
			{
				if ( writer != null)
					writer.close( );
			}
			catch ( IOException e)
			{
			}
	     }
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
	
	private static int lastIndexOfSlash(String str) {
		return max(str.lastIndexOf("/"), str.lastIndexOf("\\"));
	}
	private static int max(int num1, int num2) {
		return num1 > num2 ? num1 : num2;
	}
}

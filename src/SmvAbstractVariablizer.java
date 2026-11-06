
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * This class transforma a *.asmv file to a *.smv file, using a *.txt file for the abstract variables. 
 * @author yoni
 *
 */
public class SmvAbstractVariablizer {
	static Pattern XXXorYYYPatern = Pattern.compile("[XY][0-9]+[(].*");
	static Pattern rangePattern = Pattern.compile("[XY][0-9]+[_^][0-9]+.*");
	

	
	private static String replaceXXXorYYY(String formula) {
		char XorY = formula.charAt(0);
		int beginOfActualFormula = formula.indexOf('(');
		int endOfActualFormula = formula.length();
		int count = Integer.parseInt(formula.substring(1, beginOfActualFormula)); 
		String actualFormula = formula.substring(beginOfActualFormula, endOfActualFormula);
		String ret=actualFormula;
		for(int i=0; i<count;i++) {
			ret = XorY + "(" + ret + ")";
		}
		return ret; 
	}
	
	private static String replaceRange(String formula) {
		char XorY = formula.charAt(0);
		int beginOfActualFormula = formula.indexOf('(');
		int endOfActualFormula = formula.length();
		int indexOfSeperator = getIndexOfSeperator(formula); 
		char seperator = formula.charAt(indexOfSeperator);
		char andOrOr = (seperator == '^') ? '&' : '|';
		int minima = Integer.parseInt(formula.substring(1, indexOfSeperator));
		int maxima = Integer.parseInt(formula.substring(indexOfSeperator+1, beginOfActualFormula));
		String actualFormula = formula.substring(beginOfActualFormula, endOfActualFormula);
		String ret = "";
		for (int i=minima; i<=maxima; i++) {
			ret += "(" + XorY + i + "(" + actualFormula + ")" + ")" + andOrOr;
		}
		int lastAndOrOr = Math.max(ret.lastIndexOf("&"), ret.lastIndexOf("|")) - 1;
		ret = ret.substring(0, lastAndOrOr+1);
		return ret;
	}
	
	private static int getIndexOfSeperator(String formula) {
		int indexOfKova = formula.indexOf('^');
		int indexOfUnderscore = formula.indexOf('_');
		int indexOfSeperator;
		if (indexOfKova == -1) {
			indexOfSeperator = indexOfUnderscore;
		} else if (indexOfUnderscore == -1) {
			indexOfSeperator = indexOfKova;
		} else {
			indexOfSeperator = Math.min(indexOfKova, indexOfUnderscore);
		}
		return indexOfSeperator;
	}
	
	private static String replaceAllXXXorYYY(String abstractSmv) {
		while(XXXorYYYexists(abstractSmv)) {
			int firstOccurenceOfXorY = findFirstOccurenceOfXOrY(abstractSmv);
			int openingParenthes = abstractSmv.substring(firstOccurenceOfXorY).indexOf('(') + firstOccurenceOfXorY;
			int enfOfXorYFormula = findClosingParenthes(abstractSmv.substring(openingParenthes))+openingParenthes+1;
			String entireXXXorYYYFormula = abstractSmv.substring(firstOccurenceOfXorY, enfOfXorYFormula);
			abstractSmv = abstractSmv.replaceFirst(Pattern.quote(entireXXXorYYYFormula), replaceXXXorYYY(entireXXXorYYYFormula));
		}
		return abstractSmv;
	}
	
	private static String replaceRanges(String abstractSmv) {
		while (rangeExists(abstractSmv)) {
			int firstOccurrenceOfRange = findFirstOccurrenceOfRange(abstractSmv);
			int openingParenthes = abstractSmv.substring(firstOccurrenceOfRange).indexOf('(') + firstOccurrenceOfRange;
			int endOfRangeFormula = findClosingParenthes(abstractSmv.substring(openingParenthes)) + openingParenthes + 1;
			String entireRangeFormula = abstractSmv.substring(firstOccurrenceOfRange, endOfRangeFormula);
			abstractSmv = abstractSmv.replaceFirst(Pattern.quote(entireRangeFormula), replaceRange(entireRangeFormula));
		}
		return abstractSmv;
	}
	
	private static int findClosingParenthes(String substring) {
		int openedParentheses = 1;
		int i=0;
		while (openedParentheses > 0) {
			i++;
			if (substring.charAt(i) == '(') {
				openedParentheses++;
			}
			if (substring.charAt(i) == ')') {
				openedParentheses--;
			}
		}
		return i; 
	}

	private static int findFirstOccurenceOfXOrY(String abstractSmv) {
		int indexOfXXXOrYYY = -1;
		Matcher m = XXXorYYYPatern.matcher(abstractSmv);
		if (m.find()) {
			indexOfXXXOrYYY = m.start();
		}
		return indexOfXXXOrYYY;
	}
	
	private static int findFirstOccurrenceOfRange(String abstractSmv) {
		int indexOfRange = -1;
		Matcher m = rangePattern.matcher(abstractSmv);
		if (m.find()) {
			indexOfRange = m.start();
		}
		return indexOfRange;
	}

	private static boolean XXXorYYYexists(String abstractSmv) {	
		int indexOfXXXOrYYY = findFirstOccurenceOfXOrY(abstractSmv);
		return indexOfXXXOrYYY > -1;
	}

	private static boolean rangeExists(String abstractSmv) {
		int indexOfRange = findFirstOccurrenceOfRange(abstractSmv);
		return indexOfRange > -1;
	}
	/**
	 * This method replaces each abstract variable with its value.
	 * It does so recursively, meaning that one can define abstract variables using other abstract variables. 
	 * @param abstractSmv
	 * @param abstractVariables
	 * @return abstractSmv, after replacing (recursively) all abstractVariables.
	 * @throws IOException
	 */
	private static String replaceAbstractVariables(String abstractSmv, Properties abstractVariables) throws IOException {
		String replacedSmv = abstractSmv;
		Set<Object> abstractVariableNames = abstractVariables.keySet();
		//for debugging purposes
		int iterations = 0;
		
		while (abstractVariablesExists(replacedSmv, abstractVariableNames)) {
			iterations++;
			for (Object obj : abstractVariableNames) {
				String abstractVariableName = obj.toString();
				String abstractVariableValue = abstractVariables.getProperty(abstractVariableName);
				replacedSmv = replacedSmv.replaceAll(abstractVariableName, abstractVariableValue);
				System.out.println(abstractVariableName + "\t=\t" + abstractVariableValue);
			}
			//FileUtils.writeStringToFile(new File("c:/tmp" + iterations), replacedSmv);
		}
		return replacedSmv;
	}
	
	
	/**
	 * This method checks whether the processed .asmv file contains abstract variables.
	 * @param abstractSmv
	 * @param abstractVariableNames
	 * @return true iff the processed .asmv file still contains abstract variables
	 */
	private static boolean abstractVariablesExists(String abstractSmv,
			Set<Object> abstractVariableNames) {
		boolean ret = false;
		for (Object obj : abstractVariableNames) {
			String abstractVariable = obj.toString();
			if (abstractSmv.contains(abstractVariable)) {
				ret = true;
			}
		}
		return ret;
	}

/**
 * This method loads the .asmv file, changes it to a .smv file and saved the .smv file in the desired location.
 * @param abstractSmvFilePath
 * @param abstractVariablesFilePath
 * @param compiledSmvFilePath
 * @throws IOException
 */
	private static void compileAbstractSmv(String abstractSmvFilePath, String compiledSmvFilePath) throws IOException {
		String fullAbstractSmv = SmvUtils.getFileAsString(abstractSmvFilePath);
		String compiledSmv = compileAbstractSmv(fullAbstractSmv);		
		SmvUtils.writeStringToFile(new File(compiledSmvFilePath), compiledSmv);
	}
	
	private static String compileAbstractSmv(String abstractSmv) throws IOException {
		int beginningOfAbstracts = abstractSmv.indexOf("ABSTRACTIONS");
		String abstractsPorion = abstractSmv.substring(beginningOfAbstracts);
		abstractSmv = abstractSmv.substring(0, beginningOfAbstracts-1);
		Properties abstractVariables = createAbstractVariables(abstractsPorion);
		
		String compiledSmv = abstractSmv;
		compiledSmv = replaceAbstractVariables(compiledSmv, abstractVariables);
		compiledSmv = replaceRanges(compiledSmv);
		compiledSmv = replaceAllXXXorYYY(compiledSmv);
		return compiledSmv;
	}
	
/*	private static Properties createAbstractVariables(String abstractsPorion) throws IOException { 
	//create tmp file
		File tempAbstractVariablesFile = new File("c:/temp.txt");
		abstractsPorion=abstractsPorion.replaceAll("--", "#");
		SmvUtils.writeStringToFile(tempAbstractVariablesFile, abstractsPorion);
		FileInputStream is = new FileInputStream(tempAbstractVariablesFile);
	//load
		Properties abstractVariables = new Properties();
		abstractVariables.load(is);
		is.close();
		addParenthasesIfNeeded(abstractVariables);
	//delete temp file
		tempAbstractVariablesFile.delete();
	//return
		return abstractVariables;
	
	}
	*/
	private static Properties createAbstractVariables(String abstractsPorion) throws IOException { 
		
		// convert String into InputStream
		InputStream is = new ByteArrayInputStream(abstractsPorion.getBytes());
		//load
			Properties abstractVariables = new Properties();
			abstractVariables.load(is);
			is.close();
			addParenthasesIfNeeded(abstractVariables);
		//return
			return abstractVariables;
		
		}

	private static void addParenthasesIfNeeded(Properties abstractVariables) {
		for(Object o : abstractVariables.keySet()) {
			String formula = abstractVariables.getProperty(o.toString());
			if (!formula.startsWith("(")){
				formula = "(" + formula + ")";
				abstractVariables.setProperty(o.toString(), formula);
			}
		}
	}

	/**
	 * args[0] is the full path of the *.asmv file. This file is an smv file which contains abstract variables.
	 * args[1] is the full path of the *.txt file which contains all the definitions of the abstract variables in args[0].
	 * args[2] is the full desired path of the generated *.smv file.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String abstractSmvFilePath = args[0];
		String compiledSmvFilePath = SmvUtils.getFullPathNoEndSeparator(abstractSmvFilePath) + "/" + SmvUtils.getBaseName(abstractSmvFilePath) + ".smv";
		compileAbstractSmv(abstractSmvFilePath, compiledSmvFilePath);
		System.out.println("done. smv saved to: " + compiledSmvFilePath);
	} 
	
	public static String removeAbstracts(String smvWithAbstracts) throws IOException {
		return compileAbstractSmv(smvWithAbstracts);
	}
}

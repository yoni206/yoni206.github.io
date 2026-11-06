import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Tex2Smv {
	private static Pattern mathFormulaPattern = Pattern.compile("\\Q$\\E[^\\Q$\\E]*\\Q$\\E");
	private static Pattern XXXorYYYPattern = Pattern.compile("[XY][\\^][{][0-9]+[}]");
	private static Pattern rangePattern = Pattern.compile("[XY][_^][{][0-9]+,[0-9]+[}]");
	private static Map<String,String> thingsToReplace;
	
	public static void main(String[] args) throws IOException {
		init();
		String texFilePath = args[0];
		String outputFilePath = SmvUtils.getFullPathNoEndSeparator(texFilePath) + "/" + SmvUtils.getBaseName(texFilePath) + ".txt";
		compileTex(texFilePath, outputFilePath);
		System.out.println("done. smv saved to: " + outputFilePath);
	}

	private static void init() {
		thingsToReplace = new HashMap<String, String>();
		thingsToReplace.put("\\\\wedge", " & ");
		thingsToReplace.put("\\\\vee", " | ");
		thingsToReplace.put("\\\\rightarrow", " -> ");
		thingsToReplace.put("\\\\neg", "!");
		
		thingsToReplace.put("\\\\approx", "Y");
		thingsToReplace.put("\\\\Box", "G");
		thingsToReplace.put("\\\\Diamond", "F");
		
		thingsToReplace.put("\\\\left\\(", "(");
		thingsToReplace.put("\\\\right\\)", ")");
		thingsToReplace.put("\\\\_", "_");
		thingsToReplace.put("\\\\,", " ");
	}

	private static void compileTex(String texFilePath, String outputFilePath) throws IOException {
		String texData = SmvUtils.getFileAsString(texFilePath);
		List<String> formulas = getAllMathFormulas(texData);
		formulas = changeFormulas(formulas);
		formulas = removeDuplicates(formulas);
		formulas = removeNonFormulas(formulas);
		formulas = trim(formulas);
		System.out.println(formulas);
		String outputData = formulas.toString().replace(", ", "\n");
		outputData = outputData.substring(1, outputData.length()-1);
		File outputFile = new File(outputFilePath);
		SmvUtils.writeStringToFile(outputFile, outputData);
	}

	private static List<String> trim(List<String> formulas) {
		List<String> tmp = new ArrayList<String>();
		for (String str : formulas) {
			str = removeDoubleSpaces(str);
			tmp.add(str);
		}
		return tmp;
	}

	private static String removeDoubleSpaces(String str) {
		while (str.contains("  ")) {
			str = str.replaceAll("  ", " ");
		}
		return str;
	}

	private static List<String> removeNonFormulas(List<String> formulas) {
		List<String> ret = new ArrayList<String>();
		for (String str : formulas) {
			if (!notAFormula(str)) {
				ret.add(str);
			}
		}
		return ret;
		
	}

	private static boolean notAFormula(String str) {
		return str.length()<=1;
	}

	private static List<String> removeDuplicates(List<String> formulas) {
		List<String> ret = new ArrayList<String>();
		for (String str : formulas) {
			if (!ret.contains(str)) {
				ret.add(str);
			}
		}
		return ret;
	}

	private static List<String> changeFormulas(List<String> formulas) {
		List<String> ret = new ArrayList<String>();
		for (String formula: formulas) {
			ret.add(changeFormula(formula));
		}
		return ret;
	}

	private static String changeFormula(String formula) {
		formula = makeSimpleChanges(formula);
		formula = changeXXXorYYY(formula);
		formula = changeRanges(formula);
		return formula;
		
	}

	private static String changeRanges(String formula) {
		Matcher matcher = rangePattern.matcher(formula);
		while (matcher.find()) {
			String subformula = matcher.group();
			String newSubformula = subformula;
			String kovaOrUnderscore = newSubformula.substring(1,2);
			newSubformula = newSubformula.replace("{", ""); 
			newSubformula = newSubformula.replace("}", "");
			newSubformula = newSubformula.replace(kovaOrUnderscore, "");
			newSubformula = newSubformula.replace(",", kovaOrUnderscore);
			formula = formula.replaceAll(Pattern.quote(subformula), newSubformula);
		}
		return formula;
	}

	private static String changeXXXorYYY(String formula) {
		Matcher matcher = XXXorYYYPattern.matcher(formula);
		while (matcher.find()) {
			String subformula = matcher.group();
			String newSubformula = subformula;
			newSubformula = newSubformula.replace("{", "");
			newSubformula = newSubformula.replace("}", "");
			newSubformula = newSubformula.replace("^", "");
			formula = formula.replaceAll(Pattern.quote(subformula), newSubformula);
		}
		return formula;
	}

	private static String makeSimpleChanges(String formula) {
		Set<String> replacements = thingsToReplace.keySet();
		for(String str : replacements) {
			formula = formula.replaceAll(str, thingsToReplace.get(str));
		}
		return formula;
	}

	private static List<String> getAllMathFormulas(String texData) {
		List<String> ret = new ArrayList<String>();
		Matcher matcher = mathFormulaPattern.matcher(texData);
		while (matcher.find()) {
			String formula = matcher.group();
			//get rid of the $s
			formula = formula.substring(1, formula.length()-1);
			ret.add(formula);
		}
		return ret;
	}

}

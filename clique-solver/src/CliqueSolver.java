import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class CliqueSolver {
	public static void main(String[] args) throws ContradictionException, TimeoutException, IOException {
		CliqueSolver cliqueSolver = new CliqueSolver(null);
		String inputDotPath = args[0];
		int k = Integer.parseInt(args[1]);
		dotToDot(inputDotPath,k);
		
	}

	
	public void dotToDot(String inputDotPath, int k) {
		String outputPath = "cliqued_" + inputDotPath;
		dotToDot(inputDotPath, outputDotPath,k);
	}
	public void dotToDot(String inputDotPath, String outputDotPath,int k) {
		UndirectedGraph<String,DefaultEdge> g = cliqueSolver.fromDot(inputDotPath);
		Set<String> clique = cliqueSolver.getClique(g,k);
		toDot(g, outputFilePath, clique);
	}
	private static UndirectedGraph<String, DefaultEdge> createStringDGraph()
	{
		UndirectedGraph<String, DefaultEdge> g =
			new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

		String one = ("1");
		String two = ("2");
		String three = ("3");
		String four = ("4");
		String five = ("5");

		// add the vertices
		g.addVertex(one);
		g.addVertex(two);
		g.addVertex(three);
		g.addVertex(four);
		g.addVertex(five);

		// add edges to create linking structure
		g.addEdge(one, two);
		g.addEdge(two, three);
		g.addEdge(three, four);
		g.addEdge(four, one);
		g.addEdge(four, two);
		g.addEdge(one, three);
		g.addEdge(four, five);
		g.addEdge(five, one);


		return g;
	}

	private String satSolverName;

	public CliqueSolver(String satSolverName) {
		super();
		this.satSolverName = satSolverName;
	}

	public boolean containsClique(UndirectedGraph<String, DefaultEdge> g, int k) throws ContradictionException, TimeoutException {
		Formula formula = reduction(g, k);
		return checkSat(formula, g, k);
	}

	public UndirectedGraph<String, DefaultEdge> generateRandomGraph(int numOfVertices, int p, int q) {
		Random randomGenerator = new Random();
		UndirectedGraph<String, DefaultEdge> g =
			new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		//generate vertices
		for(int i=1; i<=numOfVertices; i++) {
			g.addVertex(new Integer(i).toString());
		}
		//generate edges
		for (int i=1; i<=numOfVertices; i++) {
			for (int j=1; j<i; j++) {
				if (coinToss(randomGenerator, p, q)) {
					g.addEdge(new Integer(i).toString(), new Integer(j).toString());
				}
			}
		}
		return g;
	}

	public Set<String> getClique(UndirectedGraph<String, DefaultEdge> g, int k) throws ContradictionException, TimeoutException {
		Formula formula = reduction(g, k);
		return getModel(formula, g, k, 90, SolverFactory.newDefault());
	}

	public String getSatSolverName() {
		return satSolverName;
	}

	public void setSatSolverName(String satSolverName) {
		this.satSolverName = satSolverName;
	}


	public void stats(int maxNumberOfVertices, int timeout) throws ContradictionException, TimeoutException, IOException {
		List<ResultEntry> entries = new ArrayList<ResultEntry>();
		//init
		int p = 8;
		int q = 10;
		Date dateStart;
		Date dateEnd;
		Set<String> excludedSolvers = new TreeSet<String>();
		excludedSolvers.add("newMiniLearning"); 
		excludedSolvers.add("newMiniLearningHeap");
		excludedSolvers.add("newMiniSATHeap");
		String[] allSolvers = SolverFactory.instance().solverNames();
		Set<String> relevantSolvers = removeExcludedSolversFromList(allSolvers, excludedSolvers);
		//main loop
		for (int n=10; n<=maxNumberOfVertices; n+=10) {
			for (int j=1; j<=p; j++) {
				UndirectedGraph<String, DefaultEdge> g = generateRandomGraph(n, j, q);
				int numOfEdges = g.edgeSet().size();
				for (int k=3; k<n*3/4; k++) {
					//my algorithms
					for (String solverName : relevantSolvers) {
						dateStart = new Date();
						Formula formula = reduction(g, k);
						boolean myResult = checkSat(formula, g, k, timeout, SolverFactory.instance().createSolverByName(solverName));
						dateEnd = new Date();
						long myIntervalInMillis = dateEnd.getTime() - dateStart.getTime();
						int variables = formula.getNumberOfVariables();
						Map<IR,Integer> map = createMap(g, k); 
						int numberOfVars = max(map.values());
						if (numberOfVars != variables) {
							throw new IllegalStateException();
						}
						int clauses = formula.getNumberOfClauses();
						entries.add(new ResultEntry(n, numOfEdges, k, solverName, myResult, variables, clauses, myIntervalInMillis, myIntervalInMillis < timeout));
					}

					//their algorithm
					dateStart = new Date();
					Collection<Set<String>> maximalCliques = new BronKerboschCliqueFinder<String, DefaultEdge>(g).getAllMaximalCliques();
					dateEnd = new Date();
					long theirIntervalInMillis = dateEnd.getTime() - dateStart.getTime();
					Set<String> someClique = maximalCliques.iterator().next();
					boolean theirResult = someClique.size()>= k;
					ResultEntry entry = new ResultEntry(n, numOfEdges, k, "Bron", theirResult, 0, 0, theirIntervalInMillis, true);
					entries.add(entry);
					System.out.println(entry);
					
					//write to disc
				}
			}
				Utils.writeStringToFile(new File("c:\\Users\\yoni\\Dropbox\\stats.xls"), toExcelString(entries));
		}
		Utils.writeStringToFile(new File("c:\\Users\\yoni\\Dropbox\\stats.xls"), toExcelString(entries));
	}

	public void toDot(UndirectedGraph<String, DefaultEdge> g, String filePath) throws IOException {
		StringNameProvider<String> snp = new StringNameProvider<String>();
		StringEdgeNameProvider<DefaultEdge> senp = new StringEdgeNameProvider<DefaultEdge>();
		DOTExporter<String, DefaultEdge> dotExportet = new DOTExporter<String, DefaultEdge>(snp, snp, null);
		FileWriter fw = new FileWriter(filePath);
		dotExportet.export(fw, g);
		fw.close();
	}



	public void toDot(UndirectedGraph<String, DefaultEdge> g, String filePath, Set<String> clique) throws IOException {
		toDot(g, filePath);
		String dotContent = Utils.getFileAsString(filePath);
		String coloredDotContent = colorDot(dotContent, clique);
		Utils.writeStringToFile(new File(filePath), coloredDotContent);
	}

	private boolean checkSat(Formula formula, UndirectedGraph<String, DefaultEdge> g, int k) throws ContradictionException, TimeoutException {
		return checkSat(formula, g, k, 90);
	}

	private boolean checkSat(Formula formula, UndirectedGraph<String, DefaultEdge> g, int k, int timeout) throws ContradictionException, TimeoutException {
		return checkSat(formula, g, k, timeout, SolverFactory.newDefault());
	}

	private boolean checkSat(Formula formula, UndirectedGraph<String, DefaultEdge> g, int k, int timeout, ISolver solver) throws ContradictionException, TimeoutException {
		return getModel(formula, g, k, timeout, solver) != null;
	}
	
	private boolean checkSat(Formula formula, UndirectedGraph<String, DefaultEdge> g, int k, ISolver solver) throws ContradictionException, TimeoutException {
		return checkSat(formula, g, k, 90, solver);
	}
	
	private boolean coinToss(Random randomGenerator, int p, int q) {
		return (randomGenerator.nextInt(q) <= p);
	}
	
	private String colorDot(String dotContent, Set<String> clique) {
		String ret = dotContent;
		if (clique != null) {
			for (String vertexName : clique) {
				String stringToReplace = "label=\"" + vertexName + "\" ];";
				String replacement = "label=\"" + vertexName + "\" ][color=red];";
				ret = ret.replaceAll(stringToReplace, replacement);
			}
		}
		return ret;
	}

	private int[] convertClauseToIntArray(Clause clause, Map<IR, Integer> map) {
		int[] ret = new int[clause.getClauseSize()];
		for (int i=0; i<ret.length; i++) {
			int absoluteValue = map.get(clause.getLiteral(i).getIr()).intValue(); 
			int sign = clause.getLiteral(i).getSign() ? 1 : -1;
			ret[i] = absoluteValue * sign;
		}
		return ret;
	}

	//	public DirectedGraph<Integer, DefaultEdge> getRandomGraph(int vertices, int edges) {
	//		RandomGraphGenerator<Integer, DefaultEdge> rgg = new RandomGraphGenerator<Integer, DefaultEdge>(
	//				vertices, edges);
	//		DirectedGraph<Integer, DefaultEdge> directedGraph = new SimpleDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class) ;
	//		VertexFactory<Integer> vertexFactory = new ClassBasedVertexFactory<Integer>(Integer.class);
	//		for (int i=0; i<vertices; i++) {
	//			Integer v = vertexFactory.createVertex();
	//			directedGraph.addVertex(v);
	//			System.out.println(v);
	//		}
	//		//jrgg.generateGraph(directedGraph, vertexFactory, null);
	//		return directedGraph;
	//	}

	private Map<IR, Integer> createMap(UndirectedGraph<String, DefaultEdge> g,
			int k) {
		Map<IR, Integer> ret = new TreeMap<IR, Integer>();
		Set<String> vertices = g.vertexSet();
		int counter = 1;
		for (String vertex : vertices) {
			for (int r=1; r<=k; r++) {
				int vertexNumber = Integer.parseInt(vertex);
				IR ir = new IR(vertexNumber,r);
				ret.put(ir, new Integer(counter));
				counter++;
			}
		}
		return ret;
	}

	private UndirectedGraph<String, DefaultEdge> fromDot(String path) throws IOException {
		List<String> dotData = Utils.getFileAsListOfLines(path);
		UndirectedGraph<String, DefaultEdge> ret = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		dotData = keepOnlyEdges(dotData);
		for (String line : dotData) {
			String v1 = getSource(line);
			String v2 = getDestination(line);
			if (!ret.containsVertex(v1)) {
				ret.addVertex(v1);
			}
			if (!ret.vertexSet().contains(v2)) {
				ret.addVertex(v2);
			}
			if (!ret.containsEdge(v1, v2)) {
				ret.addEdge(getVertex(ret,v1), getVertex(ret,v2));
			}
		}
		return ret;
	}

	private List<Clause> generateFirstCondition(
			UndirectedGraph<String, DefaultEdge> g, int k) {
		List<Clause> ret = new ArrayList<Clause>();
		for (int r=1; r<=k; r++) {
			Clause clasue = new Clause();
			for (String v : g.vertexSet()) {
				IR ir = new IR(Integer.parseInt(v), r);
				Literal literal = new Literal(ir, true);
				clasue.addLiteral(literal);
				ret.add(clasue);
			}
		}
		return ret;
	}

	private List<Clause> generateSecondCondition(
			UndirectedGraph<String, DefaultEdge> g, int k) {
		List<Clause> ret = new ArrayList<Clause>();
		for (int s=1; s<=k; s++) {
			for (int r=1; r<s; r++) {
				for (String v : g.vertexSet()) {
					IR ir1 = new IR(Integer.parseInt(v),s);
					IR ir2 = new IR(Integer.parseInt(v),r);
					Literal literal1 = new Literal(ir1, false);
					Literal literal2 = new Literal(ir2, false);
					Clause clause = new Clause();
					clause.addLiteral(literal1);
					clause.addLiteral(literal2);
					ret.add(clause);
				}
			}
		}
		return ret;
	}

	private List<Clause> generateThirdCondition(
			UndirectedGraph<String, DefaultEdge> g, int k) {
		List<Clause> ret = new ArrayList<Clause>();
		for (String u : g.vertexSet()) {
			for (String v: g.vertexSet()) {
				for (int s=1; s<=k; s++) {
					for (int r=1; r<=k; r++) {
						int yi = Integer.parseInt(u);
						int yj = Integer.parseInt(v);
						if (yi<yj && s != r && !g.containsEdge(u, v)) {
							Clause clause = new Clause();
							IR ir = new IR(yi, r);
							IR js = new IR(yj,s);
							Literal literal1 = new Literal(ir, false);
							Literal literal2 = new Literal(js, false);
							clause.addLiteral(literal1);
							clause.addLiteral(literal2);
							ret.add(clause);
						}
					}
				}
			}
		}
		return ret;
	}

	private String getDestination(String line) {
		int indexOfEndDashes = line.indexOf("--")+2;
		int indexOfEnd = min(line.indexOf(";"), line.length()-1);
		String afterDases = line.substring(indexOfEndDashes, indexOfEnd);
		String onlyDigits = removeNonDigits(afterDases);
		Integer destinationNumber = new Integer(onlyDigits);
		return destinationNumber.toString();
		
	}

	private IR getIRfromInteger(Map<IR, Integer> map, int i) {
		IR ret = null;
		Integer I = new Integer(i);
		for (Entry<IR,Integer> entry : map.entrySet()) {
			if (I.equals(entry.getValue())) {
				ret = entry.getKey();
				break;
			}
		}
		return ret;
	}

	private Set<String> getModel(Formula formula, UndirectedGraph<String, DefaultEdge> g, int k, int timeout, ISolver solver) throws ContradictionException, TimeoutException {
		try {
			Set<String> ret;
			Map<IR, Integer> map = createMap(g, k);
			int numberOfVars = max(map.values());
			solver.setTimeout(timeout);
			solver.newVar(numberOfVars); 
			solver.setExpectedNumberOfClauses(formula.getNumberOfClauses());
			for (int i=0; i<formula.getNumberOfClauses(); i++) {
				int [] clause = convertClauseToIntArray(formula.getClause(i), map);
				solver.addClause(new VecInt(clause));
			}
			IProblem problem = solver;
			if (problem.isSatisfiable()) {
				int [] model = problem.model();
				ret = transformIntArrayToVertexSet(model, map);
			} else {
				ret = null;
			}
			return ret;
		}
		catch (TimeoutException e) {
			return null;
		}
	}

	private String getSource(String line) {
		int indexOfDashes = line.indexOf("--");
		String beforeDashes = line.substring(0, indexOfDashes);
		String onlyDigits = removeNonDigits(beforeDashes);
		Integer sourceNumber = new Integer(onlyDigits);
		return sourceNumber.toString();
	}
	
	private String getVertex(UndirectedGraph<String, DefaultEdge> g, String v) {
		Set<String> vertexSet = g.vertexSet();
		for (String str : vertexSet) {
			if (str.equals(v)) {
				return str;
			}
		}
		return null;
	}

	private String getVertexName(Map<IR, Integer> map, int i) {
		IR ir = getIRfromInteger(map, i);
		return new Integer(ir.getI()).toString();
	}

	private List<String> keepOnlyEdges(List<String> dotData) {
		List <String> ret = new ArrayList<String>();
		for (String line : dotData) {
			if (line.contains("--")) {
				ret.add(line);
			}
		}
		return ret;
	}

	private int max(Collection<Integer> values) {
		int tmpMax = Integer.MIN_VALUE;
		for (Integer value : values) {
			if (tmpMax < value.intValue()) {
				tmpMax = value.intValue();
			}
		}
		return tmpMax;
	}

	private int min(int a, int b) {
		if (a <= b) {
			return a;
		} else {
			return b;
		}
	}

	private Formula reduction(UndirectedGraph<String, DefaultEdge> g, int k) {
		Formula formula = new Formula();
		List<Clause> firstCondition = generateFirstCondition(g,k);
		List<Clause> secondCondition = generateSecondCondition(g,k);
		List<Clause> thirdCondition = generateThirdCondition(g,k);
		formula.addClauses(firstCondition);
		formula.addClauses(secondCondition);
		formula.addClauses(thirdCondition);
		return formula;
	}

	private Set<String> removeExcludedSolversFromList(String[] allSolvers,
			Set<String> excludedSolvers) {
		Set<String> ret = new TreeSet<String>();
		for (String solverName : allSolvers) {
			if (!excludedSolvers.contains(solverName)) {
				ret.add(solverName);
			}
		}
		return ret;
	}

	private String removeNonDigits(String str) {
		String ret = str.replaceAll("[^0-9]", "");
		ret = ret.replaceAll("\\s", ""); 
		return ret;
	}

	private String toExcelString(List<ResultEntry> entries) {
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder.append("vertices" + "\t");
		stringBuilder.append("edges" + "\t");
		stringBuilder.append("k" + "\t");
		stringBuilder.append("solver name" + "\t");
		stringBuilder.append("result" + "\t");
		stringBuilder.append("variables" + "\t");
		stringBuilder.append("clauses" + "\t");
		stringBuilder.append("time" + "\t");
		stringBuilder.append("finished" + "\t");
		stringBuilder.append("\n");
		
		for (ResultEntry entry : entries) {
			stringBuilder.append(entry.toString());
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	private Set<String> transformIntArrayToVertexSet(int[] model, Map<IR,Integer> map) {
		Set<String> ret;
		if (model == null) {
			ret = null;
		} else {
			ret = new HashSet<String>();
			for (int i=0; i<model.length; i++) {
				if (model[i] > 0) {
					String vertexName = getVertexName(map, model[i]);
					ret.add(new Integer(vertexName).toString());
				}
			}
		}
		return ret;
	}
}

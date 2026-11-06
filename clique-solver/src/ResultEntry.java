
public class ResultEntry {
	private int n;
	private int numOfEdges;
	private int k;
	
	private String solverName;
	private boolean result;
	private int variables;
	private int clauses;
	private long time;
	private boolean finish;
	public ResultEntry(int n, int numOfEdges, int k, String solverName,
			boolean result, int variables, int clauses, long time, boolean finish) {
		super();
		this.n = n;
		this.numOfEdges = numOfEdges;
		this.k = k;
		this.solverName = solverName;
		this.result = result;
		this.variables = variables;
		this.clauses = clauses;
		this.time = time;
		this.finish = finish;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	public int getNumOfEdges() {
		return numOfEdges;
	}
	public void setNumOfEdges(int numOfEdges) {
		this.numOfEdges = numOfEdges;
	}
	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}
	public String getSolverName() {
		return solverName;
	}
	public void setSolverName(String solverName) {
		this.solverName = solverName;
	}
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public int getVariables() {
		return variables;
	}
	public void setVariables(int variables) {
		this.variables = variables;
	}
	public int getClauses() {
		return clauses;
	}
	public void setClauses(int clauses) {
		this.clauses = clauses;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public boolean getFinish() {
		return finish;
	}
	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("");
		stringBuilder.append(getN() + "\t");
		stringBuilder.append(getNumOfEdges() + "\t");
		stringBuilder.append(getK() + "\t");
		stringBuilder.append(getSolverName() + "\t");
		stringBuilder.append(getResult() + "\t");
		stringBuilder.append(getVariables() + "\t");
		stringBuilder.append(getClauses() + "\t");
		stringBuilder.append(getTime() + "\t");
		stringBuilder.append(getFinish());
		return stringBuilder.toString();
	}
	
	

}

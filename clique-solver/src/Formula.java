
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Formula {
	private List<Clause> clausesList;

	public Formula(List<Clause> clausesList) {
		super();
		this.clausesList = clausesList;
	}
	
	public Formula() {
		this(new ArrayList<Clause>());
	}


	public List<Clause> getClausesList() {
		return clausesList;
	}

	public void setClausesList(List<Clause> clausesList) {
		this.clausesList = clausesList;
	}
	
	public void addClause(Clause clause) {
		clausesList.add(clause);
	}
	
	public void addClauses(List<Clause> clauses) {
		clausesList.addAll(clauses);
	}
	public int getNumberOfClauses() {
		return clausesList.size();
	}
	
	public int getNumberOfVariables() {
		Set<IR> variables = new TreeSet<IR>();
		for (Clause clause : this.clausesList) {
			for (Literal literal : clause.getListLiteral()) {
				IR ir = literal.getIr();
				variables.add(ir);
			}
		}
		return variables.size();
	}
	public Clause getClause(int index) {
		return clausesList.get(index);
	}
}

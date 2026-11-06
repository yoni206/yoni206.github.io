
import java.util.ArrayList;
import java.util.List;


public class Clause {
	private List<Literal> listOfLiterals;

	public Clause(List<Literal> listOfLiterals ) {
		super();
		this.listOfLiterals = listOfLiterals;
	}
	
	public Clause() {
		this(new ArrayList<Literal>());
	}

	public List<Literal> getListLiteral() {
		return listOfLiterals;
	}

	public void setListLiteral(List<Literal> listIR) {
		this.listOfLiterals = listIR;
	}
	
	public void addLiteral(Literal ir) {
		listOfLiterals.add(ir);
	}
	
	public void addLiterals(List<Literal> irs) {
		listOfLiterals.addAll(irs);
	}
	
	public int getClauseSize() {
		return listOfLiterals.size();
	}
	
	public Literal getLiteral(int index) {
		return listOfLiterals.get(index);
	}
}

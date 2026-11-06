

public class Literal {
	private IR ir;
	private boolean sign;
	
	public Literal(IR ir, boolean sign) {
		super();
		this.ir = ir;
		this.sign = sign;
	}
	public IR getIr() {
		return ir;
	}
	public void setIr(IR ir) {
		this.ir = ir;
	}
	public boolean getSign() {
		return sign;
	}
	public void setSign(boolean sign) {
		this.sign = sign;
	}
	
	
	
}

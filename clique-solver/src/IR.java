

public class IR implements Comparable<IR>{
	private int i;
	private int r;
	
	public IR(int i, int r) {
		super();
		this.i = i;
		this.r = r;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}
	
	@Override
	public boolean equals(Object otherIr) {
		IR other = (IR) otherIr;
		return (this.i == other.getI() && this.r == other.getR());
	}

	@Override
	public int compareTo(IR o) {
		int ret=0;
		if (this.i == o.i && this.r == o.r) {
			ret = 0;
		}
		if (this.i == o.i && this.r < o.r) {
			ret = -1;
		}
		if (this.i == o.i && this.r > o.r) {
			ret = 1;
		}
		if (this.i > o.i) {
			ret = 1;
		}
		if (this.i < o.i) {
			ret = -1;
		}
		return ret;
	}

}

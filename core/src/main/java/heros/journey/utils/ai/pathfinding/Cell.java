package heros.journey.utils.ai.pathfinding;

public class Cell implements Comparable<Cell> {

	public int f, g, h;
	public int i;
	public int j;
	public int t;
	public Cell parent;

	public Cell(int i, int j, int h) {
		this.i = i;
		this.j = j;
		this.h = h;
		this.f = h;
	}

	@Override
	public String toString() {
		return "(" + this.i + ", " + this.j + ": {" + f + "," + g + "," + h + "," + t + "})";
	}

	@Override
	public boolean equals(Object o) {
		Cell obj = (Cell) o;
		return i == obj.i && j == obj.j;
	}

	@Override
	public int compareTo(Cell o) {
		return f - o.f == 0 ? h - o.h : f - o.f;
	}
}

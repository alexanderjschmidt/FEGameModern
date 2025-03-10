package heros.journey.utils.ai.pathfinding;

import java.util.PriorityQueue;

import heros.journey.entities.Entity;
import heros.journey.entities.EntityManager;
import heros.journey.initializers.base.Tiles;
import heros.journey.tilemap.TileMap;
import heros.journey.utils.RangeManager;

public class AStar {

	public static Cell aStar(int startI, int startJ, int endI, int endJ, TileMap tileMap) {
		PriorityQueue<Cell> open = new PriorityQueue<Cell>();

		Cell[][] grid = new Cell[tileMap.getWidth()][tileMap.getHeight()];
		boolean closed[][] = new boolean[tileMap.getWidth()][tileMap.getHeight()];

		for (int i = 0; i < tileMap.getWidth(); i++) {
			for (int j = 0; j < tileMap.getHeight(); j++) {
				grid[i][j] = new Cell(i, j, manhattanHeuristic(i, j, endI, endJ));
			}
		}

		// add the start location to open list.
		grid[startI][startJ].g = 0;
		grid[startI][startJ].f = manhattanHeuristic(startI, startJ, endI, endJ);
		open.add(grid[startI][startJ]);

		Cell current;

		while (!open.isEmpty()) {
			current = open.poll();
			closed[current.i][current.j] = true;

			if (current.equals(grid[endI][endJ])) {
				return current;
			}

			Cell target;
			if (current.i - 1 >= 0) {
				target = grid[current.i - 1][current.j];
				checkAndUpdateCost(current, target, open, closed, tileMap, endI, endJ);
			}
			if (current.j - 1 >= 0) {
				target = grid[current.i][current.j - 1];
				checkAndUpdateCost(current, target, open, closed, tileMap, endI, endJ);
			}
			if (current.j + 1 < grid[0].length) {
				target = grid[current.i][current.j + 1];
				checkAndUpdateCost(current, target, open, closed, tileMap, endI, endJ);
			}
			if (current.i + 1 < grid.length) {
				target = grid[current.i + 1][current.j];
				checkAndUpdateCost(current, target, open, closed, tileMap, endI, endJ);
			}
		}
		return null;
	}

	public static void printGrid(Cell[][] grid, TileMap tileMap) {
		for (int j = 0; j < tileMap.getHeight(); j++) {
			for (int i = 0; i < tileMap.getWidth(); i++) {
				System.out.print(grid[i][j].h + " | ");
			}
			System.out.println();
		}
	}

	private static void checkAndUpdateCost(Cell current, Cell target, PriorityQueue<Cell> open, boolean[][] closed, TileMap map, int endI, int endJ) {
		if (target == null || closed[target.i][target.j])
			return;

		target.t = getPathCost(map, target.i, target.j);
		int tent = current.g + target.t;
		if (!open.contains(target)) {
			target.parent = current;
			target.g = tent;
			target.f = target.g + manhattanHeuristic(target.i, target.j, endI, endJ);
			open.add(target);
			return;
		} else if (tent >= target.g) {
			return;
		}
		target.parent = current;
		target.g = tent;
		target.f = target.g + manhattanHeuristic(target.i, target.j, endI, endJ);
	}

	private static int manhattanHeuristic(int i, int j, int endI, int endJ) {
		return manhattanDist(i, j, endI, endJ);
	}

	public static int manhattanDist(int i, int j, int endI, int endJ) {
		int dx = Math.abs(i - endI);
		int dy = Math.abs(j - endJ);
		return (int) ((dx + dy));
	}

	private static int getPathCost(TileMap map, int i, int j) {
		return map.get(i, j) == Tiles.PATH ? 1 : ((map.getTerrain(i, j).getTerrainCost() + (map.getEnvironment(i, j) != null ? map.getEnvironment(i, j).getTerrainCost() : 0)) * 5);
	}

	public static Cell aStarEntity(int dist1, RangeManager.RangeColor[][] range, int startI, int startJ, int endI, int endJ, TileMap mapRenderer, Entity selected) {
		PriorityQueue<Cell> open = new PriorityQueue<Cell>();
		int dist = dist1 + 1;
		Cell[][] grid = new Cell[range.length][range[0].length];
		boolean closed[][] = new boolean[range.length][range[0].length];

		for (int i = 0; i < range.length; i++) {
			for (int j = 0; j < range[0].length; j++) {
				grid[i][j] = new Cell(i, j, manhattanDist(i, j, endI, endJ));
			}
		}

		// add the start location to open list.
		grid[startI][startJ].g = 0;
		grid[startI][startJ].f = manhattanDist(startI, startJ, endI, endJ);
		open.add(grid[startI][startJ]);

		Cell current;
		// System.out.println(selected);
		// System.out.println(startI + ", " + startJ);
		// System.out.println(endI + ", " + endJ);
		// System.out.println(dist1 + " : " + dist);

		while (!open.isEmpty()) {
			current = open.poll();
			closed[current.i][current.j] = true;
			// System.out.println(current.i + ", " + current.j + ": " + current.g);
			if (current.equals(grid[endI][endJ]) || current.g == dist) {
				// System.out.println("finished");
				return current;
			}

			Cell t;
			if (current.i - 1 >= 0) {
				t = grid[current.i - 1][current.j];
				checkAndUpdateCostEntity(current, t, open, closed, dist, range, mapRenderer, selected, endI, endJ);
			}
			if (current.j - 1 >= 0) {
				t = grid[current.i][current.j - 1];
				checkAndUpdateCostEntity(current, t, open, closed, dist, range, mapRenderer, selected, endI, endJ);
			}
			if (current.j + 1 < grid[0].length) {
				t = grid[current.i][current.j + 1];
				checkAndUpdateCostEntity(current, t, open, closed, dist, range, mapRenderer, selected, endI, endJ);
			}
			if (current.i + 1 < grid.length) {
				t = grid[current.i + 1][current.j];
				checkAndUpdateCostEntity(current, t, open, closed, dist, range, mapRenderer, selected, endI, endJ);
			}
		}
		return null;
	}

	private static void checkAndUpdateCostEntity(Cell current, Cell t, PriorityQueue<Cell> open, boolean[][] closed, int dist, RangeManager.RangeColor[][] range, TileMap mapRenderer, Entity selected, int endI,
                                                 int endJ) {
		if (t == null || closed[t.i][t.j] || range[t.i][t.j] != RangeManager.RangeColor.BLUE)
			return;

		int tent = current.g + 1;
		if (tent > dist) {
			return;
		}
		if (!open.contains(t)) {
			open.add(t);
		} else if (tent >= t.g) {
			return;
		}
		t.parent = current;
		t.g = tent;
		t.f = t.g + manhattanDist(t.i, t.j, endI, endJ);
	}

	public static Cell reversePath(Cell node) {
		Cell prev = null;
		Cell current = node;
		Cell next = null;
		while (current != null) {
			next = current.parent;
			current.parent = prev;
			prev = current;
			current = next;
		}
		node = prev;
		return node;
	}

	public static Cell aStarAI(int dist, RangeManager.RangeColor[][] range, int startI, int startJ, int endI, int endJ, TileMap map, Entity selected) {
		PriorityQueue<Cell> open = new PriorityQueue<Cell>();

		Cell[][] grid = new Cell[range.length][range[0].length];
		boolean closed[][] = new boolean[range.length][range[0].length];

		for (int i = 0; i < range.length; i++) {
			for (int j = 0; j < range[0].length; j++) {
				grid[i][j] = new Cell(i, j, manhattanDist(i, j, endI, endJ));
			}
		}

		// add the start location to open list.
		grid[startI][startJ].g = 0;
		grid[startI][startJ].f = manhattanDist(startI, startJ, endI, endJ);
		open.add(grid[startI][startJ]);

		Cell current;

		while (!open.isEmpty()) {
			current = open.poll();
			closed[current.i][current.j] = true;

			if (current.equals(grid[endI][endJ])) {
				return current;
			}

			if (current.i - 1 >= 0) {
				checkAndUpdateCostEntity(current, grid[current.i - 1][current.j], open, closed, dist, range, map, selected, endI, endJ);
			}
			if (current.j - 1 >= 0) {
				checkAndUpdateCostEntity(current, grid[current.i][current.j - 1], open, closed, dist, range, map, selected, endI, endJ);
			}
			if (current.j + 1 < grid[0].length) {
				checkAndUpdateCostEntity(current, grid[current.i][current.j + 1], open, closed, dist, range, map, selected, endI, endJ);
			}
			if (current.i + 1 < grid.length) {
				checkAndUpdateCostEntity(current, grid[current.i + 1][current.j], open, closed, dist, range, map, selected, endI, endJ);
			}
		}
		return null;
	}

	public static Cell aStarAILong(int[][] range, int startI, int startJ, int endI, int endJ, TileMap map, Entity selected) {
		PriorityQueue<Cell> open = new PriorityQueue<Cell>();

		Cell[][] grid = new Cell[range.length][range[0].length];
		boolean closed[][] = new boolean[range.length][range[0].length];
		for (int i = 0; i < range.length; i++) {
			for (int j = 0; j < range[0].length; j++) {
				grid[i][j] = new Cell(i, j, manhattanDist(i, j, endI, endJ));
			}
		}

		// add the start location to open list.
		grid[startI][startJ].g = 0;
		grid[startI][startJ].f = manhattanDist(startI, startJ, endI, endJ);
		open.add(grid[startI][startJ]);

		Cell current;

		while (!open.isEmpty()) {
			current = open.poll();
			closed[current.i][current.j] = true;

			if (current.equals(grid[endI][endJ])) {
				return current;
			}

			Cell t;
			if (current.i - 1 >= 0) {
				t = grid[current.i - 1][current.j];
				checkAndUpdateCostLong(current, t, open, closed, map, selected, endI, endJ);
			}
			if (current.j - 1 >= 0) {
				t = grid[current.i][current.j - 1];
				checkAndUpdateCostLong(current, t, open, closed, map, selected, endI, endJ);
			}
			if (current.j + 1 < grid[0].length) {
				t = grid[current.i][current.j + 1];
				checkAndUpdateCostLong(current, t, open, closed, map, selected, endI, endJ);
			}
			if (current.i + 1 < grid.length) {
				t = grid[current.i + 1][current.j];
				checkAndUpdateCostLong(current, t, open, closed, map, selected, endI, endJ);
			}
		}
		return new Cell(startI, startJ, manhattanDist(startI, startJ, endI, endJ));
	}

	private static void checkAndUpdateCostLong(Cell current, Cell t, PriorityQueue<Cell> open, boolean[][] closed, TileMap map, Entity selected, int endI, int endJ) {
		if (t == null || closed[t.i][t.j])
			return;

		int tent = current.g + 1;
		if (!open.contains(t)) {
			open.add(t);
		} else if (tent >= t.g) {
			return;
		}
		t.parent = current;
		t.g = tent;
		t.f = t.g + manhattanDist(t.i, t.j, endI, endJ);
	}

	public static Cell prune(Cell path, EntityManager entities, Entity e) {
		Cell p = path;
		Cell[] temp = new Cell[e.getMoveDistance()];
		for (int i = 0; i < temp.length && p != null; i++) {
			temp[i] = p;
			p = p.parent;
		}

		for (int i = temp.length - 1; i >= 0; i--) {
			if (temp[i] != null && (entities.get(temp[i].i, temp[i].j) != null && entities.get(temp[i].i, temp[i].j) != e)) {
				temp[i] = null;
			} else if (temp[i] != null) {
				temp[i].parent = null;
				break;
			}
		}
		return temp[0];
	}

	public static void printPath(Cell path) {
		while (path != null) {
			System.out.print(path);
			path = path.parent;
		}
		System.out.println();
	}
}

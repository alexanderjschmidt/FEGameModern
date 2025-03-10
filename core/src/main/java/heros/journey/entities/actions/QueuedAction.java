package heros.journey.entities.actions;

import heros.journey.utils.ai.pathfinding.Cell;

public class QueuedAction {

	private Cell path;
	private Action action;
	private int targetX, targetY;
	public String information = "";

	public QueuedAction(Cell path, Action action, int targetX, int targetY) {
		this.path = path;
		this.action = action;
		this.targetX = targetX;
		this.targetY = targetY;
	}

	public Cell getPath() {
		return path;
	}

	public Action getAction() {
		return action;
	}

	public int getTargetX() {
		return targetX;
	}

	public int getTargetY() {
		return targetY;
	}

	public String toString() {
		return action + " " + targetX + ", " + targetY;
	}

}

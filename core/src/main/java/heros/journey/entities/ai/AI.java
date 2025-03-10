package heros.journey.entities.ai;

import heros.journey.GameState;
import heros.journey.entities.Entity;
import heros.journey.entities.actions.QueuedAction;

public interface AI {

	public QueuedAction getMove(GameState gameState, Entity playingEntity);

}

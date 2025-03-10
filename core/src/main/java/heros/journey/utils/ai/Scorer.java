package heros.journey.utils.ai;

import heros.journey.GameState;
import heros.journey.entities.Entity;
import heros.journey.entities.actions.QueuedAction;

import java.util.List;

public interface Scorer {

    public List<QueuedAction> getPossibleQueuedActions(GameState gameState);

    public int getScore(GameState gameState, Entity playingEntity);

}

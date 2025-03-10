package heros.journey.entities.ai;

import heros.journey.GameState;
import heros.journey.entities.Entity;
import heros.journey.entities.actions.QueuedAction;

public class PlayerAI implements AI {

    private final AI aiForPredicting;

    public PlayerAI(AI aiForPredicting){
        this.aiForPredicting = aiForPredicting;
    }

    @Override
    public QueuedAction getMove(GameState gameState, Entity e) {
        if (gameState == GameState.global())
            return null;
        else
            return aiForPredicting.getMove(gameState, e);
    }
}

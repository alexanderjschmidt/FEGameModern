package heros.journey.entities.ai;

import java.util.ArrayList;
import java.util.List;

import heros.journey.GameState;
import heros.journey.entities.Entity;
import heros.journey.entities.actions.QueuedAction;
import heros.journey.initializers.base.Actions;
import heros.journey.utils.ai.MCTS;
import heros.journey.utils.ai.Scorer;
import heros.journey.utils.ai.pathfinding.Cell;

public class MCTSAI implements AI, Scorer {

    private MCTS mcts;

    public MCTSAI(){
        mcts = new MCTS();
    }

    @Override
    public QueuedAction getMove(GameState gameState, Entity playingEntity) {
        return mcts.runMCTS(gameState.clone(), playingEntity, this);
    }

    @Override
    public List<QueuedAction> getPossibleQueuedActions(GameState gameState) {
        List<QueuedAction> possibleActions = new ArrayList<>();
        Entity playingEntity = gameState.getEntities().getCurrentEntity();
        Cell target = new Cell(playingEntity.getXCoord(), playingEntity.getYCoord() - 1, 1);
        Cell path = new Cell(playingEntity.getXCoord(), playingEntity.getYCoord(), 1);
        path.parent = target;
        if (playingEntity.getYCoord() > 0)
            possibleActions.add(new QueuedAction(path, Actions.wait,0,0));
        Cell path2 = new Cell(playingEntity.getXCoord(), playingEntity.getYCoord(), 1);
        possibleActions.add(new QueuedAction(path2, Actions.wait,0,0));
        return possibleActions;
    }

    @Override
    public int getScore(GameState gameState, Entity playingEntity) {
        return 1;
    }
}

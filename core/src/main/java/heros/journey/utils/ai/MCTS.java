package heros.journey.utils.ai;

import heros.journey.GameState;
import heros.journey.entities.Entity;
import heros.journey.entities.actions.QueuedAction;

public class MCTS {
    private static final int SIMULATIONS = 1000;
    private static final double EXPLORATION_FACTOR = Math.sqrt(2);

    public QueuedAction runMCTS(GameState gameState, Entity playingEntity, Scorer scorer) {
        Node root = new Node(gameState, null, scorer);
        for (int i = 0; i < SIMULATIONS; i++) {
            Node selectedNode = select(root);
            selectedNode = expand(selectedNode);
            double result = simulate(selectedNode, playingEntity);
            backpropagate(selectedNode, result);
        }
        return root.bestChildByVisits().getQueuedAction();
    }

    private Node select(Node node) {
        while (node.hasChildren()) {
            node = node.bestUCTChild();
        }
        return node;
    }

    private Node expand(Node node) {
        node.expand();
        if (node.hasChildren()) {
            node = node.getRandomUnvisitedChild();
        }
        return node;
    }

    private double simulate(Node node, Entity playingEntity) {
        return node.rollout(playingEntity); // Random game simulation
    }

    private void backpropagate(Node node, double result) {
        while (node != null) {
            node.updateStats(result);
            node = node.getParent();
        }
    }
}

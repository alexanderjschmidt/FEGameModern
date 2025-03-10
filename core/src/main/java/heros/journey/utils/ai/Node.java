package heros.journey.utils.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import heros.journey.GameState;
import heros.journey.Random;
import heros.journey.entities.Entity;
import heros.journey.entities.actions.QueuedAction;

public class Node {

    GameState gameState;
    Node parent;
    List<Node> children;
    private int visitCount;
    private double winScore;
    private QueuedAction queuedAction;
    private final Scorer scorer;

    public Node(GameState gameState, Node parent, Scorer scorer) {
        this.gameState = gameState;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.visitCount = 0;
        this.winScore = 0.0;
        this.scorer = scorer;
    }

    // Get all possible QueuedActions and create child nodes
    public void expand() {
        if (!children.isEmpty()) return; // Already expanded

        List<QueuedAction> possibleQueuedActions = scorer.getPossibleQueuedActions(gameState);
        for (QueuedAction QueuedAction : possibleQueuedActions) {
            GameState newState = gameState.clone().applyAction(QueuedAction); // Apply QueuedAction to get new state
            Node childNode = new Node(newState, this, scorer);
            childNode.setQueuedAction(QueuedAction);
            children.add(childNode);
        }
    }

    // Select the best child using UCT (Upper Confidence Bound for Trees)
    public Node bestUCTChild() {
        return children.stream()
            .max(Comparator.comparingDouble(this::uctValue))
            .orElseThrow(() -> new IllegalStateException("No children found"));
    }

    // Compute UCT value for a node
    private double uctValue(Node node) {
        if (node.visitCount == 0) return Double.MAX_VALUE; // Favor unexplored nodes
        double exploitation = node.winScore / node.visitCount;
        double exploration = Math.sqrt(2) * Math.sqrt(Math.log(this.visitCount) / node.visitCount);
        return exploitation + exploration;
    }

    // Get a random unexplored child
    public Node getRandomUnvisitedChild() {
        List<Node> unvisited = new ArrayList<>();
        for (Node child : children) {
            if (child.visitCount == 0) {
                unvisited.add(child);
            }
        }
        return unvisited.isEmpty() ? this : unvisited.get(Random.get().nextInt(unvisited.size()));
    }

    // Simulate a random game from this node and return a result
    public int rollout(Entity playingEntity) {
        GameState tempState = this.gameState.clone();
        int depth = 0;
        while (scorer.getScore(gameState, playingEntity) == 0 && depth < 5) {
            List<QueuedAction> QueuedActions = scorer.getPossibleQueuedActions(gameState);
            QueuedAction randomQueuedAction = QueuedActions.get(Random.get().nextInt(QueuedActions.size()));
            tempState = tempState.applyAction(randomQueuedAction);
            depth++;
        }
        return scorer.getScore(gameState, playingEntity); // 1.0 if AI wins, 0.0 if loss, 0.5 for draw
    }

    // Backpropagate the result up the tree
    public void updateStats(double result) {
        visitCount++;
        winScore += result;
        if (parent != null) {
            parent.updateStats(1 - result); // Switch result for opponent
        }
    }

    // Get the most visited child (best QueuedAction selection)
    public Node bestChildByVisits() {
        return children.stream()
            .max(Comparator.comparingInt(n -> n.visitCount))
            .orElseThrow(() -> new IllegalStateException("No children available"));
    }

    // Utility methods
    public boolean hasChildren() { return !children.isEmpty(); }
    public Node getParent() { return parent; }
    public void setQueuedAction(QueuedAction queuedAction) { this.queuedAction = queuedAction; }
    public QueuedAction getQueuedAction() { return queuedAction; }
}

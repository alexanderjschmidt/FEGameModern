package heros.journey.initializers.base;

import java.util.concurrent.Callable;

import com.badlogic.gdx.math.Vector2;

import heros.journey.entities.actions.ActionQueue;
import heros.journey.GameState;
import heros.journey.entities.Entity;
import heros.journey.entities.ai.MCTSAI;
import heros.journey.entities.ai.PlayerAI;
import heros.journey.entities.factions.Faction;
import heros.journey.initializers.InitializerInterface;
import heros.journey.tilemap.tiles.ActionTile;
import heros.journey.tilemap.tiles.Tile;
import heros.journey.utils.ai.pathfinding.AStar;
import heros.journey.utils.ai.pathfinding.Cell;
import heros.journey.utils.worldgen.CellularAutomata;
import heros.journey.utils.worldgen.MapGenerationEffect;
import heros.journey.utils.worldgen.NewMapManager;

public class Map implements InitializerInterface {

    static {
        Faction playerFaction = new Faction(ActionQueue.get().getID(), true);

        Entity player = new Entity(Classes.hero, new PlayerAI(new MCTSAI()));
        player.setMapPosition(16, 16);
        player.getInventory().add(Items.wood);
        player.getInventory().add(Items.ironOre);
        player.getFactions().add(playerFaction);

        Entity goblin = new Entity(Classes.goblin, new MCTSAI());
        goblin.setMapPosition(20, 20);

        NewMapManager.get().getStartingEntities().add(player);
        NewMapManager.get().getStartingEntities().add(goblin);

        // Generated Map
        new MapGenerationEffect() {
            @Override
            public void apply(GameState gameState) {
                int width = gameState.getWidth();

                Tile[][] tileMap = CellularAutomata.generateMap(width);
                ActionTile[][] environment = CellularAutomata.generateTrees(tileMap, width);
                gameState.getMap().setTileMap(tileMap);
                gameState.getMap().setEnvironment(environment);

                Callable<Void> housesTask = () -> {
                    // Simulate long-running noise generation task
                    genHouses(tileMap, environment);
                    return null;
                };
                timeout(housesTask, 2000);

                gameState.getMap().genFacingAndVariance();
            }
        };
    }

    private static final int numHouses = 15;
    private static final Vector2[] housePos = new Vector2[numHouses];
    private static int houseStart = 0;
    private static int houseEnd = 1;

    public static void genHouses(Tile[][] tileMap, ActionTile[][] environment) {
        houseStart = 0;
        houseEnd = 1;
        for (int i = 0; i < numHouses; i++) {
            while (true) {
                int x = (int) (Math.random() * tileMap.length);
                int y = (int) (Math.random() * tileMap[0].length);
                if (tileMap[x][y] == Tiles.PLAINS) {
                    housePos[i] = new Vector2(x, y);
                    environment[x][y] = Tiles.HOUSE;
                    break;
                }
            }
        }
        while (houseStart < numHouses) {
            genNextPath();
        }
    }

    private static void genNextPath() {
        Cell path = AStar.aStar((int) housePos[houseStart].x, (int) housePos[houseStart].y, (int) housePos[houseEnd].x, (int) housePos[houseEnd].y, GameState.global().getMap());
        while (path != null) {
            GameState.global().getMap().setTile(path.i, path.j, Tiles.PATH);
            path = path.parent;
        }
        houseEnd++;
        if (houseStart == houseEnd) {
            houseEnd++;
        }
        if (houseEnd >= numHouses) {
            houseEnd = 0;
            houseStart++;
        }
    }

}


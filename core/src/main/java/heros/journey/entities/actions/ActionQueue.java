package heros.journey.entities.actions;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import heros.journey.GameState;
import heros.journey.entities.Entity;
import heros.journey.tilemap.MapData;
import heros.journey.ui.HUD;
import heros.journey.ui.HUD.HUDState;
import heros.journey.utils.ai.pathfinding.Cell;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ActionQueue extends ArrayList<QueuedAction> {

	private boolean actionInProgress;

	private String id = String.valueOf(UUID.randomUUID());
	private static ActionQueue actionQueue;
	private GameState gameState;

	private Socket socket;
	private boolean gameCreator = true;

	public static ActionQueue get() {
		if (actionQueue == null)
			actionQueue = new ActionQueue();
		return actionQueue;
	}

	private ActionQueue() {
		this.gameState = GameState.global();
	}

	public MapData initSocket(boolean gameCreator) {
		return initSocket(0, 0, 0, false, gameCreator);
	}

	public MapData initSocket(int seed, int mapSize, int teamCount, boolean fogOfWar, boolean gameCreator) {
		final MapData mapData = new MapData(seed, mapSize, teamCount, fogOfWar);
		this.gameCreator = gameCreator;
		try {
			socket = IO.socket("http://35.174.146.31:3000");
			socket.connect();
		} catch (Exception e) {
			System.out.println(e);
		}
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {

			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					ActionQueue.get().setID(data.getString("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (gameCreator) {
					JSONObject gameInfo = new JSONObject();
					try {
						gameInfo.put("seed", seed);
						gameInfo.put("mapSize", mapSize);
						gameInfo.put("teamCount", teamCount);
						gameInfo.put("fogOfWar", fogOfWar);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					socket.emit("newGame", gameInfo);
				} else {
					socket.emit("findGame");
				}
			}
		}).on("joinGame", new Emitter.Listener() {// change to createGame
			@Override
			public void call(Object... args) {
				JSONObject gameInfo = (JSONObject) args[0];
				mapData.load(gameInfo);
			}
		}).on("startGame", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject players = (JSONObject) args[0];
				System.out.println(players.toString());
				JSONObject.getNames(players);
				for (int i = 0; i < JSONObject.getNames(players).length; i++) {

				}
				// TODO
				// startGame();
			}
		}).on("actionRecieve", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject action = (JSONObject) args[0];
				JSONArray xCoords, yCoords;
				Cell temp = null;
				Action skill = null;
				int targetX = 0, targetY = 0;
				try {
					xCoords = action.getJSONArray("xCoords");
					yCoords = action.getJSONArray("yCoords");
					for (int i = 0; i < xCoords.length(); i++) {
						Cell temp2 = temp;
						temp = new Cell(xCoords.getInt(i), yCoords.getInt(i), 1);
						temp.parent = temp2;
					}
					targetX = action.getInt("targetX");
					targetY = action.getInt("targetY");
					skill = ActionManager.getAction(action.getString("skill"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ActionQueue.get().addAction(new QueuedAction(temp, skill, targetX, targetY));
				ActionQueue.get().nextAction();
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject p = (JSONObject) args[0];
				String pid = "";
				try {
					pid = p.getString("id");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println(pid + " disconnected");
			}
		}).on("ping", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				socket.emit("pong");
			}
		});
		return mapData;
	}

    public void update(){
        if (actionInProgress)
            return;
        nextAction();
    }

	public void nextAction() {
		if (actionQueue.size() == 0) {
			// System.out.println("end of queue");
			return;
		}
		QueuedAction action = actionQueue.removeFirst();
        System.out.println("process action " + action);
		actionInProgress = true;
		Cell path = action.getPath();

		HUD.get().setState(HUDState.MOVING);
		Entity selected = gameState.getEntities().get(path.i, path.j);
		HUD.get().getCursor().setSelected(selected);
		selected.move(path, action.getAction(), action.getTargetX(), action.getTargetY());
        GameState.global().nextTurn();
	}

	public void addAction(QueuedAction action) {
		actionQueue.add(action);
	}

	public void endAction() {
		actionInProgress = false;
	}

	public boolean isQueueEmpty() {
		return actionQueue.size() == 0;
	}

	public boolean isActionInProgress() {
		return this.actionInProgress;
	}

	// TODO
	public void sendAction(JSONObject gameAction) {
		if (id != null && socket != null)
			socket.emit("actionSend", gameAction);
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public void dispose() {
		socket.disconnect();
	}

}

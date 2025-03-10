package heros.journey.utils.input;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;

import heros.journey.entities.actions.ActionQueue;
import heros.journey.GameCamera;
import heros.journey.GameState;
import heros.journey.Random;
import heros.journey.entities.actions.ActionManager;
import heros.journey.entities.actions.TargetAction;
import heros.journey.ui.Cursor;
import heros.journey.ui.HUD;
import heros.journey.ui.HUD.HUDState;
import heros.journey.utils.ai.pathfinding.AStar;
import heros.journey.utils.ai.pathfinding.Cell;
import heros.journey.utils.worldgen.NewMapManager;

public class InputManager {

	private Cursor cursor;
	private GameState gameState;

	private float holdElapsedX, holdElapsedY;

	public Cell pathHolder;

	private static InputManager inputManager;

	public static InputManager get() {
		if (inputManager == null)
			inputManager = new InputManager();
		return inputManager;
	}

	private InputManager() {
		cursor = HUD.get().getCursor();
		gameState = GameState.global();
	}

	private void savePath() {
		Cursor cursor = HUD.get().getCursor();
		Cell path = AStar.reversePath(cursor.getPath());
		Cell temp = null;
		Cell holder = null;
		while (path != null) {
			Cell temp2 = temp;
			Cell holder2 = holder;
			temp = new Cell(path.i, path.j, 1);
			temp.parent = temp2;
			holder = new Cell(path.i, path.j, 1);
			holder.parent = holder2;
			path = path.parent;
		}
		cursor.setPath(temp);
		if (holder == null) {
			if (cursor.getSelected() != null)
				holder = new Cell(cursor.getSelected().getXCoord(), cursor.getSelected().getYCoord(), 1);
			else
				holder = new Cell(cursor.x, cursor.y, 1);
		}
		pathHolder = holder;
	}

	public void update(float delta) {
		if (Gdx.input.isKeyJustPressed(KeyManager.DEVMODE)) {
			Options.MAP_BLEND = !Options.MAP_BLEND;
		}
		if (Gdx.input.isKeyJustPressed(KeyManager.RE_GEN_MAP)) {
            Random.get().setSeed((int) (Math.random() * 10000000));
            NewMapManager.get().initMapGeneration(GameState.global());
		}
		if (Gdx.input.isKeyJustPressed(KeyManager.SHOW_JOB_INFO)) {
			HUD.get().getEntityDetailedUI().update();
		}
        if (Gdx.input.isKeyJustPressed(KeyManager.ZOOM_IN)) {
            GameCamera.get().zoomIn();
        }
        if (Gdx.input.isKeyJustPressed(KeyManager.ZOOM_OUT)) {
            GameCamera.get().zoomOut();
        }
		switch (HUD.get().getState()) {
		case CURSOR_MOVE:
			updateMoveState(delta);
			break;
		case TARGET:
			updateTargetState(delta);
			break;
		case ACTION_SELECT:
			updateActionMenu();
			break;
		case LOCKED:
			updateFreeMove(delta);
			break;
		}
	}

	private void updateTargetState(float delta) {
		if (cursor.getSelected() != null) {
			if (cursor.getActiveSkill().shouldTargetEntity()) {
				if (Gdx.input.isKeyJustPressed(KeyManager.UP) || Gdx.input.isKeyJustPressed(KeyManager.RIGHT)) {
					gameState.getRangeManager().pointAtTarget(1);
					HUD.get().getCombatUI().setMessage(cursor.getActiveSkill().getUIMessage(gameState, cursor.getSelected(), cursor.x, cursor.y));
				} else if (Gdx.input.isKeyJustPressed(KeyManager.DOWN) || Gdx.input.isKeyJustPressed(KeyManager.LEFT)) {
					gameState.getRangeManager().pointAtTarget(-1);
					HUD.get().getCombatUI().setMessage(cursor.getActiveSkill().getUIMessage(gameState, cursor.getSelected(), cursor.x, cursor.y));
				}
			} else {
				updateFreeMove(delta);
			}
			if (Gdx.input.isKeyJustPressed(KeyManager.SELECT)) {
				sendAction();
				cursor.getActiveSkill().targetEffect(gameState, cursor.getSelected(), cursor.x, cursor.y);
				HUD.get().setState(HUDState.CURSOR_MOVE);
                GameState.global().nextTurn();
			} else if (Gdx.input.isKeyJustPressed(KeyManager.ESCAPE) || Gdx.input.isKeyJustPressed(KeyManager.BACK)) {
				cursor.revertAction();
			}
		}
	}

	private void updateMoveState(float delta) {
        if (Gdx.input.isKeyJustPressed(KeyManager.NEXT_CHARACTER)) {
            cursor.setPosition(GameState.global().getEntities().getCurrentEntity());
        }
		if ((Gdx.input.isKeyJustPressed(KeyManager.ESCAPE) || Gdx.input.isKeyJustPressed(KeyManager.BACK)) && cursor.getSelected() != null) {
			cursor.clearSelected();
		} else if (Gdx.input.isKeyJustPressed(KeyManager.ESCAPE) || Gdx.input.isKeyJustPressed(KeyManager.BACK)) {
			HUD.get().getActionMenu().open(ActionManager.getTeamActions(gameState, cursor.x, cursor.y));
		} else if (Gdx.input.isKeyJustPressed(KeyManager.SELECT)) {
			if (cursor.getSelected() != null) {
				savePath();
				cursor.moveSelected();
			} else if (cursor.getHover() != null) {
				cursor.setSelectedtoHover();
				HUD.get().select();
				if (cursor.getHover().getMoveDistance() == 0) {
					savePath();
					cursor.getSelected().openActionMenu();
				} else {
					gameState.getRangeManager().setMoveAndAttackRange(cursor.getSelected());
				}
			} else if (cursor.getHover() == null) {
				savePath();
				HUD.get().getActionMenu().open(ActionManager.getTeamActions(gameState, cursor.x, cursor.y));
			}
		}
		updateFreeMove(delta);
	}

	private void updateFreeMove(float delta) {
		if (Gdx.input.isKeyJustPressed(KeyManager.DOWN) && cursor.y > 0) {
			holdElapsedY = 0;
			cursor.y--;
		} else if (Gdx.input.isKeyJustPressed(KeyManager.UP) && cursor.y < gameState.getHeight() - 1) {
			holdElapsedY = 0;
			cursor.y++;
		}
		if (Gdx.input.isKeyJustPressed(KeyManager.LEFT) && cursor.x > 0) {
			holdElapsedX = 0;
			cursor.x--;
		} else if (Gdx.input.isKeyJustPressed(KeyManager.RIGHT) && cursor.x < gameState.getWidth() - 1) {
			holdElapsedX = 0;
			cursor.x++;
		}
		if (Gdx.input.isKeyPressed(KeyManager.DOWN) && cursor.y > 0) {
			holdElapsedY += delta;
			if (holdElapsedY >= .1) {
				holdElapsedY = 0;
				cursor.y--;
			}
		} else if (Gdx.input.isKeyPressed(KeyManager.UP) && cursor.y < gameState.getHeight() - 1) {
			holdElapsedY += delta;
			if (holdElapsedY >= .1) {
				holdElapsedY = 0;
				cursor.y++;
			}
		}
		if (Gdx.input.isKeyPressed(KeyManager.RIGHT) && cursor.x < gameState.getWidth() - 1) {
			holdElapsedX += delta;
			if (holdElapsedX >= .1) {
				holdElapsedX = 0;
				cursor.x++;
			}
		} else if (Gdx.input.isKeyPressed(KeyManager.LEFT) && cursor.x > 0) {
			holdElapsedX += delta;
			if (holdElapsedX >= .1) {
				holdElapsedX = 0;
				cursor.x--;
			}
		}
	}

	private void updateActionMenu() {
		if (Gdx.input.isKeyJustPressed(KeyManager.UP)) {
            HUD.get().getActionMenu().decrement();
		} else if (Gdx.input.isKeyJustPressed(KeyManager.DOWN)) {
            HUD.get().getActionMenu().increment();
		}
		if (Gdx.input.isKeyJustPressed(KeyManager.SELECT)) {
			if (!(HUD.get().getActionMenu().getSelected() instanceof TargetAction)) {
				sendAction();
			} else {
				HUD.get().getCursor().setActiveSkill((TargetAction) HUD.get().getActionMenu().getSelected());
			}
			HUD.get().getActionMenu().select();
		} else if (Gdx.input.isKeyJustPressed(KeyManager.ESCAPE) || Gdx.input.isKeyJustPressed(KeyManager.BACK)) {
			HUD.get().setState(HUDState.CURSOR_MOVE);
			cursor.revertSelectedPosition();
		}
	}

	private void sendAction() {
		Cell temp = pathHolder;
		if (temp == null) {
			temp = new Cell(cursor.x, cursor.y, 1);
		}
		List<Integer> xCoords = new ArrayList<Integer>();
		List<Integer> yCoords = new ArrayList<Integer>();
		while (temp != null) {
			xCoords.add(temp.i);
			yCoords.add(temp.j);
			// System.out.println(temp.i + ", " + temp.j);
			temp = temp.parent;
		}
		// System.out.println(HUD.get().getActionMenu().getSelected());
		// System.out.println(HUD.get().getCursor().x + ", " +
		// HUD.get().getCursor().y);
		pathHolder = null;
		JSONObject gameAction = new JSONObject();
		try {
			gameAction.put("skill", HUD.get().getActionMenu().getSelected());
			gameAction.put("targetX", cursor.x);
			gameAction.put("targetY", cursor.y);
			gameAction.put("xCoords", xCoords);
			gameAction.put("yCoords", yCoords);
		} catch (JSONException e) {
			System.out.println(e);
		}
		ActionQueue.get().sendAction(gameAction);
	}

}

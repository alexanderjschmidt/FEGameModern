package heros.journey.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import heros.journey.*;
import heros.journey.entities.actions.ActionQueue;
import heros.journey.tilemap.MapData;
import heros.journey.ui.HUD;
import heros.journey.utils.input.InputManager;
import heros.journey.utils.input.KeyManager;
import heros.journey.utils.worldgen.NewMapManager;

public class BattleScreen implements Screen {

	private Application app;
	private SpriteBatch batch;

	private MapData mapData;
	private boolean ready = false;

	// quickStart constructor
	public BattleScreen(Application app, boolean quickStart) {
		this.app = app;
		this.mapData = new MapData((int) (Math.random() * 10000000), 32, 2, false);
		startGame();
	}

	// server join game
	public BattleScreen(Application app) {
		this.app = app;
		this.mapData = ActionQueue.get().initSocket(false);
	}

	// server create game
	public BattleScreen(Application app, int seed, int mapSize, int armySize, int teamCount, boolean fogOfWar) {
		this.app = app;
		this.mapData = ActionQueue.get().initSocket((int) (Math.random() * 10000000), 16, 4, false, true);
	}

	public void startGame() {
		batch = app.getBatch();

		GameState.global().init(mapData);
        NewMapManager.get().initGameState(GameState.global());
        GameState.global().nextTurn();

		ready = true;
		//ActionQueue.get().checkLocked();
	}

	@Override
	public void render(float delta) {
		if (!ready) {
			if (Gdx.input.isKeyJustPressed(KeyManager.ESCAPE)) {
				app.setScreen(new MainMenuScreen(app));
			}
			return;
		}
		GameCamera.get().updateGameCamera();
		app.getViewport().setCamera(GameCamera.get());
		batch.setProjectionMatrix(GameCamera.get().combined);

		InputManager.get().update(delta);
		HUD.get().update(delta);

		batch.begin();
		GameState.global().render(batch, delta);
		HUD.get().getCursor().render(batch, delta);
		batch.end();

		HUD.get().draw();

        ActionQueue.get().update();
	}

	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
		if (!ready)
			return;
		HUD.get().resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		ActionQueue.get().dispose();
	}

	public Application getApp() {
		return app;
	}

}

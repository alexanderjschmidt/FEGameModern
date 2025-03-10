package heros.journey;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import heros.journey.utils.art.ResourceManager;
import heros.journey.screens.LoadingScreen;

/*
 * TODO List
 * Make TextureMaps not Enums?
 * fix far out zoom
 * force zoom if camera would go off screen
 * make camera not go beyond map
 * Menus
 *  reposition menus on window resize
 * 	Stats Screen
 * 		visuals
 * 		increment on level up
 * 	Inventory
 * 		Use Item (health potion)
 * 		Show Weight
 * 		Show Gold
 * 		Scrolling
 * 		Tabs
 *      Crafting??
 * 		Equipment
 * 			Equip
 * 	Wait time selector
 * Action on Town
 *  Quests
 * Fog of War
 * Add Day/Night Cycle (could be used to show the world itself is getting darker not day/night)
 * Add time
 * Saving
 * Map Generation
 * 	Generate Dungeons on world map
 * 	Improve road generation some towns roads loop too much
 * 	Improve map generation
 * 	Improve Wang tiles for transitions
 * 	Inner Town Generation
 * 	Inner Dungeon Generation
 * Enter Region
 * 	World Map/Region Map/Local Map ?
 * 	64/16/16
 * Sounds
 * 	Background music
 * 	Action Sounds
 *
 */
public class Application extends Game {
	public static final String TITLE = "Game";
	public static final float VERSION = .8f;

	private Viewport viewport;
	private SpriteBatch batch;

    private static Application app;

    public static Application get() {
        if (app == null)
            app = new Application();
        return app;
    }

    private Application(){
        super();
    }

	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		viewport = new StretchViewport(w, h, new OrthographicCamera());
		GameCamera.get().setToOrtho(false, w, h);
		viewport.apply();
		batch = new SpriteBatch();

		setScreen(new LoadingScreen(this));
	}

	public void setScreen(Screen screen) {
		super.setScreen(screen);
		GameCamera.get().position.x = Gdx.graphics.getWidth() / 2;
		GameCamera.get().position.y = Gdx.graphics.getHeight() / 2;
	}

	@Override
	public void dispose() {
		super.dispose();
		ResourceManager.get().dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		viewport.update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public Viewport getViewport() {
		return viewport;
	}

}

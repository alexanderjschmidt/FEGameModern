package heros.journey.ui;

import com.badlogic.gdx.graphics.g2d.Batch;

import heros.journey.GameState;

public class TurnUI extends UI {

	public TurnUI() {
        super(0, 0, 8, 1, true, false);
	}

	public void update() {

	}

	@Override
	public void drawUI(Batch batch, float parentAlpha) {
		GameState gameState = GameState.global();
        drawText(batch, "Day " + gameState.getTurn(), 0, 0);
	}

}

package heroes.journey.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import heroes.journey.GameState;
import heroes.journey.entities.actions.Action;
import heroes.journey.entities.actions.TargetAction;
import heroes.journey.utils.art.ResourceManager;

import java.util.List;

public class ActionMenu extends UI {

    private List<Action> options;
    private int selected = 0;

    public ActionMenu() {
        super(0, 0, 8, 1, true, true);
    }

    public void open(List<Action> options) {
        GameState.global().getRangeManager().clearRange();
        HUD.get().setState(HUD.HUDState.ACTION_SELECT);
        if (options.isEmpty()) {
            HUD.get().getCursor().clearSelected();
            HUD.get().setState(HUD.HUDState.CURSOR_MOVE);
            return;
        }
        this.options = options;
        selected = 0;
        options.get(selected).onHover(GameState.global(), HUD.get().getCursor().getSelected());
        this.setSize(8, options.size());
        this.setPosition(0, 0);
    }

    @Override
    public void update() {
    }

    @Override
    public void drawUI(Batch batch, float parentAlpha) {
        batch.draw(ResourceManager.get().select, getX() + (0.5f * HUD.FONT_SIZE),
            getY() + ((height - (selected + 0.5f)) * HUD.FONT_SIZE));
        for (int i = 0; i < options.size(); i++) {
            drawText(batch, options.get(i).toString(), 1, i);
        }
    }

    public void increment() {
        if (selected < options.size() - 1) {
            selected++;
            options.get(selected).onHover(GameState.global(), HUD.get().getCursor().getSelected());
        } else {
            selected = 0;
        }
    }

    public void decrement() {
        if (selected > 0) {
            selected--;
            options.get(selected).onHover(GameState.global(), HUD.get().getCursor().getSelected());
        } else {
            selected = options.size() - 1;
        }
    }

    public void select() {
        options.get(selected).onSelect(GameState.global(), HUD.get().getCursor().getSelected());
        if (!(options.get(selected) instanceof TargetAction) && HUD.get().getCursor().getSelected() != null) {
            GameState.global().nextTurn();
        }
    }

    public Action getSelected() {
        return options.get(selected);
    }

}

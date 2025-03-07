package heros.journey.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;

import heros.journey.ActionQueue;
import heros.journey.GameCamera;
import heros.journey.GameState;
import heros.journey.entities.actions.Action;
import heros.journey.entities.actions.TargetAction;
import heros.journey.initializers.BaseActions;
import heros.journey.utils.Direction;
import heros.journey.utils.pathfinding.Cell;

public class EntityActor extends Actor {

    private int x, y;

    private String render = "idleSOUTH";
    public Direction dir = Direction.SOUTH;
    protected float elapsedTime = 0;

    private Team team;

    private GameState gameState;
    public boolean used;

	private Cell path;
	private Action action;
	private int targetX, targetY;

	public EntityActor(int x, int y) {
		this.setPosition(x, y);
        setSelected("idleSOUTH");
	}

	public EntityActor(Team team, GameState gameState) {
		this(0, 0);
        this.team = team;
        this.gameState = gameState;
	}

	public void render(Batch batch, float deltaTime) {
		if (path != null)
			setSelected("walking");
		else
			setSelected("idle");

        this.act(deltaTime);
        elapsedTime += deltaTime;

        if (used) {
            batch.setColor(Color.GRAY);
        } else {
            batch.setColor(this.getColor());
        }
	}

	private void setSelected(String selected) {
		if (!this.render.equals(selected)) {
			elapsedTime = 0;
		}
		this.render = selected;
	}

	public void vibrate(float delay, EntityActor from) {
		// 0.2 seconds
		Vector2 v = dir.getDirVector();
		this.addAction(Actions.sequence(Actions.delay(delay), Actions.moveBy(5 * v.y, 5 * v.x, .05f, Interpolation.pow5In), Actions.moveBy(-10 * v.y, -10 * v.x, .05f, Interpolation.pow5),
				Actions.moveBy(10 * v.y, 10 * v.x, .05f, Interpolation.pow5), Actions.moveBy(-5 * v.y, -5 * v.x, .05f, Interpolation.pow5Out)));

	}

	public void lunge(float delay, EntityActor at) {
		// 0.4 seconds
		Vector2 v = dir.getDirVector();
		this.addAction(Actions.sequence(Actions.delay(delay), Actions.moveBy(15 * v.x, 15 * v.y, .2f, Interpolation.pow5In), Actions.moveBy(-15 * v.x, -15 * v.y, .2f, Interpolation.pow5Out)));
	}

	public void update(float delta) {
        final Entity e = (Entity) this;
		if (path != null && !this.hasActions()) {
            //TODO Make duration based on move speed
            this.addAction(Actions.sequence(Actions.moveTo(path.i - x, path.j - y, .2f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        gameState.getEntities().removeEntity(x, y);
                        x = path.i;
                        y = path.j;
                        gameState.getEntities().addEntity(e, e.getXCoord(), e.getYCoord());
                        e.setPosition(0, 0);
                        path = path.parent;
                        if (path == null) {
                            if (action == null) {
                                e.openActionMenu();
                            } else {
                                if (action instanceof TargetAction) {
                                    TargetAction s = (TargetAction) action;
                                    s.targetEffect(gameState, e, targetX, targetY);
                                } else {
                                    action.onSelect(gameState, e);
                                }
                                if (action.equals(BaseActions.wait)) {
                                    ActionQueue.get().endAction();
                                } else {
                                    Timer.schedule(new Timer.Task() {
                                        @Override
                                        public void run() {
                                            ActionQueue.get().endAction();
                                        }
                                    }, 1f);
                                }
                                ActionQueue.get().nextAction();
                                ActionQueue.get().checkLocked();
                            }
                        }
                    }
                })
            ));
		}
	}

	public void move(Cell p, Action action, int x2, int y2) {
		if (path != null) {
			return;
		}
		x = this.getXCoord();
		y = this.getYCoord();
		this.path = p;
		this.action = action;
		this.targetX = x2;
		this.targetY = y2;
	}

    public boolean remove() {
        gameState.getEntities().removeEntity(getXCoord(), getYCoord());
        return true;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getXCoord() {
        return x;
    }

    public int getYCoord() {
        return y;
    }

    public void setXCoord(int x) {
        this.x = x;
    }

    public void setYCoord(int y) {
        this.y = y;
    }

    public float getRenderX() {
        return (x + getX()) * GameCamera.get().getSize();
    }

    public float getRenderY() {
        return (y + getY()) * GameCamera.get().getSize();
    }

}

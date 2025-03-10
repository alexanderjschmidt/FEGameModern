package heros.journey.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import heros.journey.GameState;
import heros.journey.entities.actions.Action;
import heros.journey.entities.ai.AI;
import heros.journey.entities.buffs.Buff;
import heros.journey.entities.factions.Faction;
import heros.journey.entities.items.Inventory;
import heros.journey.entities.stats.Stats;
import heros.journey.ui.HUD;

public class Entity extends EntityActor {

	protected EntityClass job;
    private Stats stats;
    private Inventory inventory;
	private ArrayList<Buff> buffs;
    private ArrayList<Faction> factions;
    private AI ai;

	public Entity(int x, int y) {
		super(x, y);
	}

	public Entity(EntityClass job, AI ai) {
        super();
		this.job = job;
        this.ai = ai;
        this.stats = new Stats();
        this.inventory = new Inventory();
        buffs = new ArrayList<Buff>();
        factions = new ArrayList<Faction>();
	}

	public Entity clone(GameState newGameState) {
        Entity clone = new Entity(this.job, this.ai);
        clone.setXCoord(getXCoord());
        clone.setYCoord(getYCoord());
        clone.setPosition(getX(), getY());
        clone.buffs = new ArrayList<Buff>();
        for (Buff b : buffs) {
            clone.buffs.add(b.clone());
        }
        clone.factions = new ArrayList<Faction>();
        for (Faction f : factions) {
            clone.factions.add(f.clone());
        }
		clone.job = job;
        clone.stats = stats.clone();
        clone.inventory = inventory.clone();
		return clone;
	}

	public void render(Batch batch, float deltaTime, float buffTime) {
		render(batch, deltaTime);

        job.render(batch, getRenderX(), getRenderY(), elapsedTime);
		batch.setColor(Color.WHITE);
		renderBuffs(batch, buffTime);
	}

	private void renderBuffs(Batch batch, float buffTime) {
		ArrayList<Buff> activeBuffs = new ArrayList<Buff>();
		for (Buff buff : buffs) {
			if (buff.isActive())
				activeBuffs.add(buff);
		}
		if (activeBuffs.size() == 1) {
			activeBuffs.add(null);
		}
		if (!activeBuffs.isEmpty()) {
			if (activeBuffs.get((int) (buffTime % activeBuffs.size())) != null)
				activeBuffs.get((int) (buffTime % activeBuffs.size())).render(batch, getRenderX(), getRenderY());
		}
	}

    public void updateBuffs() {
        for (int i = 0; i < buffs.size(); i++) {
            if (buffs.get(i).update()) {
                buffs.remove(i--);
            }
        }
    }

    public EntityClass getEntityClass() {
        return job;
    }

    public Stats getStats() {
        return stats;
    }

    public Inventory getInventory() {
        return inventory;
    }

	public String toString() {
		return job.toString() + " (" + getXCoord() + ", " + getYCoord() + ")";
	}

    public void openActionMenu() {
        List<Action> options = getEntityClass().getActions().stream().filter(action -> action.requirementsMet(getGameState(), this)).collect(
            Collectors.toList());
        HUD.get().getActionMenu().open(options);
    }

	public ArrayList<Buff> getBuffs() {
		return buffs;
	}

    public int getMoveDistance() {
        return stats.getBody() + 4;
    }

    public int[] getRanges() {
        return new int[]{1, 2};
    }

    public int getSpeed() {
        return stats.getBody() * 2;
    }

    public AI getAI() {
        return ai;
    }

    public List<Faction> getFactions() {
        return factions;
    }

    public void addFaction(Faction faction) {
        if (factions.stream().anyMatch(Faction::isPlayerFaction))
            throw new RuntimeException("Entity already has a player faction, CANNOT have more than one");
        factions.add(faction);
    }
}

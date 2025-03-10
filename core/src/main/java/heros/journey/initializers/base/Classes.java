package heros.journey.initializers.base;

import heros.journey.entities.EntityClass;
import heros.journey.initializers.InitializerInterface;
import heros.journey.utils.art.ResourceManager;
import heros.journey.utils.art.TextureMaps;

public class Classes implements InitializerInterface {

    public static final String HERO = "Hero";
    public static EntityClass hero, goblin;

	static {
        hero = new EntityClass(HERO, "You", ResourceManager.get(TextureMaps.Sprites)[1][1]);
        goblin = new EntityClass("Goblin", "Goblin", ResourceManager.get(TextureMaps.Sprites)[8][2]);
	}

}

package heros.journey.initializers;

import heros.journey.entities.EntityClass;
import heros.journey.utils.art.ResourceManager;

public class BaseClass {

    public static final String HERO = "Hero";
    public static EntityClass hero;

	static {
        hero = new EntityClass(HERO, "You", ResourceManager.get().sprites[1][1], 10, 0, 0, new int[] {1}, 5, 5, 30);
	}

}

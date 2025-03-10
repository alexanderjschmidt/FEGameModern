package heros.journey.tilemap;

import org.json.JSONException;
import org.json.JSONObject;

public class MapData {

	private int seed, mapSize,  teamCount;

	private boolean fogOfWar;

	public MapData(int seed, int mapSize, int teamCount, boolean fogOfWar) {
		this.seed = seed;
		this.mapSize = mapSize;
		this.teamCount = teamCount;
		this.fogOfWar = fogOfWar;
	}

	public void load(JSONObject gameInfo) {
		try {
			seed = gameInfo.getInt("seed");
			mapSize = gameInfo.getInt("mapSize");
			teamCount = gameInfo.getInt("teamCount");
			fogOfWar = gameInfo.getBoolean("fogOfWar");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getSeed() {
		return seed;
	}

	public int getMapSize() {
		return mapSize;
	}

	public int getTeamCount() {
		return teamCount;
	}

	public boolean isFogOfWar() {
		return fogOfWar;
	}
}

package me.wilkosam.deathcore.data;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.List;

import static me.wilkosam.deathcore.GameManager.getCore;

public class ActiveGame extends YamlConfig {

	private final List<GameDeath> gameDeaths = new ArrayList<>();

	@Getter
	private MultiverseWorld gameWorld;

	/**
	 * Use when loading an existing active game file
	 */
	public ActiveGame() {

		// Loads previously saved data about the current active game
		this.loadConfiguration(NO_DEFAULT, "data/active-game.yml");

		this.save();
	}

	/**
	 * Use when starting a new active game with a new world
	 */
	public ActiveGame(MultiverseWorld world) {
		this.loadConfiguration(NO_DEFAULT, "data/active-game.yml");

		this.gameWorld = world;
		this.gameDeaths.clear();

		this.save();
	}

	public void registerDeath(Player player, DamageCause damageCause) {
		this.gameDeaths.add(new GameDeath(player, damageCause));

		this.save();
	}

	public int getTotalDeaths() {
		return this.gameDeaths.size();
	}

	@Override
	protected void onLoad() {
		if (isSet("game-deaths")) {
			List<SerializedMap> serializedMaps = getMapList("game-deaths");
			for (SerializedMap map : serializedMaps) {
				this.gameDeaths.add(GameDeath.deserialise(map));
			}
		}

		if (isSet("game-world"))
			this.gameWorld = getCore().getMVWorldManager().getMVWorld(getString("game-world"));
		else
			this.gameWorld = getCore().getMVWorldManager().getSpawnWorld();
	}

	@Override
	protected void onSave() {

		// Create a list of all game deaths serialized
		List<SerializedMap> serializedGameDeaths = new ArrayList<>();
		for (GameDeath gameDeath : this.gameDeaths)
			serializedGameDeaths.add(gameDeath.serialise());

		this.set("game-deaths", serializedGameDeaths);
		this.set("game-world", this.gameWorld.getName());
	}
}

package me.wilkosam.deathcore.data;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerCache extends YamlConfig {

	private static final Map<UUID, PlayerCache> CACHE_MAP = new HashMap<>();

	private final UUID uuid;
	private int totalDeaths = 0;

	private PlayerCache(UUID uuid) {
		this.uuid = uuid;

		this.loadConfiguration(NO_DEFAULT, "data/player-data/" + uuid.toString() + ".yml");
	}

	/**
	 * Retrieves a player's cache file from the disk
	 *
	 * @param player the player's whose data file to find
	 * @return the PlayerCache instance
	 */
	public static PlayerCache get(Player player) {

		// The player's UUID
		UUID uuid = player.getUniqueId();

		// Check if the player's file aleady exists in the map
		if (CACHE_MAP.containsKey(uuid))
			return CACHE_MAP.get(uuid);

		// Create new file or load existing file
		PlayerCache newCache = new PlayerCache(uuid);

		// Load it into the map
		CACHE_MAP.put(uuid, newCache);

		return newCache;
	}

	/**
	 * Removes a given player from the cache map but maintains their file.
	 *
	 * @param player the player to remove
	 */
	public static void unload(Player player) {
		CACHE_MAP.remove(player.getUniqueId());
	}

	@Override
	protected void onLoad() {
		if (isSet("total-deaths"))
			this.totalDeaths = getInteger("total-deaths");
		else
			this.totalDeaths = 0;

		this.save();
	}

	@Override
	protected void onSave() {
		set("total-deaths", String.valueOf(this.totalDeaths));
	}

	public void setTotalDeaths(int totalDeaths) {
		this.totalDeaths = totalDeaths;

		this.save();
	}
}
